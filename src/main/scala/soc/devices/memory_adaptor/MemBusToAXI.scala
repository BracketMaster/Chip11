package soc.devices.memory_adaptor
import spinal.lib.bus.amba4.axi.{
  Axi4Shared,
  Axi4SlaveFactory,
  Axi4Config,
  Axi4CrossbarFactory
}

import soc.interfaces.MemBus128
import cpu.interfaces.{LineRequest, LineResponse}
// import cpu.debug.debug_memory_adaptor
import cpu.shared.memory_state.{
  TransactionStatus,
  TransactionType,
  TransactionSize
}

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import scala.collection.concurrent.Debug

class MemBusToAXIShared(id: Int)(implicit axiConfig: Axi4Config)
    extends Component {
  val io = new Bundle {
    val membus = slave(new MemBus128)
    val axi = master(Axi4Shared(axiConfig))
  }
  assert(axiConfig.dataWidth == 128)
  val accessSize = UInt(3 bits)
  switch(io.membus.access_size) {
    is(TransactionSize.BYTE) { accessSize := 0 }
    is(TransactionSize.HALFWORD) { accessSize := 1 }
    is(TransactionSize.WORD) { accessSize := 2 }
    is(TransactionSize.DOUBLEWORD) { accessSize := 3 }
    is(TransactionSize.QUADWORD) { accessSize := 4 }
  }
  io.axi.arw.valid := False
  io.axi.r.ready := False
  io.axi.b.ready := False
  io.axi.w.valid := False
  //io.axi.w.valid.noCombLoopCheck

  io.axi.arw.size := accessSize
  io.axi.arw.payload.addr := io.membus.byte_address.resized
  io.axi.arw.write := False
  io.axi.arw.burst := 0
  io.axi.arw.len := 0
  io.axi.arw.id := id

  io.axi.w.last := False
  io.axi.w.payload.data := io.membus.write_data.asBits
  io.axi.w.strb := io.membus.write_mask.asBits

  io.membus.read_data := io.membus.read_data.getZero
  io.membus.status := TransactionStatus.WAITING
  io.membus.req_ack := False
  val write_data = Bits(128 bits)
  write_data := io.membus.write_data.asBits |>> 8 * io.membus.byte_address(
    3 downto 0
  )

  val fsm = new StateMachine {
    def doIdle {
      when(io.membus.ldst_req === TransactionType.LOAD) {
        io.axi.arw.write := False
        io.axi.arw.valid := True
        when(io.axi.arw.ready) {
          goto(stateLoad)
          io.membus.req_ack := True
        }
      }
      when(io.membus.ldst_req === TransactionType.STORE) {
        io.axi.arw.write := True
        io.axi.arw.valid := True
        io.axi.w.valid := True
        io.axi.w.last := True
        when(io.axi.arw.ready) {
          // goto(stateStoreData)
          goto(stateStoreResp)
          io.membus.req_ack := True
        }
        // when(io.axi.arw.ready & io.axi.w.ready) {
        //   goto(stateStoreResp)
        //   io.membus.req_ack := True
        // }
      }

    }
    val stateIdle: State = new State with EntryPoint {
      whenIsActive {
        doIdle
      }
    }
    val stateLoad: State = new State {
      whenIsActive {
        io.axi.r.ready := True
        io.axi.arw.valid := False
        when(io.axi.r.valid) {
          io.membus.read_data.assignFromBits(io.axi.r.payload.data)
          io.membus.status := TransactionStatus.DONE
          goto(stateIdle)
          //doIdle
        }
      }
    }
    val stateStoreResp: State = new State {
      whenIsActive {
        io.axi.b.ready := True
        when(io.axi.b.valid) {
          io.membus.status := TransactionStatus.DONE
          goto(stateIdle)
          //doIdle
        }
      }
    }
    val stateStoreData: State = new State {
      whenIsActive {
        io.axi.w.valid := True
        when(io.axi.w.ready) {
          io.membus.req_ack := True
          goto(stateIdle)
        }
      }
    }
  }
}

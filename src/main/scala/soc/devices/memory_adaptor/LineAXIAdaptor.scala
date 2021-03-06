package soc.devices.memory_adaptor

import cpu.interfaces.{LineRequest, LineResponse}
import spinal.lib.bus.amba4.axi.{
  Axi4Shared,
  Axi4SlaveFactory,
  Axi4Config,
  Axi4CrossbarFactory
}
// import cpu.debug.debug_memory_adaptor
import cpu.shared.memory_state.{
  TransactionStatus,
  TransactionType,
  TransactionSize
}

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._

class LineAXIAdaptor(id: Int)(implicit axiConfig: Axi4Config)
    extends Component {

  val io = new Bundle {
    val request = slave(new LineRequest)
    val response = master(new LineResponse)

    val axi = master(Axi4Shared(axiConfig))
  }

  val requestReg = Reg(new LineRequest)

  // TODO convert this to a pipelined implementation. For now we'll stick with a state machine

  io.axi.arw.valid := False
  io.axi.r.ready := False
  io.axi.b.ready := False
  io.axi.w.valid := False
  //io.axi.w.valid.noCombLoopCheck

  io.axi.arw.size := 0
  io.axi.arw.payload.addr := 0
  io.axi.arw.write := False
  io.axi.arw.burst := 0
  io.axi.arw.len := 0
  io.axi.arw.id := id

  io.axi.w.last := False
  io.axi.w.payload.data := 0
  io.axi.w.strb := 0

  io.request.ack := False


  val req_start_byte = requestReg.byte_address(3 downto 0)
  val req_size = requestReg.size.as(UInt)
  // Whether the transaction will have to use two bus accesses
  val bus_aligned = Bool
  bus_aligned := False
  // Whether the transaction address is aligned to the size
  val byte_aligned = Bool
  byte_aligned := False

  val trans1_mask = B(0, 128 bits)
  val trans2_mask = B(0, 128 bits)
  val trans1_shift = U(0, 4 bits)
  val trans2_shift = U(0, 4 bits)

  val write1_strb = B(0, 16 bits)
  val write2_strb = B(0, 16 bits)

  for (permutation <- LineRequestTruthTable.TableEntries) {
    when(
      (req_start_byte === permutation.start_byte) & (req_size === permutation.request_size)
    ) {
      bus_aligned := Bool(permutation.bytes_in_transaction2 == 0)
      byte_aligned := Bool(permutation.byte_addr_aligned)
      trans1_mask := (BigInt(1) << 8 * permutation.bytes_in_transaction1) - 1
      trans2_mask := ((BigInt(
        1
      ) << 8 * permutation.bytes_in_transaction2) - 1) << (8 * permutation.bytes_in_transaction1)

      write1_strb := ((1 << permutation.bytes_in_transaction1) - 1) << permutation.start_byte
      write2_strb := (1 << permutation.bytes_in_transaction2) - 1
      trans1_shift := req_start_byte
      if (permutation.bytes_in_transaction2 != 0) {
        trans2_shift := permutation.bytes_in_transaction1
      }
    }
  }

  val dataOut = RegInit(B(0, 128 bits))
  val statusOut = RegInit(TransactionStatus.IDLE)
  val byteAddrOut = RegInit(U(0, 64 bits))

  io.response.data := dataOut.asUInt
  io.response.status := statusOut
  io.response.byte_address := byteAddrOut

  when(statusOut === TransactionStatus.DONE && io.response.ready){
    statusOut := TransactionStatus.IDLE
  }


  // The size field presented to the axi bus
  val axi_size = UInt(3 bits)
  axi_size := 4
  when(byte_aligned) {
    switch(requestReg.size) {
      is(TransactionSize.BYTE) { axi_size := 0 }
      is(TransactionSize.HALFWORD) { axi_size := 1 }
      is(TransactionSize.WORD) { axi_size := 2 }
      is(TransactionSize.DOUBLEWORD) { axi_size := 3 }
      is(TransactionSize.QUADWORD) { axi_size := 4 }
    }
  }

  val fsm = new StateMachine {
    val address1 = new State with EntryPoint
    val address2 = new State
    val read1 = new State
    val read2 = new State
    val write1 = new State
    val write2 = new State

    // Handles the awr channel
    address1.whenIsActive {
      // When valid
      when(io.request.ldst_req =/= TransactionType.NONE) {
        requestReg := io.request
        io.request.ack := True
        goto(address2)
        statusOut := TransactionStatus.WAITING
      }
    }
    address2.whenIsActive {
      io.axi.arw.addr := requestReg.byte_address.resized
      io.axi.arw.id := id
      io.axi.arw.write := requestReg.ldst_req === TransactionType.STORE
      io.axi.arw.size := axi_size
      io.axi.arw.burst := 1 // INCR
      io.axi.arw.len := U(~bus_aligned).resized
      io.axi.arw.valid := True
      byteAddrOut := requestReg.byte_address
      when(requestReg.ldst_req === TransactionType.STORE) {
        io.axi.w.data := (requestReg.data |<< (trans1_shift*8)).asBits
        io.axi.w.strb := write1_strb
        io.axi.w.valid := True
        io.axi.w.last := bus_aligned

      }
      when(io.axi.arw.ready) {
        when(requestReg.ldst_req === TransactionType.LOAD) {
          goto(read1)
        }.otherwise {
          when(bus_aligned) {
            goto(write2)
          }.otherwise {
            goto(write1)
          }
        }
      }
      // If a misaligned access crosses a 4k boundary, issue a bus
      // error so it can be emulated by the page fault handler
      when(!bus_aligned && requestReg.byte_address(10 downto 4) === 0x7f){
        statusOut := TransactionStatus.PAGE_FAULT
        io.axi.arw.valid := False
        goto(address1)
      }
    }
    read1.whenIsActive {
      io.axi.r.ready := io.response.ready
      when(io.axi.r.valid & io.axi.r.ready) {
        when(bus_aligned) {
          goto(address1)
          statusOut := TransactionStatus.DONE
        }.otherwise {
          goto(read2)
        }
        // TODO register this
        dataOut := (io.axi.r.data |>> (8 * trans1_shift)) & trans1_mask
      }
    }

    read2.whenIsActive {
      io.axi.r.ready := io.response.ready
      when(io.axi.r.valid & io.axi.r.ready) {
        goto(address1)
        statusOut := TransactionStatus.DONE
      }
      dataOut := dataOut | ((io.axi.r.data |<< (8 * trans2_shift)) & trans2_mask)
    }

    write1.whenIsActive {
      io.axi.w.data := (requestReg.data |>> (trans2_shift*8)).asBits
      io.axi.w.strb := write2_strb
      io.axi.w.valid := True
      io.axi.w.last := True

      when(io.axi.w.ready) {
        goto(write2)
      }
    }
    write2.whenIsActive {
      io.axi.b.ready := True
      when(io.axi.b.valid){
        goto(address1)
        statusOut := TransactionStatus.DONE
      }
    }
  }

}

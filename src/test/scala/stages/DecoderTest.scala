package cpu.stages
import cpu.{CPUConfig}

import cpu.interfaces.{DecoderData, ReadInterface, FetchOutput}
import isa.{FormEnums, MnemonicEnums, ISAPairings, SourceSelect}
import cpu.uOps.functional_units.Integer.{IntegerFUSub, AdderSelectB, AdderCarryIn, AdderArgs, LogicSelectB, LogicArgs, MultiplierSelectB, MultiplierArgs, BranchArgs, ShifterSelectB, ShifterME, ShifterMB, ShifterArgs, ComparatorArgs, ComparatorSelectB}
import util.{PipeStage, PipeData}

import spinal.core._
import spinal.lib._
import spinal.sim._

import spinal.core.sim._
import org.scalatest._
import flatspec._
import matchers._


class DecoderDUT extends PipeStage(new FetchOutput, new ReadInterface){
  implicit val config = new CPUConfig()
  val dec = new uOpAndFormDecoder
  val populate = new PopulateByForm
  dec << pipeInput
  dec >-> populate >-> pipeOutput
}

class DecoderTestVerilog extends AnyFlatSpec with should.Matchers {
  behavior of "uOpAndFormDecoder"


  it should "create verilog" in {
    implicit val cpuConfig = new CPUConfig()
    val config = SpinalConfig(mode=Verilog, mergeAsyncProcess=true).withoutEnumString()
    SpinalVerilog(new DecoderDUT)
    SpinalVerilog(new uOpAndFormDecoder)
    config.generate(new uOpAndFormDecoderBySeq(ISAPairings.pairings.take(10)))
  }
}

class DecoderTest extends AnyFlatSpec with should.Matchers {
  behavior of "uOpAndFormDecoder"


  it should "create verilog" in {
    implicit val cpuConfig = new CPUConfig()
    val config = SpinalConfig(mode=Verilog, mergeAsyncProcess=true).withoutEnumString()
    SpinalVerilog(new DecoderDUT)
    SpinalVerilog(new uOpAndFormDecoder)
    config.generate(new uOpAndFormDecoderBySeq(ISAPairings.pairings.take(10)))
  }

  it should "decode some simple instructions" in {
    SimConfig.withWave.doSim(new DecoderDUT) { dut =>
      dut.clockDomain.forkStimulus(10)
        dut.pipeOutput.ready #= true
        dut.pipeInput.valid #= true
        // andi. r1, r5, 0x3f
        dut.pipeInput.payload.insn #= 0x70a1003F
        dut.clockDomain.waitSampling(10)
        assert(dut.pipeOutput.payload.dec_data.form.toEnum == FormEnums.D6)
        assert(dut.pipeOutput.payload.dec_data.found_match.toBoolean == true)
        assert(dut.pipeOutput.payload.dec_data.opcode.toEnum == MnemonicEnums.andidot)
        assert(dut.pipeOutput.payload.slots(0).idx.toInt == 5)
        assert(dut.pipeOutput.payload.slots(0).sel.toEnum == SourceSelect.GPR)
        assert(dut.pipeOutput.payload.write_interface.slots(0).idx.toInt == 1)
        assert(dut.pipeOutput.payload.write_interface.slots(0).sel.toEnum == SourceSelect.GPR)
        assert(dut.pipeOutput.payload.imm.valid.toBoolean == true)
        assert(dut.pipeOutput.payload.imm.payload.toBigInt == 0x3f)
      }
  }
  it should "decode ExecuteArgs" in {
    SimConfig.withWave.doSim(new DecoderDUT) { dut =>
      dut.clockDomain.forkStimulus(10)
      dut.pipeOutput.ready #= true
      dut.pipeInput.valid #= true
      dut.pipeOutput.flush #= false

      // addi r4, r8, 0x1234
      dut.pipeInput.payload.insn #= 0x38a81234
      dut.clockDomain.waitSampling(1)
      // add r2, r3, r4
      dut.pipeInput.payload.insn #= 0x7c432214
      dut.clockDomain.waitSampling(1)
      // neg r10, r11
      dut.pipeInput.payload.insn #= 0x7d4b00d0
      dut.clockDomain.waitSampling(10)
      }
  }
}

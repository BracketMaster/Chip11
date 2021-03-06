package cpu.stages.functional_units.integer

import cpu.interfaces.{DecoderData, ReadInterface}
import util.{PipeStage}
import isa.MnemonicEnums

import spinal.core._
import spinal.lib._

// contains implementation of POWER multiplier/divider class which is derived from a Chisel module

// M/D class should also contain ChiselEnum objects that help interprest
// args1,2, and 3 that it recieves.


class Mult(wid: Int) extends Component {
  val io = new Bundle {
    val a = in UInt(wid bits)
    val b = in UInt(wid bits)

    val o = out UInt(2*wid bits)
  }

  io.o := io.a * io.b
}

class MultData(wid: Int) extends Bundle {
  val products = Seq.fill(4)(UInt(wid bits))
}

class MultB(wid: Int) extends Component {
  val io = new Bundle {
    val a = in UInt(wid bits)
    val b = in UInt(wid bits)

    val o = out(new MultData(wid))
  }

  val half_wid = wid/2

  val products = Seq.fill(4)(UInt(wid bits))

  val a_operands = io.a.subdivideIn(2 slices)
  val b_operands = io.b.subdivideIn(2 slices)

  val operands = for { a <- a_operands
    b <- b_operands} yield (a, b)

  for((operand, dest) <- operands.zip(products)){
    operand match {
      case (a, b) => dest := a * b
    }
  }
  println(operands)

  io.o(half_wid-1 downto 0) := products(0)(half_wid-1 downto 0)

  val sum1 = UInt(half_wid+2 bits)
  sum1 := (products(0)(wid-1 downto half_wid) +^ products(1)(half_wid-1 downto 0)) +^ products(2)(half_wid-1 downto 0)

  io.o(wid-1 downto half_wid) := sum1(half_wid-1 downto 0)

  val sum2 = UInt(half_wid + 2 bits)
  sum2 := (products(1)(wid-1 downto half_wid) +^ products(2)(wid-1 downto half_wid)) +^ (
    products(3)(half_wid-1 downto 0) +^ sum1(half_wid+1 downto half_wid))
  io.o(half_wid*3-1 downto wid) := sum2(half_wid-1 downto 0)

  val sum3 = UInt(half_wid+1 bits)
  sum3 := products(3)(wid-1 downto half_wid) +^ sum2(half_wid+1 downto half_wid)
  io.o(wid*2-1 downto half_wid*3) := sum3(half_wid-1 downto 0)
}


class Multiplier(val wid: Int) extends Component {
  val io = new Bundle {
    val a = in UInt (wid bits)
    val b = in UInt (wid bits)

    val is_div = in Bool
    val word_operands = in Bool
    val is_unsigned = in Bool
    val output_high = in Bool
    val output_word = in Bool
    val shift_a = in Bool

    val o = out UInt (wid bits)
    val cr0_out = out UInt (3 bits)
    val overflow = out Bool
  }
  val mul_a = SInt((wid + 1) bits)
  val mul_b = SInt((wid + 1) bits)
  val mul_o = UInt((2 * wid + 2) bits)
  val mul_result = SInt((2 * wid + 2) bits)

  when(io.word_operands) {
    when(io.is_unsigned) {
      mul_a := Cat(U(0, 1 bits), io.a(wid / 2 - 1 downto 0)).asSInt.resized
      mul_b := Cat(U(0, 1 bits), io.b(wid / 2 - 1 downto 0)).asSInt.resized
    }.otherwise {
      mul_a := io.a(wid / 2 - 1 downto 0).asSInt.resized
      mul_b := io.b(wid / 2 - 1 downto 0).asSInt.resized
    }
  }.otherwise {
    when(io.is_unsigned) {
      mul_a := io.a.intoSInt
      mul_b := io.b.intoSInt
    }.otherwise {
      mul_a := io.a.asSInt.resized
      mul_b := io.b.asSInt.resized
    }
  }
  io.overflow := False
  //cast data out to UInt
  mul_result := (mul_a * mul_b)
  when(mul_result > (2 ^ 32 - 1)) {
    io.overflow := True
  }.otherwise {
    io.overflow := False
  }

  mul_o := mul_result.asUInt
  when(io.output_word) {
    when(io.output_high) {
      io.o := Cat(U(0, 32 bits), mul_o(wid - 1 downto wid / 2)).asUInt
    }.otherwise {
      io.o := Cat(U(0, 32 bits), mul_o(wid / 2 - 1 downto 0)).asUInt
    }
  }.otherwise {
    when(io.output_high) {
      io.o := mul_o(2 * wid - 1 downto wid)
    }.otherwise {
      io.o := mul_o(wid - 1 downto 0)
    }
  }

  when(mul_result > 0) {
    io.cr0_out := 2
  }.elsewhen(mul_result < 0) {
    io.cr0_out := 4
  }.otherwise {
    io.cr0_out := 1
  }

}

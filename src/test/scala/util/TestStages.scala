package util

import spinal.core._
import spinal.lib._

class AdderInput(val wid: Int) extends Bundle {
  val a = UInt(wid bits)
  val b = UInt(wid bits)
}

class AdderOutput extends Bundle {
  val y = UInt(8 bits)
}

class Adder(val wid: Int) extends PipeStage(new AdderInput(wid), UInt(wid bits)){
  o := i.a + i.b
}


class MultiDelay extends PipeStage(UInt(8 bits), UInt(8 bits)){

  val stage3Flush = in Bool

  val d1 = new Delay(UInt(8 bits))
  val d2 = new Delay(UInt(8 bits))
  val d3 = new Delay(UInt(8 bits))


  pipeInput >> d1.pipeInput
  d1.pipeOutput >-> d2.pipeInput
  d2.pipeOutput >-> d3.pipeInput
  d3.pipeOutput >> pipeOutput

  d1.pipeOutput.allowOverride
  d1.pipeOutput.flush := stage3Flush
}

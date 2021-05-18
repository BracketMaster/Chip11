//generated 2021-05-18 00:01:23.533759

package isa

import spinal.core._
import spinal.lib._

// All the different forms an instruction can take
object FormEnums extends SpinalEnum{

  val NONE = newElement()
  val A1 = newElement()
  val A2 = newElement()
  val A3 = newElement()
  val A4 = newElement()
  val A5 = newElement()
  val B1 = newElement()
  val D1 = newElement()
  val D2 = newElement()
  val D3 = newElement()
  val D4 = newElement()
  val D5 = newElement()
  val D6 = newElement()
  val D7 = newElement()
  val D8 = newElement()
  val D9 = newElement()
  val DQ1 = newElement()
  val DQ2 = newElement()
  val DQ3 = newElement()
  val DS1 = newElement()
  val DS2 = newElement()
  val DS3 = newElement()
  val DS4 = newElement()
  val DS5 = newElement()
  val DS6 = newElement()
  val DS7 = newElement()
  val DX1 = newElement()
  val I1 = newElement()
  val M1 = newElement()
  val M2 = newElement()
  val MD1 = newElement()
  val MD2 = newElement()
  val MDS1 = newElement()
  val MDS2 = newElement()
  val SC1 = newElement()
  val VA1 = newElement()
  val VA2 = newElement()
  val VA3 = newElement()
  val VC1 = newElement()
  val VX1 = newElement()
  val VX2 = newElement()
  val VX3 = newElement()
  val VX4 = newElement()
  val VX5 = newElement()
  val VX6 = newElement()
  val VX7 = newElement()
  val VX8 = newElement()
  val VX9 = newElement()
  val VX10 = newElement()
  val VX11 = newElement()
  val VX12 = newElement()
  val VX13 = newElement()
  val VX14 = newElement()
  val VX15 = newElement()
  val VX16 = newElement()
  val VX17 = newElement()
  val X1 = newElement()
  val X2 = newElement()
  val X3 = newElement()
  val X4 = newElement()
  val X5 = newElement()
  val X6 = newElement()
  val X7 = newElement()
  val X8 = newElement()
  val X9 = newElement()
  val X10 = newElement()
  val X11 = newElement()
  val X12 = newElement()
  val X13 = newElement()
  val X14 = newElement()
  val X15 = newElement()
  val X16 = newElement()
  val X17 = newElement()
  val X18 = newElement()
  val X19 = newElement()
  val X20 = newElement()
  val X21 = newElement()
  val X22 = newElement()
  val X23 = newElement()
  val X24 = newElement()
  val X25 = newElement()
  val X26 = newElement()
  val X27 = newElement()
  val X28 = newElement()
  val X29 = newElement()
  val X30 = newElement()
  val X31 = newElement()
  val X32 = newElement()
  val X33 = newElement()
  val X34 = newElement()
  val X35 = newElement()
  val X36 = newElement()
  val X37 = newElement()
  val X38 = newElement()
  val X39 = newElement()
  val X40 = newElement()
  val X41 = newElement()
  val X42 = newElement()
  val X43 = newElement()
  val X44 = newElement()
  val X45 = newElement()
  val X46 = newElement()
  val X47 = newElement()
  val X48 = newElement()
  val X49 = newElement()
  val X50 = newElement()
  val X51 = newElement()
  val X52 = newElement()
  val X53 = newElement()
  val X54 = newElement()
  val X55 = newElement()
  val X56 = newElement()
  val X57 = newElement()
  val X58 = newElement()
  val X59 = newElement()
  val X60 = newElement()
  val X61 = newElement()
  val X62 = newElement()
  val X63 = newElement()
  val X64 = newElement()
  val X65 = newElement()
  val X66 = newElement()
  val X67 = newElement()
  val X68 = newElement()
  val X69 = newElement()
  val X70 = newElement()
  val X71 = newElement()
  val X72 = newElement()
  val X73 = newElement()
  val X74 = newElement()
  val X75 = newElement()
  val X76 = newElement()
  val X77 = newElement()
  val X78 = newElement()
  val X79 = newElement()
  val X80 = newElement()
  val X81 = newElement()
  val X82 = newElement()
  val X83 = newElement()
  val X84 = newElement()
  val X85 = newElement()
  val X86 = newElement()
  val X87 = newElement()
  val X88 = newElement()
  val X89 = newElement()
  val X90 = newElement()
  val X91 = newElement()
  val X92 = newElement()
  val X93 = newElement()
  val X94 = newElement()
  val XFL1 = newElement()
  val XFX1 = newElement()
  val XFX2 = newElement()
  val XFX3 = newElement()
  val XFX4 = newElement()
  val XFX5 = newElement()
  val XFX6 = newElement()
  val XFX7 = newElement()
  val XFX8 = newElement()
  val XFX9 = newElement()
  val XL1 = newElement()
  val XL2 = newElement()
  val XL3 = newElement()
  val XL4 = newElement()
  val XL5 = newElement()
  val XO1 = newElement()
  val XO2 = newElement()
  val XO3 = newElement()
  val XO4 = newElement()
  val XS1 = newElement()
  val XX2_1 = newElement()
  val XX2_2 = newElement()
  val XX2_3 = newElement()
  val XX2_4 = newElement()
  val XX2_5 = newElement()
  val XX2_6 = newElement()
  val XX2_7 = newElement()
  val XX2_8 = newElement()
  val XX3_1 = newElement()
  val XX3_2 = newElement()
  val XX3_3 = newElement()
  val XX3_4 = newElement()
  val XX3_5 = newElement()
  val XX4_1 = newElement()
  val Z22_1 = newElement()
  val Z22_2 = newElement()
  val Z22_3 = newElement()
  val Z22_4 = newElement()
  val Z22_5 = newElement()
  val Z22_6 = newElement()
  val Z23_1 = newElement()
  val Z23_2 = newElement()
  val Z23_3 = newElement()
  val Z23_4 = newElement()
  val Z23_5 = newElement()
  val Z23_6 = newElement()
  val Z23_7 = newElement()
  val Z23_8 = newElement()
  val Z23_9 = newElement()
}

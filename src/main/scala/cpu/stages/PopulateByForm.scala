package cpu.stages

import isa.{SourceSelect}
import cpu.interfaces.{DecoderData, ReadInterface}
import cpu.shared.memory_state.{TransactionType, TransactionSize}
import cpu.shared.{XERMask}
import cpu.uOps.{FunctionalUnit}

import util.{PipeStage, PipeData}

import isa.{MnemonicEnums, FormEnums, SPREnums, Forms}
import isa.{ISAPairings, InstructionInfo, ReadSlotPacking, WriteSlotPacking}

import spinal.core._
import spinal.lib.{Reverse}

class PopulateByForm extends PipeStage(new DecoderData, new ReadInterface) {
  o.dec_data := i
  for (idx <- 0 until 5) {
    o.slots(idx).idx := 0
    o.slots(idx).sel := SourceSelect.NONE
    o.slots(idx).data := 0
    o.write_interface.slots(idx).idx := 0
    o.write_interface.slots(idx).data := 0
    o.write_interface.slots(idx).sel := SourceSelect.NONE
  }
  o.imm.valid := False
  o.imm.payload := 0

  o.compare := o.compare.getZero

  o.ldst_request.req_type := TransactionType.NONE
  o.ldst_request.size := TransactionSize.BYTE
  o.ldst_request.store_src_slot := 0
  o.ldst_request.store_data := 0
  o.ldst_request.load_dest_slot := 0
  o.ldst_request.ea := 0
  o.ldst_request.arithmetic := False

  import cpu.debug.debug_form
  if (debug_form) {
    when(pipeOutput.fire) {
      // printf(p"FORM: Populated info for form ${i.form.asUInt}\n")
    }
  }
  val spr_fields = Forms.XFX8.spr(i.insn)
  val spr = UInt(10 bits)
  spr := Cat(spr_fields(4 downto 0), spr_fields(9 downto 5)).asUInt

  def addRC() {
    o.write_interface.slots(WriteSlotPacking.CRAPort1).idx := 8
    o.write_interface.slots(WriteSlotPacking.CRAPort1).sel := SourceSelect.CRA
    o.compare.activate := True
    o.compare.out_slot := WriteSlotPacking.CRAPort1
    o.slots(ReadSlotPacking.XERPort1).idx := XERMask.SO
    o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER
  }

  switch(i.form) {

    // not sure we actually need this one (just used for fnmsub)
    // is(FormEnums.A4){
    // }

    // TODO revisit this one to implement checks for whether the CTR, BD, etc. are actually needed
    is(FormEnums.B1) {
      // So the CR is split up into two pseudo register files:
      // CRA is [cr0, cr2, cr4, cr6] and is on slot 4
      // CRB is [cr1, cr3, cr5, cr7] and is on slot 5

      // If the LSB is clear, use slot4/CRA
      val bi = Forms.B1.BI(i.insn)
      // printf(p"FORM: B1: Using bi value ${bi} (0b${Binary(bi)})\n")

      // TODO FIGURE THIS OUT!!!
      // bi's MSB selects between fields [0, 1, 2, 3] and [4, 5, 6, 7]
      // bi's 2nd MSB selects between fields [(0, 1), (2, 3)] or [(4, 5), (6, 7)]
      // bi's 3rd MSB selects between fields [0, 1 or 2, 3]
      when(bi(2) === False) {
        o.slots(ReadSlotPacking.CRAPort1).idx := 0xf
        o.slots(ReadSlotPacking.CRAPort1).sel := SourceSelect.CRA
      }.otherwise {
        o.slots(ReadSlotPacking.CRBPort1).idx := 0xf
        o.slots(ReadSlotPacking.CRBPort1).sel := SourceSelect.CRB
      }

      val bo = Reverse(Forms.B1.BO(i.insn))
      // If BO[2] == 0, then we need to read/write ctr
      when(bo(2) === False) {
        o.slots(ReadSlotPacking.SPRPort1).idx := SPREnums.CTR.asBits.asUInt
        o.slots(ReadSlotPacking.SPRPort1).sel := SourceSelect.SPR
        o.write_interface
          .slots(WriteSlotPacking.SPRPort1)
          .idx := SPREnums.CTR.asBits.asUInt
        o.write_interface
          .slots(WriteSlotPacking.SPRPort1)
          .sel := SourceSelect.SPR
      }

      // Write to lk if link is enabled
      val lk = Forms.B1.LK(i.insn)
      when(lk === True) {
        o.write_interface
          .slots(WriteSlotPacking.SPRPort2)
          .idx := SPREnums.LR.asBits.asUInt
        o.write_interface
          .slots(WriteSlotPacking.SPRPort2)
          .sel := SourceSelect.SPR
      }

      o.imm.valid := True
      o.imm.payload := Forms.B1.BD(i.insn).resize(64).asUInt
    }

    is(FormEnums.D1) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.D1.RA(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.XERPort1).idx := XERMask.SO
      o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER

      val bf = Forms.D1.BF(i.insn)
      val field_select = 3 - bf(2 downto 1)
      val mask = U(1) << field_select
      when(bf(0) === False) {
        o.write_interface.slots(WriteSlotPacking.CRAPort1).idx := mask.resized
        o.write_interface
          .slots(WriteSlotPacking.CRAPort1)
          .sel := SourceSelect.CRA
      }.otherwise {
        o.write_interface.slots(WriteSlotPacking.CRBPort1).idx := mask.resized
        o.write_interface
          .slots(WriteSlotPacking.CRBPort1)
          .sel := SourceSelect.CRB
      }
      o.imm.valid := True
      o.imm.payload := Forms.D1.SI(i.insn).resize(64).asUInt
    }

    is(FormEnums.D2) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.D2.RA(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.XERPort1).idx := XERMask.SO
      o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER
      val bf = Forms.D1.BF(i.insn)
      val field_select = 3 - bf(2 downto 1)
      val mask = U(1) << field_select
      when(bf(0) === False) {
        o.write_interface.slots(WriteSlotPacking.CRAPort1).idx := mask.resized
        o.write_interface
          .slots(WriteSlotPacking.CRAPort1)
          .sel := SourceSelect.CRA
      }.otherwise {
        o.write_interface.slots(WriteSlotPacking.CRBPort1).idx := mask.resized
        o.write_interface
          .slots(WriteSlotPacking.CRBPort1)
          .sel := SourceSelect.CRB
      }
      o.imm.valid := True
      o.imm.payload := Forms.D2.UI(i.insn).resized
    }

    is(FormEnums.D5) {
      val ra = Forms.D5.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.D5.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Forms.D5.D(i.insn).resize(64).asUInt
      switch(i.opcode) {
        is(MnemonicEnums.stb) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.BYTE
          o.ldst_request.store_src_slot := 1
        }
        is(MnemonicEnums.stbu) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.BYTE
          o.ldst_request.store_src_slot := 1
          // Write for updated address
          o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(WriteSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
        is(MnemonicEnums.stw) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.WORD
          o.ldst_request.store_src_slot := 1
        }
        is(MnemonicEnums.stwu) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.WORD
          o.ldst_request.store_src_slot := 1
          // Write for updated address
          o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(WriteSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
        is(MnemonicEnums.sth) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.HALFWORD
          o.ldst_request.store_src_slot := 1
        }
        is(MnemonicEnums.sthu) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.HALFWORD
          o.ldst_request.store_src_slot := 1
          // Write for updated address
          o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(WriteSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
      }
    }

    is(FormEnums.D6) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.D6.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.D6
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Forms.D6.UI(i.insn).resized
      // for D6 instructions, bit 28 (from left) controls whether the comparison is done
      when(i.insn(28)) {
        // use an output slot to hold the data to write to CR field 0
        // Select cr0 (the most significant 4 bits of the data field)
        o.write_interface.slots(WriteSlotPacking.CRAPort1).idx := 0x8
        o.write_interface
          .slots(WriteSlotPacking.CRAPort1)
          .sel := SourceSelect.CRA
        o.compare.activate := i.insn(28)
        o.compare.out_slot := WriteSlotPacking.CRAPort1
      }
    }

    is(FormEnums.D7) {
      val ra = Forms.D7.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      o.write_interface.slots(WriteSlotPacking.GPRPort2).idx := Forms.D7
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Forms.D7.D(i.insn).resize(64).asUInt
      switch(i.opcode) {
        is(MnemonicEnums.lbz) {
          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.BYTE
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
        is(MnemonicEnums.lbzu) {
          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.BYTE
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
          // Write slot for update register
          o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(WriteSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
        is(MnemonicEnums.lwz) {
          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.WORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
        is(MnemonicEnums.lwzu) {
          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.WORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
          // Write slot for update register
          o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(WriteSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
        is(MnemonicEnums.lhz, MnemonicEnums.lha) {
          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.HALFWORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
        is(MnemonicEnums.lhzu, MnemonicEnums.lhau) {
          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.HALFWORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
          // Write slot for update register
          o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(WriteSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
      }
      switch(i.opcode){
        is(MnemonicEnums.lha, MnemonicEnums.lhau){
          o.ldst_request.arithmetic := True
        }
      }
    }

    is(FormEnums.D8) {
      val ra = Forms.D8.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      val rt = Forms.D8.RT(i.insn)
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := rt.resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Forms.D8.SI(i.insn).resize(64).asUInt
      when(i.opcode === MnemonicEnums.addicdot) {
        addRC()
      }
      when(
        i.opcode === MnemonicEnums.addic || i.opcode === MnemonicEnums.addicdot
      ) {
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .idx := XERMask.CA | XERMask.CA32
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .sel := SourceSelect.XER
      }

      if (debug_form) { debug_form_d8 }
      def debug_form_d8 {
        when(pipeOutput.fire) {
          // printf(p"\tRA: $ra\n")
          // printf(p"\tRT: $rt\n")
        }
      }
    }

    // TODO revisit these and come up with a resolution for e.g. std and stdu sharing a form but having different R/W behavior
    is(FormEnums.DS3) {
      // printf("Populating fields for form DS3\n")
      val ra = Forms.DS3.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      val rs = Forms.DS3.RS(i.insn)
      o.slots(ReadSlotPacking.GPRPort2).idx := rs.resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR

      o.imm.valid := True
      o.imm.payload := Forms.DS3.DS(i.insn).resize(64).asUInt
      // handle doubleword load to RT, without update
      when(i.opcode === MnemonicEnums.std) {
        o.ldst_request.req_type := TransactionType.STORE
        o.ldst_request.size := TransactionSize.DOUBLEWORD
        o.ldst_request.store_src_slot := 1
      }
      when(i.opcode === MnemonicEnums.stdu) {
        o.ldst_request.req_type := TransactionType.STORE
        o.ldst_request.size := TransactionSize.DOUBLEWORD
        o.ldst_request.store_src_slot := 1
        // Write for updated address
        o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
        o.write_interface
          .slots(WriteSlotPacking.GPRPort1)
          .sel := SourceSelect.GPR
      }
    }
    is(FormEnums.DS5) {
      // printf("Populating fields for form DS5\n")
      val ra = Forms.DS5.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      o.write_interface.slots(WriteSlotPacking.GPRPort2).idx := Forms.DS5
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Forms.DS5.DS(i.insn).resize(64).asUInt
      // handle doubleword load to RT, without update
      when(i.opcode === MnemonicEnums.ld) {
        o.ldst_request.req_type := TransactionType.LOAD
        o.ldst_request.size := TransactionSize.DOUBLEWORD
        o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
      }
      when(i.opcode === MnemonicEnums.ldu) {
        o.ldst_request.req_type := TransactionType.LOAD
        o.ldst_request.size := TransactionSize.DOUBLEWORD
        o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := ra.resized
        o.write_interface
          .slots(WriteSlotPacking.GPRPort1)
          .sel := SourceSelect.GPR
      }
      when(i.opcode === MnemonicEnums.lwa){
        o.ldst_request.req_type := TransactionType.LOAD
        o.ldst_request.size := TransactionSize.WORD
        o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        o.ldst_request.arithmetic := True
      }

    }

    // TODO make sure this fully supports everything used in b[l][a]
    is(FormEnums.I1) {
      o.imm.valid := True
      val li = Forms.I1.LI(i.insn)
      val li_si = SInt(o.imm.payload.getWidth bits)
      li_si := li.resize(o.imm.payload.getWidth)
      o.imm.payload := li_si.asUInt
      when(Forms.I1.LK(i.insn) === True) {
        o.write_interface
          .slots(WriteSlotPacking.SPRPort1)
          .idx := SPREnums.LR.asBits.asUInt
        o.write_interface
          .slots(WriteSlotPacking.SPRPort1)
          .sel := SourceSelect.SPR
      }
    }

    is(FormEnums.M2) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.M2.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(i.opcode === MnemonicEnums.rlwimi_dot_) {
        o.slots(ReadSlotPacking.GPRPort3).idx := Forms.M2.RA(i.insn).resized
        o.slots(ReadSlotPacking.GPRPort3).sel := SourceSelect.GPR
      }
      o.imm.valid := True
      o.imm.payload := Forms.M2.SH(i.insn).resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.M2
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.M2.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.MD1) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.MD1.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(i.opcode === MnemonicEnums.rldimi_dot_) {
        o.slots(ReadSlotPacking.GPRPort3).idx := Forms.MD1.RA(i.insn).resized
        o.slots(ReadSlotPacking.GPRPort3).sel := SourceSelect.GPR
      }
      o.imm.valid := True
      o.imm.payload := Cat(
        Forms.MD1.sh2(i.insn),
        Forms.MD1.sh1(i.insn)
      ).asUInt.resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.MD1
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.MD1.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.MD2) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.MD2.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Cat(
        Forms.MD2.sh2(i.insn),
        Forms.MD2.sh1(i.insn)
      ).asUInt.resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.MD2
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.MD2.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.MDS2) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.MDS2.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.MDS2.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.MDS2
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.MDS2.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.X30) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.X30.RA(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.X30.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.XERPort1).idx := XERMask.SO
      o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER
      val bf = Forms.X30.BF(i.insn)
      val field_select = 3 - bf(2 downto 1)
      val mask = U(1) << field_select
      when(bf(0) === False) {
        o.write_interface.slots(WriteSlotPacking.CRAPort1).idx := mask.resized
        o.write_interface
          .slots(WriteSlotPacking.CRAPort1)
          .sel := SourceSelect.CRA
      }.otherwise {
        o.write_interface.slots(WriteSlotPacking.CRBPort1).idx := mask.resized
        o.write_interface
          .slots(WriteSlotPacking.CRBPort1)
          .sel := SourceSelect.CRB
      }
    }

    is(FormEnums.X62) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.X62.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.X62
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.X62.Rc(i.insn) === True) {
        addRC()
      }
    }
    is(FormEnums.X60) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.X62.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.X62
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
    }

    is(FormEnums.X65) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.X65.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Forms.X65.SH(i.insn).resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.X65
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.write_interface
        .slots(WriteSlotPacking.XERPort1)
        .idx := XERMask.CA | XERMask.CA32
      o.write_interface.slots(WriteSlotPacking.XERPort1).sel := SourceSelect.XER
      when(Forms.X62.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.X66) {
      val ra = Forms.X66.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.X66.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.GPRPort3).idx := Forms.X66.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort3).sel := SourceSelect.GPR
      switch(i.opcode) {
        import MnemonicEnums._
        is(stbx, stbux) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.BYTE
          o.ldst_request.store_src_slot := ReadSlotPacking.GPRPort3
        }
        is(sthx, sthux) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.HALFWORD
          o.ldst_request.store_src_slot := ReadSlotPacking.GPRPort3
        }
        is(stwx, stwux) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.WORD
          o.ldst_request.store_src_slot := ReadSlotPacking.GPRPort3
        }
        is(stdx, stdux) {
          o.ldst_request.req_type := TransactionType.STORE
          o.ldst_request.size := TransactionSize.DOUBLEWORD
          o.ldst_request.store_src_slot := ReadSlotPacking.GPRPort3
        }
      }
      switch(i.opcode) {
        import MnemonicEnums._
        is(stbux, sthux, stwux, stdux) {
          o.write_interface.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(ReadSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
      }
    }

    is(FormEnums.X68) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.X68.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.X68.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      when(
        i.opcode === MnemonicEnums.srad_dot_ || i.opcode === MnemonicEnums.sraw_dot_
      ) {
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .idx := XERMask.CA | XERMask.CA32
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .sel := SourceSelect.XER
      }
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.X68
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.X68.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.X77) {
      val ra = Forms.X77.RA(i.insn)
      when(ra =/= 0) {
        o.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      }
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.X77.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      val rt = Forms.X77.RT(i.insn).resized

      switch(i.opcode) {
        import MnemonicEnums._
        is(lbzx, lbzux) {
          o.write_interface.slots(ReadSlotPacking.GPRPort2).idx := rt.resized
          o.write_interface
            .slots(ReadSlotPacking.GPRPort2)
            .sel := SourceSelect.GPR

          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.BYTE
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
        is(lwzx, lwzux, lwax, lwaux) {
          o.write_interface.slots(ReadSlotPacking.GPRPort2).idx := rt.resized
          o.write_interface
            .slots(ReadSlotPacking.GPRPort2)
            .sel := SourceSelect.GPR

          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.WORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
        is(lhzx, lhzux, lhax, lhaux) {
          o.write_interface.slots(ReadSlotPacking.GPRPort2).idx := rt.resized
          o.write_interface
            .slots(ReadSlotPacking.GPRPort2)
            .sel := SourceSelect.GPR

          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.HALFWORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
        is(ldx, ldux) {
          o.write_interface.slots(ReadSlotPacking.GPRPort2).idx := rt.resized
          o.write_interface
            .slots(ReadSlotPacking.GPRPort2)
            .sel := SourceSelect.GPR

          o.ldst_request.req_type := TransactionType.LOAD
          o.ldst_request.size := TransactionSize.DOUBLEWORD
          o.ldst_request.load_dest_slot := WriteSlotPacking.GPRPort2
        }
      }
      switch(i.opcode) {
        import MnemonicEnums._
        is(lhax, lhaux, lwax, lwaux) {
          o.ldst_request.arithmetic := True
        }
      }

      switch(i.opcode) {
        import MnemonicEnums._
        is(lbzux, lhzux, lwzux, ldux, lwaux, lhaux) {
          o.write_interface.slots(ReadSlotPacking.GPRPort1).idx := ra.resized
          o.write_interface
            .slots(ReadSlotPacking.GPRPort1)
            .sel := SourceSelect.GPR
        }
      }
    }

    is(FormEnums.XFX2, FormEnums.XFX3) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.XFX2.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      val fxm = Forms.XFX3.FXM(i.insn)
      val maska = Cat(fxm(7), fxm(5), fxm(3), fxm(1))
      val maskb = Cat(fxm(6), fxm(4), fxm(2), fxm(0))

      // If any bits are set in mask a
      when(maska =/= 0) {
        o.write_interface
          .slots(WriteSlotPacking.CRAPort1)
          .idx := maska.asUInt.resized
        o.write_interface
          .slots(WriteSlotPacking.CRAPort1)
          .sel := SourceSelect.CRA
      }
      when(maskb =/= 0) {
        o.write_interface
          .slots(WriteSlotPacking.CRBPort1)
          .idx := maskb.asUInt.resized
        o.write_interface
          .slots(WriteSlotPacking.CRBPort1)
          .sel := SourceSelect.CRB
      }
    }
    is(FormEnums.XFX4) {
      val rs = Forms.XFX4.RS(i.insn)
      when(spr === SPREnums.XER.asBits.asUInt) {
        o.dec_data.opcode := MnemonicEnums.mtxer
        o.slots(ReadSlotPacking.GPRPort1).idx := rs.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
        o.write_interface.slots(WriteSlotPacking.XERPort1).idx := XERMask.ALL
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .sel := SourceSelect.XER
      }.otherwise {
        o.slots(ReadSlotPacking.GPRPort1).idx := rs.resized
        o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
        o.write_interface.slots(WriteSlotPacking.SPRPort1).idx := spr.resized
        o.write_interface
          .slots(WriteSlotPacking.SPRPort1)
          .sel := SourceSelect.SPR
      }
    }

    is(FormEnums.XFX5) {
      o.slots(ReadSlotPacking.CRAPort1).idx := 0xf
      o.slots(ReadSlotPacking.CRAPort1).sel := SourceSelect.CRA
      o.slots(ReadSlotPacking.CRBPort1).idx := 0xf
      o.slots(ReadSlotPacking.CRBPort1).sel := SourceSelect.CRB
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XFX5
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
    }
    is(FormEnums.XFX6) {
      val fxm = Forms.XFX6.FXM(i.insn)
      val maska = Cat(fxm(7), fxm(5), fxm(3), fxm(1))
      val maskb = Cat(fxm(6), fxm(4), fxm(2), fxm(0))
      when(maska =/= 0) {
        o.slots(ReadSlotPacking.CRAPort1).idx := 0xf
        o.slots(ReadSlotPacking.CRAPort1).sel := SourceSelect.CRA
      }
      when(maskb =/= 0) {
        o.slots(ReadSlotPacking.CRBPort1).idx := 0xf
        o.slots(ReadSlotPacking.CRBPort1).sel := SourceSelect.CRB
      }
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XFX6
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
    }

    // TODO check if the address returned from XFX8.spr is properly encoded
    is(FormEnums.XFX8) {
      // val spr_fields = Forms.XFX8.spr(i.insn)
      // val spr = UInt(10 bits)
      // spr := Cat(spr_fields(5 downto 0), spr_fields(9 downto 6)).asUInt
      when(spr === SPREnums.XER.asBits.asUInt) {
        o.dec_data.opcode := MnemonicEnums.mfxer
        o.slots(ReadSlotPacking.XERPort1).idx := XERMask.ALL
        o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER
        o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XFX8
          .RT(i.insn)
          .resized
        o.write_interface
          .slots(WriteSlotPacking.GPRPort1)
          .sel := SourceSelect.GPR
      }.otherwise {
        o.slots(ReadSlotPacking.SPRPort1).idx := spr
        o.slots(ReadSlotPacking.SPRPort1).sel := SourceSelect.SPR
        o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XFX8
          .RT(i.insn)
          .resized
        o.write_interface
          .slots(WriteSlotPacking.GPRPort1)
          .sel := SourceSelect.GPR
      }
    }

    // TODO revisit this one to determine if further sub-forms are necesarry
    is(FormEnums.XL4) {
      when(i.opcode === MnemonicEnums.bclr_l_) {
        o.slots(ReadSlotPacking.SPRPort1).idx := SPREnums.LR.asBits.asUInt
        o.slots(ReadSlotPacking.SPRPort1).sel := SourceSelect.SPR
      }
      when(i.opcode === MnemonicEnums.bcctr_l_) {
        o.slots(ReadSlotPacking.SPRPort1).idx := SPREnums.CTR.asBits.asUInt
        o.slots(ReadSlotPacking.SPRPort1).sel := SourceSelect.SPR
      }
      when(i.opcode === MnemonicEnums.bctar_l_) {
        o.slots(ReadSlotPacking.SPRPort1).idx := SPREnums.TAR.asBits.asUInt
        o.slots(ReadSlotPacking.SPRPort1).sel := SourceSelect.SPR
      }

      val bi = Forms.XL4.BI(i.insn)
      // printf(p"FORM: B1: Using bi value ${bi} (0b${Binary(bi)})\n")

      // TODO FIGURE THIS OUT!!!
      // bi's MSB selects between fields [0, 1, 2, 3] and [4, 5, 6, 7]
      // bi's 2nd MSB selects between fields [(0, 1), (2, 3)] or [(4, 5), (6, 7)]
      // bi's 3rd MSB selects between fields [0, 1 or 2, 3]
      when(bi(2) === False) {
        o.slots(ReadSlotPacking.CRAPort1).idx := 0xf
        o.slots(ReadSlotPacking.CRAPort1).sel := SourceSelect.CRA
      }.otherwise {
        o.slots(ReadSlotPacking.CRBPort1).idx := 0xf
        o.slots(ReadSlotPacking.CRBPort1).sel := SourceSelect.CRB
      }
    }

    is(FormEnums.XO1) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.XO1.RA(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XO1
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.XO1.OE(i.insn) === True) {
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .idx := XERMask.SO | XERMask.OV | XERMask.OV32
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .sel := SourceSelect.XER
      }
      when(Forms.XO1.Rc(i.insn) === True) {
        addRC()
      }
      switch(i.opcode) {
        is(
          MnemonicEnums.addme_o__dot_,
          MnemonicEnums.subfme_o__dot_,
          MnemonicEnums.addze_o__dot_,
          MnemonicEnums.subfze_o__dot_
        ) {
          o.slots(ReadSlotPacking.XERPort1).idx := XERMask.CA
          o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER
          o.write_interface
            .slots(WriteSlotPacking.XERPort1)
            .idx := XERMask.CA | XERMask.CA32
          o.write_interface
            .slots(WriteSlotPacking.XERPort1)
            .sel := SourceSelect.XER
          when(Forms.XO1.OE(i.insn) === True) {
            o.write_interface
              .slots(WriteSlotPacking.XERPort1)
              .idx := XERMask.SO | XERMask.OV | XERMask.OV32 | XERMask.CA | XERMask.CA32
            o.write_interface
              .slots(WriteSlotPacking.XERPort1)
              .sel := SourceSelect.XER
          }
        }
      }
    }

    is(FormEnums.XO3) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.XO3.RA(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.XO3.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XO3
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.XO3.Rc(i.insn) === True) {
        addRC()
      }
    }

    is(FormEnums.XO4) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.XO4.RA(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.slots(ReadSlotPacking.GPRPort2).idx := Forms.XO4.RB(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort2).sel := SourceSelect.GPR
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XO4
        .RT(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      when(Forms.XO4.Rc(i.insn) === True) {
        addRC()
      }
      when(Forms.XO4.OE(i.insn) === True) {
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .idx := XERMask.OV | XERMask.OV32 | XERMask.SO
        o.write_interface
          .slots(WriteSlotPacking.XERPort1)
          .sel := SourceSelect.XER
      }
      switch(i.opcode) {
        is(MnemonicEnums.adde_o__dot_, MnemonicEnums.subfe_o__dot_) {
          o.slots(ReadSlotPacking.XERPort1).idx := XERMask.CA
          o.slots(ReadSlotPacking.XERPort1).sel := SourceSelect.XER
          when(Forms.XO4.Rc(i.insn) === True) {
            o.slots(ReadSlotPacking.XERPort1).idx := XERMask.CA | XERMask.SO
          }
        }
      }
      switch(i.opcode) {
        is(
          MnemonicEnums.addc_o__dot_,
          MnemonicEnums.subfc_o__dot_,
          MnemonicEnums.adde_o__dot_,
          MnemonicEnums.subfe_o__dot_
        ) {

          o.write_interface
            .slots(WriteSlotPacking.XERPort1)
            .sel := SourceSelect.XER
          when(Forms.XO4.OE(i.insn) === True) {
            o.write_interface
              .slots(WriteSlotPacking.XERPort1)
              .idx := XERMask.OV | XERMask.OV32 |
              XERMask.SO | XERMask.CA | XERMask.CA32
          }.otherwise {
            o.write_interface
              .slots(WriteSlotPacking.XERPort1)
              .idx := XERMask.CA | XERMask.CA32
          }
        }
      }
    }

    is(FormEnums.XS1) {
      o.slots(ReadSlotPacking.GPRPort1).idx := Forms.XS1.RS(i.insn).resized
      o.slots(ReadSlotPacking.GPRPort1).sel := SourceSelect.GPR
      o.imm.valid := True
      o.imm.payload := Cat(
        Forms.XS1.sh2(i.insn),
        Forms.XS1.sh1(i.insn)
      ).asUInt.resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).idx := Forms.XS1
        .RA(i.insn)
        .resized
      o.write_interface.slots(WriteSlotPacking.GPRPort1).sel := SourceSelect.GPR
      // TODO determine what field is written here
      o.write_interface
        .slots(WriteSlotPacking.XERPort1)
        .idx := XERMask.CA | XERMask.CA32
      o.write_interface.slots(WriteSlotPacking.XERPort1).sel := SourceSelect.XER
      when(Forms.XS1.Rc(i.insn) === True) {
        addRC()
      }
    }

  }

}

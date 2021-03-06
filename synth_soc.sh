#!/bin/bash
set -e
sbt "test:testOnly *SoCWithUARTVerilog"
yosys -p 'synth_ecp5 -json soc.json -retime' SoCWithUART.v 2>&1 | tee synth.log
nextpnr-ecp5 --json soc.json --out-of-context --85k --freq 100 --router router2 2>&1 | tee pnr.log

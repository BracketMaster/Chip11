//generated 2021-04-21 15:24:58.035246

package soc.devices.memory_adaptor

case class TableEntry(start_byte: Int, request_size: Int, bytes_in_transaction1: Int, bytes_in_transaction2: Int, byte_addr_aligned: Boolean)

// Enumerate all possible ldst _line requests
object LineRequestTruthTable {
  val TableEntries = Seq(
    TableEntry(start_byte = 0, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 0, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 0, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 0, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 0, request_size  = 16, bytes_in_transaction1 = 16, bytes_in_transaction2 = 0, byte_addr_aligned  =true),

    TableEntry(start_byte = 1, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 1, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 1, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 1, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 1, request_size  = 16, bytes_in_transaction1 = 15, bytes_in_transaction2 = 1, byte_addr_aligned  =false),

    TableEntry(start_byte = 2, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 2, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 2, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 2, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 2, request_size  = 16, bytes_in_transaction1 = 14, bytes_in_transaction2 = 2, byte_addr_aligned  =false),

    TableEntry(start_byte = 3, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 3, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 3, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 3, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 3, request_size  = 16, bytes_in_transaction1 = 13, bytes_in_transaction2 = 3, byte_addr_aligned  =false),

    TableEntry(start_byte = 4, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 4, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 4, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 4, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 4, request_size  = 16, bytes_in_transaction1 = 12, bytes_in_transaction2 = 4, byte_addr_aligned  =false),

    TableEntry(start_byte = 5, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 5, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 5, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 5, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 5, request_size  = 16, bytes_in_transaction1 = 11, bytes_in_transaction2 = 5, byte_addr_aligned  =false),

    TableEntry(start_byte = 6, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 6, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 6, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 6, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 6, request_size  = 16, bytes_in_transaction1 = 10, bytes_in_transaction2 = 6, byte_addr_aligned  =false),

    TableEntry(start_byte = 7, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 7, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 7, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 7, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 7, request_size  = 16, bytes_in_transaction1 = 9, bytes_in_transaction2  = 7, byte_addr_aligned  =false),

    TableEntry(start_byte = 8, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 8, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 8, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 8, request_size  = 8, bytes_in_transaction1  = 8, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 8, request_size  = 16, bytes_in_transaction1 = 8, bytes_in_transaction2  = 8, byte_addr_aligned  =false),

    TableEntry(start_byte = 9, request_size  = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 9, request_size  = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 9, request_size  = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 9, request_size  = 8, bytes_in_transaction1  = 7, bytes_in_transaction2  = 1, byte_addr_aligned  =false),
    TableEntry(start_byte = 9, request_size  = 16, bytes_in_transaction1 = 7, bytes_in_transaction2  = 9, byte_addr_aligned  =false),

    TableEntry(start_byte = 10, request_size = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 10, request_size = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 10, request_size = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 10, request_size = 8, bytes_in_transaction1  = 6, bytes_in_transaction2  = 2, byte_addr_aligned  =false),
    TableEntry(start_byte = 10, request_size = 16, bytes_in_transaction1 = 6, bytes_in_transaction2  = 10, byte_addr_aligned =false),

    TableEntry(start_byte = 11, request_size = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 11, request_size = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 11, request_size = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 11, request_size = 8, bytes_in_transaction1  = 5, bytes_in_transaction2  = 3, byte_addr_aligned  =false),
    TableEntry(start_byte = 11, request_size = 16, bytes_in_transaction1 = 5, bytes_in_transaction2  = 11, byte_addr_aligned =false),

    TableEntry(start_byte = 12, request_size = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 12, request_size = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 12, request_size = 4, bytes_in_transaction1  = 4, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 12, request_size = 8, bytes_in_transaction1  = 4, bytes_in_transaction2  = 4, byte_addr_aligned  =false),
    TableEntry(start_byte = 12, request_size = 16, bytes_in_transaction1 = 4, bytes_in_transaction2  = 12, byte_addr_aligned =false),

    TableEntry(start_byte = 13, request_size = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 13, request_size = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =false),
    TableEntry(start_byte = 13, request_size = 4, bytes_in_transaction1  = 3, bytes_in_transaction2  = 1, byte_addr_aligned  =false),
    TableEntry(start_byte = 13, request_size = 8, bytes_in_transaction1  = 3, bytes_in_transaction2  = 5, byte_addr_aligned  =false),
    TableEntry(start_byte = 13, request_size = 16, bytes_in_transaction1 = 3, bytes_in_transaction2  = 13, byte_addr_aligned =false),

    TableEntry(start_byte = 14, request_size = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 14, request_size = 2, bytes_in_transaction1  = 2, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 14, request_size = 4, bytes_in_transaction1  = 2, bytes_in_transaction2  = 2, byte_addr_aligned  =false),
    TableEntry(start_byte = 14, request_size = 8, bytes_in_transaction1  = 2, bytes_in_transaction2  = 6, byte_addr_aligned  =false),
    TableEntry(start_byte = 14, request_size = 16, bytes_in_transaction1 = 2, bytes_in_transaction2  = 14, byte_addr_aligned =false),

    TableEntry(start_byte = 15, request_size = 1, bytes_in_transaction1  = 1, bytes_in_transaction2  = 0, byte_addr_aligned  =true),
    TableEntry(start_byte = 15, request_size = 2, bytes_in_transaction1  = 1, bytes_in_transaction2  = 1, byte_addr_aligned  =false),
    TableEntry(start_byte = 15, request_size = 4, bytes_in_transaction1  = 1, bytes_in_transaction2  = 3, byte_addr_aligned  =false),
    TableEntry(start_byte = 15, request_size = 8, bytes_in_transaction1  = 1, bytes_in_transaction2  = 7, byte_addr_aligned  =false),
    TableEntry(start_byte = 15, request_size = 16, bytes_in_transaction1 = 1, bytes_in_transaction2  = 15, byte_addr_aligned =false)
  )
  // some sanity tests
  LineRequestTruthTable.TableEntries.foreach{entry => assert((entry.start_byte + entry.bytes_in_transaction1) <= 16)}
  LineRequestTruthTable.TableEntries.foreach{entry => assert((entry.bytes_in_transaction1 + entry.bytes_in_transaction2) == entry.request_size)}
}

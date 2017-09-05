package set1.xor

import set1.hex.Hex
import set1.xor.SingleXor.toAscii

/* Set 1, Challenge 5: Encrypt some plain text by using a repeating block in an XOR cipher. A little harder than just
 *   plain single-character XOR ciphering.
 * Set 1, Challenge 6: Decrypt some cipher text that has been encrypted using a repeating block in an XOR cipher.
 */
object RepeatXor {

  private const val MAX_KEY_SIZE = 40

  /* <String>.toHex() converts the receiver object from an ASCII representation to the equivalent hex representation.
   * Requires: The receiver object is an ASCII-encoded (7-bit) string.
   */
  private fun String.toHex(): String =
    this.map { "%02x".format(it.toInt()) }.joinToString("")

  private fun String.standardize(): String =
    Hex.unconvert(this.filter { it != '\n' }).toAscii()

  /* encrypt(str, cipher) encrypts the plain text [str] by using [cipher] as a repeating cipher key.
   * Requires: [str] and [cipher] are ASCII-encoded (7-bit) strings.
   */
  fun encrypt(str: String, cipher: String): String {
    val msg = str.toHex()
    val key = cipher.toHex()
    val numSequences = str.length / cipher.length
    val remainder = (str.length % cipher.length) * 2 // Because each ASCII character is 2 hex characters.
    val cipherKey = key.repeat(numSequences) + if (remainder != 0) key.slice(0 until remainder) else ""
    return FixedXor.join(msg, cipherKey)
  }

  private fun Char.toBinary(): String =
    when (this) {
      in '0'..'9' -> this - '0'
      in 'a'..'f' -> this - 'a' + 10
      else -> throw IllegalArgumentException("$this is not a valid hex character!")
    }.toString(2)

  private fun String.toBinary(): String =
    this.fold("", { acc, ch -> acc + ch.toBinary() })

  private fun hammingDistance(s1: String, s2: String): Int {
    val str1 = s1.toHex()
    val str2 = s2.toHex()
    return FixedXor.join(str1, str2).toBinary().fold(0, { acc, ch -> acc + ch.toString().toInt() })
  }

  private fun <T> transpose(blocks: List<List<T>>): List<List<T>> {
    if (blocks.isEmpty()) return blocks

    val width = blocks.first().size
    if (blocks.any{ it.size != width }) {
      throw IllegalStateException("The specified list of lists is not rectangular! Some sublists are of uneven length.")
    }

    return (0 until width).map { col -> (0 until blocks.size).map { row -> blocks[row][col] } }
  }

  /* decrypt(str) decrypts the cipher text [str] by using a statistical algorithm to determine the most-likely cipher
   *   key and then undoing the encryption process via a second repeating-block XOR pass. Returns the key and plain text.
   * Requires: [str] is a base-64 string.
   */
  fun decrypt(str: String): Pair<String,String> {
    val msg = str.standardize()
    val keySizes  = Array(MAX_KEY_SIZE, { it + 1 })
    val distances = HashMap<Int,Double>()

    // Loop through all of the possible key sizes and find the one that has the minimum Hamming distance group metric.
    for (keySize in keySizes) {
      val firstChunk  = msg.slice(0 until keySize)
      val secondChunk = msg.slice(keySize until 2 * keySize)
      val normalizedDistance = hammingDistance(firstChunk, secondChunk) / keySize.toDouble()
      distances.put(keySize, normalizedDistance)
    }

    // Find the ideal key size and then pad the string so that it has a multiple of this many characters. TODO: FIX.
    /* val keySize = distances.minBy { (_,v) -> v }!!.key // Most likely the key size, since smallest normalized distance. */
    val keySize = 29
    var lst = msg.toList()
    lst += List(keySize - (lst.size % keySize), { '\u0000' })

    // Split the string into chunks of the ideal size, and then transpose them.
    val blocks = Partition(lst, keySize)
    val transposedBlocks = transpose(blocks)

    // Finally, get the key from the transposed blocks via single-byte XOR recombination. Use it to recover the message.
    val key = transposedBlocks.fold("", { acc, b -> acc + SingleXor.decrypt(b.filter{ it != '\u0000' }.joinToString("").toHex()).first })
    val text = encrypt(msg, key).toAscii()
    val plainText = if (str.contains('=')) text.substring(0 until text.length - 2) else text // Strip last 2 chars.
    
    return Pair(key, plainText)
  }
}

/* A Partition<T> is a view of a list that appears as if it has been broken into partitions of equal size. The list and
 *   the size of each "chunk" is specified in the constructor. Since Partitions are immutable, providing a lookup-based
 *   view saves space in memory by not actually creating a partition.
 *
 * Based on a Lists collection implementation provided by Google Guava. 
 */
private class Partition<out T>(private val list: List<T>, private val chunkSize: Int): AbstractList<List<T>>() {
  override val size = (list.size + chunkSize - 1) / chunkSize // Should mostly avoid overflow.

  @Override
  override fun get(index: Int): List<T> {
    if (index < 0 || index >= size) throw IndexOutOfBoundsException("Out of range: index: $index, size: $size.")

    val start = index * chunkSize
    val end = Math.min(start + chunkSize, list.size)
    return list.subList(start, end) // Provides a view by doing this look-up every time. Slower but space-efficient.
  }
}
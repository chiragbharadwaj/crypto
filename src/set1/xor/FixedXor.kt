package set1.xor

import set1.xor.Bit.*

/* Set 1, Challenge 2. XOR together two strings represented in hex, bitwise, and return the resulting hex string. */
object FixedXor {
  /* <Int>.toBinary() converts the receiver object into the equivalent bit representation.
   * Requires: The receiver object is either 0 or 1.
   */
  private fun Int.toBinary(): Bit =
    when (this) {
      0 -> ZERO
      1 -> ONE
      else -> throw IllegalArgumentException("$this is not a valid binary number!")
    }

  /* <Bit>.toDecimal() converts the receiver object into the equivalent integer representation. */
  private fun Bit.toDecimal(): Int =
    when (this) {
      ZERO -> 0
      ONE  -> 1
    }

  /* bitXor((b1,b2)) returns the bitwise XOR of bits [b1] and [b2] as a bit. Optimized. */
  private fun bitXor(p: Pair<Bit, Bit>): Bit =
    when (p.first) {
      ZERO ->  p.second
      ONE  -> !p.second
    }

  /* <Bit[]>.pad() pads the receiver object by zero-extending it from however many bits wide it is to four bits wide.
   * Requires: The receiver object contains at most four bits.
   */
  private fun Array<Bit>.pad(): Array<Bit> =
    when (this.size) {
      0 -> emptyArray<Bit>()
      1 -> arrayOf(ZERO, ZERO, ZERO) + this
      2 -> arrayOf(ZERO, ZERO) + this
      3 -> arrayOf(ZERO) + this
      4 -> this
      else -> throw IllegalArgumentException("Hex characters cannot contain more than 4 bits!")
    }

  /* expand(ch) expands the hex character [ch] into the equivalent 4-bit binary representation.
   * Requires: [ch] must be input as a single base-16 number, i.e. it is either [0-9] or [a-f].
   */
  private fun expand(ch: Char): Array<Bit> {
    /* <Char>.toInteger() converts the receiver object into the equivalent integer representation.
     * Requires: The receiver object must be input as a single base-16 character, i.e. it is either [0-9] or [a-f].
     */
    fun Char.toInteger(): Int =
      when (this) {
        in '0'..'9' -> this - '0'
        in 'a'..'f' -> this - 'a' + 10
        else -> throw IllegalArgumentException("$this is not a valid hex character.")
      }

    /* <Int>.toBits() converts the receiver object into the equivalent binary representation through repeated division. */
    fun Int.toBits(): Array<Bit> =
      if (this == this % 2) arrayOf(this.toBinary()) else (this / 2).toBits() + arrayOf((this % 2).toBinary())

    return ch
           .toInteger()
           .toBits()
           .pad()
  }

  /* <Bit[]>.compress() compresses the receiver object into the equivalent hexadecimal character via base arithmetic on
   *   each input bit from right-to-left.
   * Requires: The receiver object consists of at most 4 bits. */
  private fun Array<Bit>.compress(): Char {
    val num = this.fold(0) { acc, b -> 2 * acc + b.toDecimal() }
    return when (num) {
      in 0..9   -> num.toString()[0]
      in 10..15 -> 'a' + num - 10
      else -> throw IllegalArgumentException("$num cannot be converted to a valid hex character.")
    }
  }

  /* join(s1,s2) performs a bit-wise XOR on the two hex strings [s1] and [s2] and returns the result as a hex string.
   * Requires: [s1] and [s2] consist of only hexadecimal characters, i.e. only [0-9] and [a-f] as constituents. The two
   *   hex strings must also be of equal length.
   */
  fun join(s1: String, s2: String): String {
    if (s1.length != s2.length) throw IllegalArgumentException("Mismatch: s1.length: ${s1.length} != s2.length: ${s2.length}.")

    // Expand the strings to binary numbers and XOR them together.
    val bits1 = s1.toCharArray().map(this::expand).toTypedArray().flatten()
    val bits2 = s2.toCharArray().map(this::expand).toTypedArray().flatten()
    val bits = (bits1 zip bits2).map(this::bitXor).toTypedArray()

    /* Array<Bit>.recover() returns the hex characters corresponding to the binary characters in the receiver object by
     *   patching them in chunks/groups of four. Zero-extension is done for the most-significant group of bits.
     */
    fun Array<Bit>.recover(): CharArray =
      when (this.size) {
        0       -> CharArray(0)
        in 1..4 -> charArrayOf(this.pad().compress())
        else    -> charArrayOf(this.slice(0 until 4).toTypedArray().compress()) +
                               this.slice(4 until this.size).toTypedArray().recover()
      }

    return bits
           .recover()
           .joinToString("")
  }
}
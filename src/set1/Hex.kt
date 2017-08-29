package set1

/* Set 1, Challenge 1. Convert strings represented in hex to their equivalent
 * representations in base-64. */
object Hex {

  private fun CharArray.regroup(n: Int): CharArray {
    // No need to do extra work if it's already that small.
    if (this.size < n) return this

    // Pre-processing information.
    val rev = this.reversedArray()
    val final = rev.size - (rev.size % n) - 1
    val chs = CharArray(rev.size)

    var base = 0  // Marker for which "group of n" we're on.
    var count = 0 // How far away from the base marker we are, up to n away.
    for (i in 0..final) {
      // Small optimization hack to get everything done in a single loop.
      if (i < rev.size - final - 1) {
        chs[final + i + 1] = this[i]
      }
      // Reset the counter.
      if (count == n) {
        count = 0
        base = i // Move the marker up.
      }
      // Don't question these indices. They work! Source: lots of testing.
      chs[i] = rev[base + n - count - 1]
      count++
    }
    return chs
  }

  private fun Char.toDecimal(): Int =
    when (this) {
      in '0'..'9' -> this - '0'
      in 'a'..'f' -> this - 'a' + 10
      else -> throw IllegalArgumentException("$this is not a valid hex character.")
    }

  private fun CharArray.toDecimal(): Int =
    this.fold(0) { acc, c -> 16 * acc + c.toDecimal() }

  private fun chunkToSixtyFour(chars: CharArray): CharArray {
    fun intToChar(num: Int): Char {
      val mod = num % 26
      return when (num / 26) {
        0 -> 'A' + mod
        1 -> 'a' + mod
        2 -> when (mod) {
          10 -> '+'
          11 -> '/'
          else -> mod.toString()[0] // Another hack based on the representation.
        }
        else -> throw IllegalStateException("$num is greater than 63 in base-64!")
      }.toChar()
    }
    val dec = chars.toDecimal()
    val msb = dec / 64
    val lsb = dec - (msb) * 64
    return intArrayOf(msb,lsb)
           .map(::intToChar)
           .toCharArray()
  }

  private fun CharArray.toSixtyFour(): CharArray {
    val numGroups = this.size / 3
    val chs = CharArray(2 * numGroups)
    for (i in 0 until numGroups) {
      val sub = this.slice(3 * i until 3 * i + 3).toCharArray()
      val convertedChs = chunkToSixtyFour(sub)
      chs[2*i]   = convertedChs[0]
      chs[2*i+1] = convertedChs[1]
    }
    val last = this.slice(3 * numGroups until this.size).toCharArray()
    val convertedChs = chunkToSixtyFour(last)
    return chs + if (this.size % 3 != 0) convertedChs else CharArray(0)
  }

  /* convert(hex) returns the string [hex] in a base-64 representation of itself.
   * Requires: [hex] is input as a base-16 number, i.e. it only contains [0-9]
   *   and [a-f] as constituent characters.
   */
  fun convert(hex: String): String {
    return hex
           .toCharArray()
           .regroup(3)
           .toSixtyFour()
           .regroup(2)
           .joinToString("")
  }
}
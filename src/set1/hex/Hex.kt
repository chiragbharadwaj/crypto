package set1.hex

/* Set 1, Challenge 1. Convert strings represented in hex to their equivalent representations in base-64. */
object Hex {
  /* <Char[]>.regroup(n) reverses the receiver object in groups/chunks of size [n]. That is, the entire receiver
   *   object is reversed while keeping chunks of size n intact (counting from the back). For example, if n = 3, then
   *   ['d','e','a','d','b','e','e'].regroup(3) would return ['e','d','b','e','d','e','a'].
   * Requires: [n] >= 1.
   * */
  private fun CharArray.regroup(n: Int): CharArray {
    if (n < 1) throw IllegalArgumentException("$n is not greater than or equal to 1!")

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

  /* <Char>.toDecimal() converts the receiver object to the equivalent integer.
   * Requires: The receiver object must be input as a single base-16 number, i.e. it is either [0-9] or [a-f].
   */
  private fun Char.toDecimal(): Int =
    when (this) {
      in '0'..'9' -> this - '0'
      in 'a'..'f' -> this - 'a' + 10
      else -> throw IllegalArgumentException("$this is not a valid hex character!")
    }

  /* <Char[]>.toDecimal() converts the receiver object into the equivalent integer via base arithmetic on each hex input
   *   character from right-to-left.
   * Requires: The receiver object consists of only hexadecimal characters, i.e. only [0-9] and [a-f] as constituents.
   */
  private fun CharArray.toDecimal(): Int =
    this.fold(0) { acc, c -> 16 * acc + c.toDecimal() }

  /* <Char>.toDec() converts the receiver object to the equivalent integer.
   * Requires: The receiver object must be input as a single base-64 number, i.e. it is either [0-9],[a-z],[A-Z], or [+,/].
   */
  private fun Char.toDec(): Int =
    when (this) {
      in 'A'..'Z' -> this - 'A'
      in 'a'..'z' -> this - 'a' + 26
      in '0'..'9' -> this - '0' + 52
              '+' -> 62
              '/' -> 63
              '=' -> 0 // Special character used for padding in base-64, apparently.
      else -> throw IllegalArgumentException("$this is not a valid base-64 character!")
    }

  /* <Char[]>.toDec() converts the receiver object into the equivalent integer via base arithmetic on each base-64 input
   *   character from right-to-left.
   * Requires: The receiver object consists of only base-64 characters, i.e. only [0-9],[a-z],[A-Z], and [+,/] as constituents.
   */
  private fun CharArray.toDec(): Int =
    this.fold(0) { acc, c -> 64 * acc + c.toDec() }

  /* <Char[]>.chunkToSixtyFour() converts the receiver object from its hexadecimal representation to a base-64
   *   representation by converting the three hexadecimal characters into two base-64 characters.
   * Requires: The receiver object consists of at most three hexadecimal characters, i.e. between 000 and fff.
   */
  private fun chunkToSixtyFour(chars: CharArray): CharArray {
    /* intToChar(num) converts [num] to its base-64 character representation.
     * Requires: 0 <= [num] <= 63.
     */
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
        else -> throw IllegalStateException("$num is greater than 63 in base-64!") // Even 63/26 = 2.
      }.toChar()
    }

    val dec = chars.toDecimal()
    val msb = dec / 64
    val lsb = dec - (64 * msb)
    return intArrayOf(msb,lsb)
           .map(::intToChar)
           .toCharArray()
  }

  /* <Char[]>.chunkToHex() converts the receiver object from its base-64 representation to a hexadecimal representation
   *   by converting the two base-64 characters into three hexadecimal characters.
   * Requires: The receiver object consists of at most two base-64 characters, i.e. between 00 and //.
   */
  private fun chunkToHex(chars: CharArray): CharArray {
    /* toHex(num) converts [num] to its hex character representation.
     * Requires: 0 <= [num] <= 15.
     */
    fun toHex(num: Int): Char =
      when (num) {
        in 0..9   -> '0' + num
        in 10..15 -> 'a' + num - 10
        else -> throw IllegalArgumentException("$num is not an integer between 0 and 15.")
      }

    val dec = chars.toDec()
    val msb = dec / 256
    val sb  = (dec - (256 * msb)) / 16
    val lsb = dec - (256 * msb) - (16 * sb)
    return intArrayOf(msb,sb,lsb)
           .map(::toHex)
           .toCharArray()
  }

  /* <Char[]>.toSixtyFour() converts the receiver object from its hexadecimal representation to a base-64 representation
   *   by converting each chunk of three hexadecimal characters into two base-64 characters.
   * Requires: The receiver object consists of only hexadecimal characters, i.e. only [0-9] and [a-f] as constituents.
   */
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

  /* <Char[]>.toHex() converts the receiver object from its base-64 representation to a hexadecimal representation by
   *   converting each chunk of two base-64 characters into three hexadecimal characters.
   * Requires: The receiver object consists of only base-64 characters, i.e. only [0-9],[a-z],[A-Z], and [+,/] as constituents.
   */
  private fun CharArray.toHex(): CharArray {
    val numGroups = this.size / 2
    val chs = CharArray(3 * numGroups)
    for (i in 0 until numGroups) {
      val sub = this.slice(2 * i until 2 * i + 2).toCharArray()
      val convertedChs = chunkToHex(sub)
      chs[3*i]   = convertedChs[0]
      chs[3*i+1] = convertedChs[1]
      chs[3*i+2] = convertedChs[2]
    }
    val last = this.slice(2 * numGroups until this.size).toCharArray()
    val convertedChs = chunkToHex(last)
    return chs + if (this.size % 2 != 0) convertedChs else CharArray(0) // Parallel behavior to above.
  }

  /* convert(hex) returns the string [hex] in a base-64 representation of itself.
   * Requires: [hex] consists of only hexadecimal characters, i.e. only [0-9] and [a-f] as constituents.
   */
  fun convert(hex: String): String {
    return hex
           .toCharArray()
           .regroup(3)
           .toSixtyFour()
           .regroup(2)
           .joinToString("")
  }

  /* unconvert(base) returns the string [base] in a hexadecimal representation of itself.
   * Requires: [base] consists of only base-64 characters, i.e. only [0-9],[a-z],[A-Z], and [+,/] as constituents.
   */
  fun unconvert(base: String): String {
    return base
           .toCharArray()
           .regroup(2)
           .toHex()
           .regroup(3)
           .joinToString("") // Exactly the opposite process as convert(hex).
  }
}
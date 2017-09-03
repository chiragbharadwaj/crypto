package set1.xor

/* Set 1, Challenge 5: Encrypt some plain text by using a repeating block in an XOR cipher. A little harder than just
 *   plain single-character XOR ciphering. */
object RepeatXor {
  /* <String>.toHex() converts the receiver object from an ASCII representation to the equivalent hex representation.
   * Requires: The receiver object is an ASCII-encoded (7-bit) string.
   */
  private fun String.toHex(): String =
    this.map { "%02x".format(it.toInt()) }.joinToString("")

  /* encrypt(str, cipher) encrypts the plain text [msg] by using [cipher] as a repeating cipher key.
   * Requires: [str] and [cipher] are ASCII-encoded (7-bit) strings.
   */
  fun encrypt(str: String, cipher: String): String {
    val msg = str.toHex()
    val key = cipher.toHex()
    val numSequences = str.length / cipher.length
    val remainder = (str.length % cipher.length) * 2
    val cipherKey = key.repeat(numSequences) + if (remainder != 0) key.slice(0 until remainder) else ""
    return FixedXor.join(msg, cipherKey)
  }
}
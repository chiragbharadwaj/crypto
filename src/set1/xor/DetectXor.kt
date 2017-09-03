package set1.xor

import java.io.File

import set1.xor.SingleXor.score

/* Set 1, Challenge 4: A file containing hundreds of hex strings is provided. Exactly one of them has been ciphered
 *   using the single-character XOR cipher from Set 1, Challenge 3. Find the plain text of that ciphered string. */
object DetectXor {
  /* detect(filename) detects which string is most likely to be the ciphered one by scoring each one and picking the
   *   one with the highest score overall in the file.
   * Requires: [filename] is the fully-qualified name of a valid file which contains strings separated by newlines. Each
   *           string in the file consists of only hexadecimal characters, i.e. only [0-9] and [a-f] as constituents.
   */
  fun detect(filename: String): String {
    val bufferedReader = File(filename).bufferedReader()
    val text = bufferedReader.use { it.readLines() } // Try-with-resources, Kotlin-style.

    var max = 0
    var result = ""

    for (line in text) {
      val (_, plain) = SingleXor.decrypt(line)
      var score = plain.score()
      if (score > max) {
        max = score
        result = plain
      }
    }

    return result
  }
}
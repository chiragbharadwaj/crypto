package test

import org.junit.Test
import set1.Hex

/* Set 1: The basics of cryptography. */
class Basics {

  // Challenge 1: Randomized and edge-case testing.
  @Test
  fun testHex() {
    val orig = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d"
    val final = Hex.convert(orig)
    println("Base-16: $orig")
    println("Base-64: $final")
  }
}
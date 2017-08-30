package test

import org.junit.Test
import org.junit.Assert.*

import set1.hex.Hex
import set1.xor.FixedXor

/* Set 1: The basics of cryptography. */
class Basics {

  // Challenge 1: Converting hex strings into their base-64 equivalents.
  @Test
  fun testHex() {
    val original = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d"
    val expected = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t"
    val actual   = Hex.convert(original)
    assertEquals(expected, actual)
  }

  // Challenge 2: XOR two equal-length hex strings together, bit by bit.
  @Test
  fun testFixedXor() {
    val string1  = "1c0111001f010100061a024b53535009181c"
    val string2  = "686974207468652062756c6c277320657965"
    val expected = "746865206b696420646f6e277420706c6179"
    val actual   = FixedXor.join(string1, string2)
    assertEquals(expected, actual)
  }
}
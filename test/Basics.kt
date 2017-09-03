package test

import org.junit.Test
import org.junit.Assert.*

import set1.hex.Hex
import set1.xor.FixedXor
import set1.xor.SingleXor
import set1.xor.DetectXor
import set1.xor.RepeatXor

/* Set 1: The basics of cryptography. */
class Basics {

  // Challenge 1: Convert a hex string into its base-64 equivalent.
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

  // Challenge 3: Plain text has been XOR-ciphered with a single ASCII character. Recover both the cipher key and plain text.
  @Test
  fun testSingleXor() {
    val cipherText = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736"
    val expectedKey = 'X'
    val expectedPlainText = "Cooking MC's like a pound of bacon"
    val (actualKey, actualPlainText) = SingleXor.decrypt(cipherText)
    assertEquals(expectedKey, actualKey)
    assertEquals(expectedPlainText, actualPlainText)
  }

  // Challenge 4: A file of 327 strings is provided. Exactly one has been single-character XOR-ciphered. Find it.
  @Test
  fun testDetectXor() {
    val file = "src/set1/data/detectXor.txt"
    val expectedPlainText = "Now that the party is jumping\n"
    val actualPlainText = DetectXor.detect(file)
    assertEquals(expectedPlainText, actualPlainText)
  }

  // Challenge 5: Encipher plain text using XOR-ciphering with repeating blocks of multiple-character ASCII text.
  @Test
  fun testRepeatXor() {
    val msg = "Burning 'em, if you ain't quick and nimble\n" +
              "I go crazy when I hear a cymbal"
    val expectedCipherText = "0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272" +
                             "a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f"
    val actualCipherText = RepeatXor.encrypt(msg, "ICE")
    assertEquals(expectedCipherText, actualCipherText)
  }
}
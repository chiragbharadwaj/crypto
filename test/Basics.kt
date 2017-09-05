package test

import org.junit.Test
import org.junit.Assert.*

import java.io.File

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
    val file = "src/set1/data/detectSingleXor.txt"
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

  // Challenge 6: Decipher cipher text created via XOR-ciphering with repeating blocks of multiple-character ASCII text.
  @Test
  fun testBreakRepeatXor() {
    val filename = "src/set1/data/breakRepeatXor.txt"
    val msg = File(filename).readText()
    val expectedKey = "Terminator X: Bring the noise"
    val expectedPlainText =
      """
        |I'm back and I'm ringin' the bell 
        |A rockin' on the mike while the fly girls yell 
        |In ecstasy in the back of me 
        |Well that's my DJ Deshay cuttin' all them Z's 
        |Hittin' hard and the girlies goin' crazy 
        |Vanilla's on the mike, man I'm not lazy. 

        |I'm lettin' my drug kick in 
        |It controls my mouth and I begin 
        |To just let it flow, let my concepts go 
        |My posse's to the side yellin', Go Vanilla Go! 

        |Smooth 'cause that's the way I will be 
        |And if you don't give a damn, then 
        |Why you starin' at me 
        |So get off 'cause I control the stage 
        |There's no dissin' allowed 
        |I'm in my own phase 
        |The girlies sa y they love me and that is ok 
        |And I can dance better than any kid n' play 

        |Stage 2 -- Yea the one ya' wanna listen to 
        |It's off my head so let the beat play through 
        |So I can funk it up and make it sound good 
        |1-2-3 Yo -- Knock on some wood 
        |For good luck, I like my rhymes atrocious 
        |Supercalafragilisticexpialidocious 
        |I'm an effect and that you can bet 
        |I can take a fly girl and make her wet. 

        |I'm like Samson -- Samson to Delilah 
        |There's no denyin', You can try to hang 
        |But you'll keep tryin' to get my style 
        |Over and over, practice makes perfect 
        |But not if you're a loafer. 

        |You'll get nowhere, no place, no time, no girls 
        |Soon -- Oh my God, homebody, you probably eat 
        |Spaghetti with a spoon! Come on and say it! 

        |VIP. Vanilla Ice yep, yep, I'm comin' hard like a rhino 
        |Intoxicating so you stagger like a wino 
        |So punks stop trying and girl stop cryin' 
        |Vanilla Ice is sellin' and you people are buyin' 
        |'Cause why the freaks are jockin' like Crazy Glue 
        |Movin' and groovin' trying to sing along 
        |All through the ghetto groovin' this here song 
        |Now you're amazed by the VIP posse. 

        |Steppin' so hard like a German Nazi 
        |Startled by the bases hittin' ground 
        |There's no trippin' on mine, I'm just gettin' down 
        |Sparkamatic, I'm hangin' tight like a fanatic 
        |You trapped me once and I thought that 
        |You might have it 
        |So step down and lend me your ear 
        |'89 in my time! You, '90 is my year. 

        |You're weakenin' fast, YO! and I can tell it 
        |Your body's gettin' hot, so, so I can smell it 
        |So don't be mad and don't be sad 
        |'Cause the lyrics belong to ICE, You can call me Dad 
        |You're pitchin' a fit, so step back and endure 
        |Let the witch doctor, Ice, do the dance to cure 
        |So come up close and don't be square 
        |You wanna battle me -- Anytime, anywhere 

        |You thought that I was weak, Boy, you're dead wrong 
        |So come on, everybody and sing this song 

        |Say -- Play that funky music Say, go white boy, go white boy go 
        |play that funky music Go white boy, go white boy, go 
        |Lay down and boogie and play that funky music till you die. 

        |Play that funky music Come on, Come on, let me hear 
        |Play that funky music white boy you say it, say it 
        |Play that funky music A little louder now 
        |Play that funky music, white boy Come on, Come on, Come on 
        |Play that funky music 
      """.trimMargin()
    val (actualKey, actualPlainText) = RepeatXor.decrypt(msg)
    assertEquals(expectedKey, actualKey)
    assertEquals(expectedPlainText, actualPlainText)
  }
}
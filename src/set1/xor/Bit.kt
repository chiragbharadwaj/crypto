package set1.xor

import set1.xor.Bit.*

/* Abstract representation of a bit. Uses only constants with no assigned values (slightly more annoying conversion). */
enum class Bit { ZERO, ONE }

/* Overrides the ! operator for bits. !ZERO = ONE and !ONE = ZERO. Purely syntactic sugar. */
operator fun Bit.not() =
  when (this) {
    ZERO -> ONE
    ONE  -> ZERO
  }
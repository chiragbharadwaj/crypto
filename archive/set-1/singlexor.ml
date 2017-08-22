open Fixedxor

module type CIPHER = sig
	
	val encrypt : string -> string

	val decrypt : string -> string

end

module SingleXor : CIPHER = struct

	let gen_hex () : string = (* Map to corresponding string *)
		match Random.int 16 with
		| 10 -> "a"
		| 11 -> "b"
		| 12 -> "c"
		| 13 -> "d"
		| 14 -> "e"
		| 15 -> "f"
		| i when abs i <= 9 -> string_of_int i
		| _ -> failwith "22: This should not happen."

	let score (s : string) : int = 0

	let encrypt (plaintext : string) : string = ""

	let decrypt (ciphertext : string) : string = ""

end
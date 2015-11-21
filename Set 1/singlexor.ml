open Fixedxor

module type CIPHER = sig
	
	val encrypt : string -> string

	val decrypt : string -> string

end

module SingleXor : CIPHER = struct

	let score (s : string) : int = 0

	let encrypt (plaintext : string) : string = ""

	let decrypt (ciphertext : string) : string = ""

end
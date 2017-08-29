(* The only thing that is publicly visible is the xor function, which is the
 * type of abstraction we are going for: the implementaiton is NOT visible. *)
module type XOR = sig

	(* [xor (s1, s2)] returns a string that corresponds to the logical XOR of
	 *  the two input strings s1 and s2. Requires: s1 and s2 are hex-encoded.
	 *
	 *  - [s1] is a hex-encoded string.
	 *  - [s2] is another hex-encoded string that s1 is logical XOR'ed with.
	 *)
	val xor : string * string -> string

end

(* The actual internal implementation of the XOR signature. These helper
 * functions are not seen from outside, and are not usable out of this scope. *)
module FixedXOR : XOR = struct

	(* Simple type to encode binary numbers, useful in limiting conversion. *)
	type binary = Zero | One

	(* [to_binary i] converts an integer i to its corresponding binary construct
	 *  by invoking the constructor. Requires: i = 0 or i = 1. Throws an
	 *  exception if i is not either 0 or 1.
	 *
	 *  - [i] is an integer representation of a binary number.
	 *)
	let to_binary (i : int) : binary =
		match i with
		| 0 -> Zero
		| 1 -> One
		| _ -> failwith "19: This should not happen." (* Not a binary number! *)

	(* [to_decimal b] converts a binary encoding b to its corresponding integer
	 *  value by reverse-matching the construct.
	 *
	 *  - [b] is a 1-bit binary encoding of an integer value.
	 *)
	let to_decimal (b : binary) : int =
		match b with
		| Zero -> 0
		| One  -> 1
		(* No edge cases, because b has only two variant values! *)

	(* [bitxor (b1, b2)] computes the XOR of two 1-bit binary encodings b1 and
	 *  b2, and outputs this value as a binary encoding as well.
	 *
	 *  - [b1] is a 1-bit binary encoding of an integer value.
	 *  - [b2] is a 1-bit binary encoding of an integer value to be XORed with
	 *         b1.
	 *)
	let bitxor ((b1, b2) : binary * binary) : binary =
		match (b1, b2) with
		| (Zero, _) -> b2
		| (One, _)  -> to_binary ( ( (to_decimal b2) + 1 ) mod 2 )

	(* [zero_pad b_list] pads the 4-bit binary encoding b_list (passed in as a
	 *  list of binary encodings) with 0's in the MSBs if the encoded list is
	 *  fewer than 4 bits in length. Throws an exception if b_list has more than
	 *  4 elements. 
	 *
	 *  - [b_list] is the list encoding of a 4-bit binary number, to be padded.
	 *             NOTE: [a; b; c; d] corresponds to the number abcd, NOT dcba.
	 *)
	let zero_pad (b_list : binary list) : binary list =
		match List.length b_list with
		| 0 -> []
		| 1 -> Zero :: Zero :: Zero :: b_list
		| 2 -> Zero :: Zero :: b_list
		| 3 -> Zero :: b_list
		| 4 -> b_list
		| _ -> failwith "37: This should not happen."

	(* [ungroup c] expands the hexadecimal character c to its corresponding 4-bit
	 *  binary encoding, returned as a list. The result is zero-padded to 4 bits
	 *  if it is necessary.
	 *
	 *  - [c] is a hexadecimal character to be expanded into a 4-bit encoding.
	 *)
	let ungroup (c : char) : binary list =
		let rec bin (i : int) : binary list =
			if (i = i mod 2) then (to_binary i) :: []
				else bin (i / 2) @ [to_binary (i mod 2)]
		in
		let hex (ch : char) : int =	
			match ch with
			| 'a' -> 10
			| 'b' -> 11
			| 'c' -> 12
			| 'd' -> 13
			| 'e' -> 14
			| 'f' -> 15
			| x when abs (Char.code x - Char.code '0') <= 9 ->
				int_of_string (Char.escaped ch)
			| _   -> failwith "54: This should not happen."
		in
			zero_pad (bin (hex c))

	(* [regroup b_list] regroups the 4-bit binary encoding b_list, passed in as
	 *  a list, as a hexadecimal character.
	 *
	 *  - [b_list] is a list encoding of a 4-bit binary number.
	 *)
	let regroup (b_list : binary list) : char =
		let num =
			List.fold_left (fun acc x -> (to_decimal x) + (2 * acc)) 0 b_list in
		let map_over (numerical : int) : char = (* Map to corresponding char *)
			match numerical with
			| 10 -> 'a'
			| 11 -> 'b'
			| 12 -> 'c'
			| 13 -> 'd'
			| 14 -> 'e'
			| 15 -> 'f'
			| x when abs x <= 9 -> String.get (string_of_int x) 0
			| _ -> failwith "69: This should not happen."
		in
			map_over num

	(* [c ^^ s] returns a string in which the character c has been prepended to
	 *  the string s.
	 *
	 *  - [c] is a character to be prepended to the string.
 	 *  - [s] is a string of characters.
 	 *)
	let (^^) (c : char) (s : string) : string = (Char.escaped c) ^ s

	(* [string_of_char c_list] returns a string created from concatenating all
	 *  the characters in c_list together in order of appearance in the string.
 	 *
 	 *  - [c_list] is a list of characters to be strung together.
 	 *)
	let string_of_char (c_list : char list) : string = 
		List.fold_right (^^) c_list "" (* Makes concatenation much easier. *)

	(* [char_of_string s] returns the string s as a list of characters in order
	 *  of appearance in the string.
	 *
	 *  - [s] is a string to be converted to a list of characters in order.
	 *)
	let rec char_of_string (s : string) : char list =
		let len = String.length s in 
		match len with
		| 0 -> [] (* A quick edge/base case in case "" is passed in. *)
		| 1 -> (String.get s 0) :: [] (* Needed because sub from 1 undefined. *)
		| _ -> (String.get s 0) :: char_of_string ( String.sub s 1 (len - 1) )

	(* [xor (s1, s2)] returns a string that corresponds to the logical XOR of
	 *  the two input strings s1 and s2. Requires: s1 and s2 are hex-encoded.
	 *
	 *  - [s1] is a hex-encoded string.
	 *  - [s2] is another hex-encoded string that s1 is logical XOR'ed with.
	 *)
	let xor ((s1, s2) : string * string) : string =
		let bl1 = List.flatten (List.map ungroup (char_of_string s1)) in
		let bl2 = List.flatten (List.map ungroup (char_of_string s2)) in
		let b_list =
			try List.fold_left2
				(fun acc b1 b2 -> (acc @ [bitxor (b1, b2)]))[] bl1 bl2
				(* XOR bit by bit, fold over list and create a new list! *)
			with | _ -> failwith "106: This should not happen."
		in (* List.fold_left2 will catch uneven-length error, thankfully! *)
		let rec recover (bl : binary list) : char list =
			match bl with
			| [] -> []
			| b1 :: b2 :: b3 :: b4 :: rest -> (* Regroup and recover. *)
				(regroup (b1 :: b2 :: b3 :: b4 :: [])) :: recover rest
			| b1 :: b2 :: b3 :: [] as b -> (regroup (zero_pad b)) :: []
			| b1 :: b2 :: [] as b -> (regroup (zero_pad b)) :: []
			| b1 :: [] as b -> (regroup (zero_pad b)) :: []
		in string_of_char (recover b_list) (* Convert back to string at end. *)
end

(* Runs a command-line interface that prints out the base-64 string given via
 *  command-line arguments an input that represents a hex string. *)
let () =
	let s  = Array.to_list Sys.argv in
	try
		let s1 = List.nth s 1 in
		let s2 = List.nth s 2 in (* Get both strings for computation. *)
		print_string ("\n" ^ (FixedXOR.xor (s1, s2)) ^ "\n\n")
	with
		| _ -> print_string "Error: Bad input! Perhaps you made a typo?\n"

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
	 *  by invoking the constructor. Requires: i = 0 or i = 1.
	 *
	 *  - [i] is an integer representation of a binary number.
	 *)
	let to_binary (i : int) : binary =
		match i with
		| 0 -> Zero
		| 1 -> One
		| _ -> failwith "19: This should not happen." (* Not a binary number! *)

	let to_decimal (b : binary) : int =
		match b with
		| Zero -> 0
		| One  -> 1

	let bitxor ((b1, b2) : binary * binary) : binary =
		match (b1, b2) with
		| (Zero, _) -> b2
		| (One, _)  -> to_binary ( ( (to_decimal b2) + 1 ) mod 2 )

	let zero_pad (b_list : binary list) : binary list =
		match List.length b_list with
		| 1 -> Zero :: Zero :: Zero :: b_list
		| 2 -> Zero :: Zero :: b_list
		| 3 -> Zero :: b_list
		| 4 -> b_list
		| _ -> failwith "37: This should not happen."

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
			with | _ -> failwith "106: This should not happen."
		in (* List.fold_left2 will catch uneven-length error, thankfully! *)
		let rec recover (bl : binary list) : char list =
			match bl with
			| [] -> []
			| b1 :: b2 :: b3 :: b4 :: rest ->
				(regroup (b1 :: b2 :: b3 :: b4 :: [])) :: recover rest
			| b1 :: b2 :: b3 :: [] as b -> (regroup (zero_pad b)) :: []
			| b1 :: b2 :: [] as b -> (regroup (zero_pad b)) :: []
			| b1 :: [] as b -> (regroup (zero_pad b)) :: []
		in string_of_char (recover b_list)
end

(* Runs a command-line interface that prints out the base-64 string given via
 *  command-line arguments an input that represents a hex string. *)
let () =
	let s  = Array.to_list Sys.argv in
	try
		let s1 = List.nth s 1 in
		let s2 = List.nth s 2 in
		print_string ("\n" ^ (FixedXOR.xor (s1, s2)) ^ "\n\n")
	with
		| _ -> print_string "Error: Bad input! Perhaps you made a typo?\n"

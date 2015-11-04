module type XOR = sig

	val xor : string * string -> string

end

module FixedXOR : XOR = struct

	type binary = Zero | One

	let to_binary (i : int) : binary =
		match i with
		| 0 -> Zero
		| 1 -> One
		| _ -> failwith "15: This should not happen."

	let to_decimal (b : binary) : int =
		match b with
		| Zero -> 0
		| One  -> 1

	let bitxor ((b1, b2) : binary * binary) : int =
		match (b1, b2) with
		| (Zero, _) -> to_decimal b2                 (* 0 ^ B = B *)
		| (One, _)  -> ( (to_decimal b2) + 1 ) mod 2 (* 1 ^ B = ~B = (B+1)%2 *)

	let zero_pad (b_list : binary list) : binary list =
		match List.length b_list with
		| 1 -> Zero :: Zero :: Zero :: b_list
		| 2 -> Zero :: Zero :: b_list
		| 3 -> Zero :: b_list
		| 4 -> b_list
		| _ -> failwith "33: This should not happen."

	let ungroup (c : char) : binary list =
		let rec bin (i : int) : binary list =
			if (i = i mod 2) then (to_binary i) :: [] else bin (i / 2) @ [to_binary (i mod 2)]
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
			| _   -> failwith "49: This should not happen."
		in
			zero_pad (bin (hex c))

	let regroup (i_list : int list) : char =
		let num = List.fold_left (fun acc x -> x + (2 * acc)) 0 i_list in
		let map_over (numerical : int) : char = (* Map to corresponding char *)
			match numerical with
			| 10 -> 'a'
			| 11 -> 'b'
			| 12 -> 'c'
			| 13 -> 'd'
			| 14 -> 'e'
			| 15 -> 'f'
			| x when abs x <= 9 -> String.get (string_of_int x) 0
			| _ -> failwith "64: This should not happen."
		in
			map_over num

	let xor ((s1, s2) : string * string) : string =
		if (String.length s1 = String.length s2)
		then
			(* (match 
			) *)
			failwith "LOL YOU SUCK."
		else failwith "74: This should not happen."

end

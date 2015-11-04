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

	let bitxor ((b1, b2) : binary * binary) : binary =
		let result =
			match (b1, b2) with
			| (Zero, _) -> b2
			| (One, _) -> to_binary ( ( (to_decimal b2) + 1 ) mod 2 )
			| _ -> failwith "27: This should not happen."
		in
			to_binary result

	let zero_pad (c_list : int list) : int list =
		match List.length c_list with
		| 1 -> 0 :: 0 :: 0 :: c_list
		| 2 -> 0 :: 0 :: c_list
		| 3 -> 0 :: c_list
		| 4 -> c_list
		| _ -> failwith "37: This should not happen."

	let ungroup (c : char) : int list =
		let rec bin (i : int) : int list =
			if (i = i mod 2) then i :: [] else bin (i / 2) @ [(i mod 2)]
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
			| _   -> failwith "53: This should not happen."
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
			| _ -> failwith "68: This should not happen."
		in
			map_over num

	let xor (s1 : string, s2 : string) : string =
		if (String.length s1 = String.length s2)
		then
			(* (match 
			) *)
			failwith "LOL YOU SUCK."
		else failwith "78: This should not happen."

end

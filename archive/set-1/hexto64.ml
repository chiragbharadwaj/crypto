(* The only thing that is publicly visible is the convert function, which is the
 * type of abstraction we are going for: the implementaiton is NOT visible. *)
module type SIXTY_FOUR = sig

	(* [convert s] returns the string s in a base-64 representation of the same
 	 *  value. Requires: s is input as a base 16 number, i.e. it only contains
 	 *  [0-9] and [a-f] as constituent characters.
 	 *
 	 *  - [s] is a string to be converted to a base-64 representation of itself.
 	 *)
	val convert : string -> string
end

(* The actual internal implementation of the SIXTY_FOUR signature. These helper
 * functions are not seen from outside, and are not usable out of this scope. *)
module SixtyFour : SIXTY_FOUR = struct

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

	(* [to_sixty_four c_list] returns a character list containing base-64
	 *  characters that correspond to the hex characters specified in the given
	 *  character list c_list.
	 *
	 *  - [c_list] is a character list of hex characters to be converted to x64.
	 *)
	let to_sixty_four (c_list : char list) : char list =
		let to_decimal (c : char) : int =
			match c with
			| 'a' -> 10
			| 'b' -> 11
			| 'c' -> 12
			| 'd' -> 13
			| 'e' -> 14
			| 'f' -> 15
			| _   -> int_of_string (Char.escaped c) (* Should be only [0-9]. *)
		in
		let dec =
			List.fold_left (fun acc x -> (to_decimal x) + (16*acc)) 0 c_list in
		let msb = dec / 64 in
		let lsb = dec - (msb) * 64 in
		let map_over (numerical : int) : char = (* Map to corresponding char *)
			match (numerical / 26) with
			| 0 -> Char.chr ((Char.code 'A') + (numerical mod 26))
			| 1 -> Char.chr ((Char.code 'a') + (numerical mod 26))
			| 2 -> (match (numerical mod 26) with
				 	| 10 -> '+'
				 	| 11 -> '/'
				 	| x  -> String.get (string_of_int x) 0) (* Return as char *)
			| _ -> failwith "75: This should not happen." (* 63/26 <= 2 *)
		in List.map map_over [msb; lsb] (* Map to actual character literals. *)

	(* [regroup_three c_list] regroups a character list by reversing the list
	 *  while preserving the order within each block of three characters.
	 *   EXAMPLE: [3; 4; 5; 7; 1; 0; 2] --> [1; 0; 2; 4; 5; 7; 3]
	 *
	 *  - [c_list] is the character list to be regrouped by a 3-partition.
	 *)
	let regroup_three (c_list : char list) : char list =
		let rec regroup (c_list : char list) : char list =
			match c_list with
			| [] -> []
			| x1 :: x2 :: x3 :: rest -> (regroup rest) @ (x1 :: x2 :: x3 :: [])
			| _ -> failwith "89: This should not happen."
		in
			match (List.length c_list) mod 3 with
			| 0 -> regroup c_list
			| 1 -> (regroup c_list) @ ((List.hd c_list) :: [])
			| 2 ->
			  (regroup c_list) @ ((List.hd c_list) :: (List.nth c_list 1) :: [])
			| _ -> failwith "96: This should not happen." (* Base 3 math.*)

	(* [regroup_two c_list] regroups a character list by reversing the list
	 *  while preserving the order within each block of two characters.
	 *   EXAMPLE: [3; 4; 5; 7; 1; 0; 2] --> [0; 2; 7; 1; 4; 5; 3]
	 *
	 *  - [c_list] is the character list to be regrouped by a 2-partition.
	 *)
	let regroup_two (c_list : char list) : char list =
		let rec regroup (c_list : char list) : char list =
			match c_list with
			| [] -> []
			| x1 :: x2 :: rest -> (regroup rest) @ (x1 :: x2 :: [])
			| _ -> failwith "109: This should not happen."
		in
			match (List.length c_list) mod 2 with
			| 0 -> regroup c_list
			| 1 -> (regroup c_list) @ ((List.hd c_list) :: [])
			| _ -> failwith "114: This should not happen." (* Base 2 math. *)

	(* [convert s] returns the string s in a base-64 representation of the same
	 *  value. Requires: s is input as a base 16 number, i.e. it only contains
	 *  [0-9] and [a-f] as constituent characters.
	 *
 	 *  - [s] is a string to be converted to a base-64 representation of itself.
 	 *)
	let convert (s : string) : string =
		let lst = regroup_three (char_of_string s) in (* Regroup for now. *)
			let rec helper (c_list : char list) : char list =
				match c_list with
				| [] -> []
				| x1 :: x2 :: x3 :: rest ->
					(to_sixty_four (x1 :: x2 :: x3 :: [])) @ (helper rest)
				| (x1 :: x2 :: []) as xs -> to_sixty_four xs
				| (x :: []) as xs        -> to_sixty_four xs
			in string_of_char (regroup_two (helper lst)) (* Convert to string *)
end

(* Runs a command-line interface that prints out the base-64 string given via
 *  command-line arguments an input that represents a hex string. *)
let () =
	let s = List.nth (Array.to_list Sys.argv) 1 in
		try print_string ("\n" ^ (SixtyFour.convert s) ^ "\n\n") with
		  | _ -> print_string "Error: Bad input! Perhaps you made a typo?\n"

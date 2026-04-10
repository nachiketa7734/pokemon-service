package com.bolttedex.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.bolttedex.dto.PokemonDetailResponse;
import com.bolttedex.dto.PokemonListResponse;
import com.bolttedex.dto.PokemonSpeciesResponse;
import com.bolttedex.dto.TypeResponse;

@FeignClient(name = "pokeApiClient", url = "${pokeapi.base.url}")
public interface PokeApiClient {

	// 1. Get Pokemon List
	@GetMapping("/pokemon")
	PokemonListResponse getPokemonList(@RequestParam("offset") int offset, @RequestParam("limit") int limit);

	// 2. Get Pokemon Details
	@GetMapping("/pokemon/{id}")
	PokemonDetailResponse getPokemonDetails(@PathVariable("id") int id);

	// 3. Get Pokemon Species (for region)
	@GetMapping("/pokemon-species/{id}")
	PokemonSpeciesResponse getPokemonSpecies(@PathVariable int id);

	// 4. Get Type Details (for weaknesses)
	@GetMapping("/type/{type}")
	TypeResponse getTypeDetails(@PathVariable String type);
}
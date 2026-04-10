package com.bolttedex.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bolttedex.dto.PokemonDetailDTO;
import com.bolttedex.dto.PokemonSummaryDTO;
import com.bolttedex.service.PokemonService;

@RestController
@RequestMapping("/api/pokemons") 
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public ResponseEntity<List<PokemonSummaryDTO>> getPokemons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<PokemonSummaryDTO> response =
                pokemonService.getPokemonList(page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonDetailDTO> getPokemonDetails(
            @PathVariable int id
    ) {
        PokemonDetailDTO response =
                pokemonService.getPokemonDetails(id);

        return ResponseEntity.ok(response);
    }
}
package com.bolttedex.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bolttedex.service.PokemonService;

@Component
public class PokemonScheduler {

	private final PokemonService pokemonService;

	public PokemonScheduler(PokemonService pokemonService) {
		this.pokemonService = pokemonService;
	}

	@Scheduled(fixedRateString = "${scheduler.pokemon.refresh.rate}")
	public void refreshPokemonCache() {

		System.out.println("Starting Pokemon cache refresh...");

		try {
			for (int page = 0; page < 5; page++) {
				pokemonService.getPokemonList(page, 20);
			}

			for (int id = 1; id <= 20; id++) {
				pokemonService.getPokemonDetails(id);
			}

			System.out.println("Pokemon cache refresh completed.");

		} catch (Exception e) {
			System.out.println("Error during cache refresh: " + e.getMessage());
		}
	}
}

package com.bolttedex.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bolttedex.client.PokeApiClient;
import com.bolttedex.dto.PokemonDetailDTO;
import com.bolttedex.dto.PokemonDetailResponse;
import com.bolttedex.dto.PokemonListResponse;
import com.bolttedex.dto.PokemonSpeciesResponse;
import com.bolttedex.dto.PokemonSummaryDTO;
import com.bolttedex.dto.TypeResponse;
import com.bolttedex.exception.PokemonNotFoundException;
import com.bolttedex.mapper.PokemonMapper;

@Service
public class PokemonService {

	private final PokeApiClient pokeApiClient;
	private final Executor executor;

	public PokemonService(PokeApiClient pokeApiClient, @Qualifier("taskExecutor") Executor executor) {
		this.pokeApiClient = pokeApiClient;
		this.executor = executor;
	}

	@Cacheable(value = "pokemonList", key = "#page")
	public List<PokemonSummaryDTO> getPokemonList(int page, int size) {

		int offset = page * size;

		PokemonListResponse listResponse = pokeApiClient.getPokemonList(offset, size);

		return listResponse.getResults().stream().map(result -> extractIdFromUrl(result.getUrl()))
				.map(pokeApiClient::getPokemonDetails).map(PokemonMapper::toSummaryDTO).toList();
	}

	@Cacheable(value = "pokemonDetails", key = "#id")
	public PokemonDetailDTO getPokemonDetails(int id) {

		try {
			CompletableFuture<PokemonDetailResponse> detailFuture = CompletableFuture
					.supplyAsync(() -> safeGetPokemonDetails(id), executor);

			CompletableFuture<PokemonSpeciesResponse> speciesFuture = CompletableFuture
					.supplyAsync(() -> safeGetPokemonSpecies(id), executor);

			CompletableFuture.allOf(detailFuture, speciesFuture).join();

			PokemonDetailResponse detail = detailFuture.join();
			PokemonSpeciesResponse species = speciesFuture.join();

			if (detail == null) {
				throw new PokemonNotFoundException("Pokemon not found with id: " + id);
			}

			List<String> types = detail.getTypes().stream().map(t -> t.getType().getName()).toList();

			List<String> weaknesses = getWeaknessesParallel(types);

			return PokemonMapper.toDetailDTO(detail, species, weaknesses);

		} catch (PokemonNotFoundException ex) {
			throw ex;
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch Pokemon details for id: " + id, e);
		}
	}

	private PokemonDetailResponse safeGetPokemonDetails(int id) {
		try {
			return pokeApiClient.getPokemonDetails(id);
		} catch (Exception e) {
			throw new PokemonNotFoundException("Pokemon not found with id: " + id);
		}
	}

	private PokemonSpeciesResponse safeGetPokemonSpecies(int id) {
		try {
			return pokeApiClient.getPokemonSpecies(id);
		} catch (Exception e) {
			return null;
		}
	}

	private List<String> getWeaknessesParallel(List<String> types) {

		List<CompletableFuture<Set<String>>> futures = types.stream()
				.map((String type) -> CompletableFuture.supplyAsync(() -> {
					try {
						TypeResponse response = pokeApiClient.getTypeDetails(type);

						return response.getDamage_relations().getDouble_damage_from().stream().map(t -> t.getName())
								.collect(Collectors.toSet());

					} catch (Exception e) {
						// fail-safe: return empty instead of breaking flow
						return Set.<String>of();
					}
				}, executor)).toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		return futures.stream().flatMap(f -> f.join().stream()).distinct().toList();
	}

	private int extractIdFromUrl(String url) {
		String[] parts = url.split("/");
		return Integer.parseInt(parts[parts.length - 1]);
	}
}

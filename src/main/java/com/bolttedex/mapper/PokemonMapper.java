package com.bolttedex.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.bolttedex.dto.PokemonDetailDTO;
import com.bolttedex.dto.PokemonDetailResponse;
import com.bolttedex.dto.PokemonSpeciesResponse;
import com.bolttedex.dto.PokemonSummaryDTO;

public class PokemonMapper {

	public static PokemonSummaryDTO toSummaryDTO(PokemonDetailResponse response) {

		return PokemonSummaryDTO.builder().id(response.getId()).name(response.getName())
				.image(response.getSprites().getFront_default())
				.types(response.getTypes().stream().map(t -> t.getType().getName()).collect(Collectors.toList()))
				.build();
	}

	public static PokemonDetailDTO toDetailDTO(PokemonDetailResponse detail, PokemonSpeciesResponse species,
			List<String> weaknesses) {
		PokemonDetailDTO dto = new PokemonDetailDTO();

		dto.setId(detail.getId());
		dto.setName(detail.getName());
		dto.setFrontImage(detail.getSprites().getFront_default());
		dto.setBackImage(detail.getSprites().getBack_default());

		dto.setTypes(detail.getTypes().stream().map(t -> t.getType().getName()).toList());

		dto.setRegion(mapRegion(species.getGeneration().getName()));
		dto.setWeaknesses(weaknesses);

		return dto;
	}

	private static String mapRegion(String generation) {
		return switch (generation) {
		case "generation-i" -> "kanto";
		case "generation-ii" -> "johto";
		case "generation-iii" -> "hoenn";
		default -> "unknown";
		};
	}

}

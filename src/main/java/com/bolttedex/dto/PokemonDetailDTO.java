package com.bolttedex.dto;

import java.util.List;

import lombok.Data;

@Data
public class PokemonDetailDTO {

	private int id;
	private String name;
	private String frontImage;
	private String backImage;
	private List<String> types;
	private String region;
	private List<String> weaknesses;

}

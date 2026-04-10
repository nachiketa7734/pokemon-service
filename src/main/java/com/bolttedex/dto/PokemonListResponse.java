package com.bolttedex.dto;

import java.util.List;

import lombok.Data;

@Data
public class PokemonListResponse {
    private List<PokemonResult> results;

    @Data
    public static class PokemonResult {
        private String name;
        private String url;
    }
}

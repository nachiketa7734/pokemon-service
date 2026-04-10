package com.bolttedex.dto;

import lombok.Data;

@Data
public class PokemonSpeciesResponse {

    private Generation generation;

    @Data
    public static class Generation {
        private String name;
    }
}

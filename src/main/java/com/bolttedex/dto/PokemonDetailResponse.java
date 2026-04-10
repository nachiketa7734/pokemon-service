package com.bolttedex.dto;
import lombok.Data;
import java.util.List;

@Data
public class PokemonDetailResponse {

    private int id;
    private String name;
    private Sprites sprites;
    private List<TypeWrapper> types;

    @Data
    public static class Sprites {
        private String front_default;
        private String back_default;
    }

    @Data
    public static class TypeWrapper {
        private Type type;
    }

    @Data
    public static class Type {
        private String name;
    }
}

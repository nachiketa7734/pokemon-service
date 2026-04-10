package com.bolttedex.dto;

import java.util.List;

import lombok.Data;

@Data
public class TypeResponse {

    private DamageRelations damage_relations;

    @Data
    public static class DamageRelations {
        private List<Type> double_damage_from;
    }

    @Data
    public static class Type {
        private String name;
    }
}

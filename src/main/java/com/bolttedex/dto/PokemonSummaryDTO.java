package com.bolttedex.dto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PokemonSummaryDTO {
    private int id;
    private String name;
    private String image;
    private List<String> types;
}

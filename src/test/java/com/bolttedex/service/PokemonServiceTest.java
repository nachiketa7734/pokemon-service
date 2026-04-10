package com.bolttedex.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.bolttedex.client.PokeApiClient;
import com.bolttedex.dto.*;
import com.bolttedex.exception.PokemonNotFoundException;

class PokemonServiceTest {

    @Mock
    private PokeApiClient pokeApiClient;

    @Mock
    private Executor executor;

    @InjectMocks
    private PokemonService pokemonService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        //async run synchronously in tests
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any());
    }


    @Test
    void testGetPokemonList_success() {

        // Mock list response
        PokemonListResponse.PokemonResult result = new PokemonListResponse.PokemonResult();
        result.setUrl("https://pokeapi.co/api/v2/pokemon/1/");

        PokemonListResponse listResponse = new PokemonListResponse();
        listResponse.setResults(List.of(result));

        // Mock detail response
        PokemonDetailResponse detail = new PokemonDetailResponse();
        detail.setId(1);
        detail.setName("bulbasaur");

        PokemonDetailResponse.Sprites sprites = new PokemonDetailResponse.Sprites();
        sprites.setFront_default("img");
        detail.setSprites(sprites);

        PokemonDetailResponse.TypeWrapper typeWrapper = new PokemonDetailResponse.TypeWrapper();
        PokemonDetailResponse.Type type = new PokemonDetailResponse.Type();
        type.setName("grass");
        typeWrapper.setType(type);

        detail.setTypes(List.of(typeWrapper));

        when(pokeApiClient.getPokemonList(0, 10)).thenReturn(listResponse);
        when(pokeApiClient.getPokemonDetails(1)).thenReturn(detail);

        // Execute
        List<PokemonSummaryDTO> resultList = pokemonService.getPokemonList(0, 10);

        // Verify
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals("bulbasaur", resultList.get(0).getName());
    }


    @Test
    void testGetPokemonDetails_success() {

        // Mock detail response
        PokemonDetailResponse detail = new PokemonDetailResponse();
        detail.setId(1);
        detail.setName("bulbasaur");

        PokemonDetailResponse.Sprites sprites = new PokemonDetailResponse.Sprites();
        sprites.setFront_default("front");
        sprites.setBack_default("back");
        detail.setSprites(sprites);

        PokemonDetailResponse.TypeWrapper typeWrapper = new PokemonDetailResponse.TypeWrapper();
        PokemonDetailResponse.Type type = new PokemonDetailResponse.Type();
        type.setName("grass");
        typeWrapper.setType(type);
        detail.setTypes(List.of(typeWrapper));

        // Mock species
        PokemonSpeciesResponse species = new PokemonSpeciesResponse();
        PokemonSpeciesResponse.Generation gen = new PokemonSpeciesResponse.Generation();
        gen.setName("generation-i");
        species.setGeneration(gen);

        // Mock type response
        TypeResponse typeResponse = new TypeResponse();
        TypeResponse.DamageRelations damage = new TypeResponse.DamageRelations();

        TypeResponse.Type weakType = new TypeResponse.Type();
        weakType.setName("fire");

        damage.setDouble_damage_from(List.of(weakType));
        typeResponse.setDamage_relations(damage);

        when(pokeApiClient.getPokemonDetails(1)).thenReturn(detail);
        when(pokeApiClient.getPokemonSpecies(1)).thenReturn(species);
        when(pokeApiClient.getTypeDetails("grass")).thenReturn(typeResponse);

        // Execute
        PokemonDetailDTO result = pokemonService.getPokemonDetails(1);

        // Verify
        assertNotNull(result);
        assertEquals("bulbasaur", result.getName());
        assertTrue(result.getWeaknesses().contains("fire"));
    }


    @Test
    void testGetPokemonDetails_notFound() {


        // Verify exception
        assertThrows(PokemonNotFoundException.class,
                () -> pokemonService.getPokemonDetails(999));
    }
}
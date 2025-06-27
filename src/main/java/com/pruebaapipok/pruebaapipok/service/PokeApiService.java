package com.pruebaapipok.pruebaapipok.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PokeApiService {

    private final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/ditto";
    private final RestTemplate restTemplate;

    public PokeApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getDittoPokemon() {
        return restTemplate.getForObject(POKEAPI_URL, String.class);
    }
}
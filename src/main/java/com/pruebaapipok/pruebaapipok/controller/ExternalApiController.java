package com.pruebaapipok.pruebaapipok.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebaapipok.pruebaapipok.service.PokeApiService;

@RestController
@RequestMapping("/api/external")
public class ExternalApiController {

    @Autowired
    private PokeApiService pokeApiService;

    @GetMapping("/pokemon/ditto")
    public ResponseEntity<String> getDitto() {
        String response = pokeApiService.getDittoPokemon();
        return ResponseEntity.ok(response);
    }
}
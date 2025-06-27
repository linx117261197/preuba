package com.pruebaapipok.pruebaapipok.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebaapipok.pruebaapipok.service.EncryptionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public ResponseEntity<Map<String, String>> encrypt(@RequestBody Map<String, String> request) {
        String plainText = request.get("text");
        Map<String, String> response = new HashMap<>();
        if (plainText == null || plainText.isEmpty()) {
            response.put("error", "El texto a cifrar no puede estar vacío.");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String encryptedText = encryptionService.encrypt(plainText);
            response.put("originalText", plainText);
            response.put("encryptedText", encryptedText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al cifrar el texto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<Map<String, String>> decrypt(@RequestBody Map<String, String> request) {
        String encryptedText = request.get("text");
        Map<String, String> response = new HashMap<>();
        if (encryptedText == null || encryptedText.isEmpty()) {
            response.put("error", "El texto a descifrar no puede estar vacío.");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            String decryptedText = encryptionService.decrypt(encryptedText);
            response.put("encryptedText", encryptedText);
            response.put("decryptedText", decryptedText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error al descifrar el texto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
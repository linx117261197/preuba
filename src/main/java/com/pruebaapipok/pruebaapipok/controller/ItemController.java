package com.pruebaapipok.pruebaapipok.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pruebaapipok.pruebaapipok.model.Item; // Asegúrate de que esta clase sea tu @Entity
import com.pruebaapipok.pruebaapipok.service.ItemService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    
    @PostMapping("/preload")
    public ResponseEntity<String> preloadItems() {
     
        itemService.saveItem(new Item("Camisa", "Camisa de algodón"));
        itemService.saveItem(new Item("Pantalon", "Pantalón de mezclilla"));
        itemService.saveItem(new Item("Zapatos", "Zapatos deportivos"));
        itemService.saveItem(new Item("Israel", "Item de prueba Israel"));

        // Opcional: Si quieres agregar varios elementos de forma más concisa:
       
        List<Item> initialItems = Arrays.asList(
                new Item("Gorra", "Gorra de béisbol con logo"),
                new Item("Calcetines", "Calcetines de algodón suaves") 
            );
            initialItems.forEach(itemService::saveItem);

            return ResponseEntity.ok("Items precargados.");
        }

    /**
     * Endpoint para obtener ítems filtrados por nombre.
     * Accede vía POST a /api/items
     * @param request Un mapa que contiene el campo "name" para filtrar.
     * @return ResponseEntity con una lista de ítems que coinciden con el filtro.
     */
    @PostMapping
    public ResponseEntity<List<Item>> getItems(@RequestBody Map<String, String> request) {
        String name = request.getOrDefault("name", "");
        List<Item> items = itemService.getFilteredItems(name);
        return ResponseEntity.ok(items);
    }
}
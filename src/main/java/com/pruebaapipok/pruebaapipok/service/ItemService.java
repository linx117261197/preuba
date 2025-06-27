package com.pruebaapipok.pruebaapipok.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pruebaapipok.pruebaapipok.model.Item;
import com.pruebaapipok.pruebaapipok.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getFilteredItems(String name) {
        if (name == null || name.isEmpty()) {
            return itemRepository.findAll();
        }
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    public void saveItem(Item item) {
        itemRepository.save(item);
    }
}
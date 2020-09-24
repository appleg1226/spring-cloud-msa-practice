package com.chong.shop.controller;

import com.chong.shop.domain.Item;
import com.chong.shop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DefaultController {

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/shop/{id}")
    public ResponseEntity<Item> getItemInfo(@PathVariable("id") String id){
        Item result = itemRepository.findById(id).orElse(null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/shop/all")
    public ResponseEntity<Iterable<Item>> getAllItem(){
        Iterable<Item> result = itemRepository.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/shop/add")
    public ResponseEntity<String> addUser(@RequestBody Item item){
        itemRepository.save(item);
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }
}

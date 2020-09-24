package com.chong.shop.controller;

import com.chong.shop.domain.Item;
import com.chong.shop.repository.ItemRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@Log
public class DefaultController {

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/iteminfo/{id}")
    public ResponseEntity<Item> getItemInfo(@PathVariable("id") String id){
        log.info("item info called");
        Item result = itemRepository.findById(id).orElse(null);
        if(result == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/iteminfo/all")
    public ResponseEntity<Iterable<Item>> getAllItem(){
        Iterable<Item> result = itemRepository.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/iteminfo/add")
    public ResponseEntity<String> addUser(@RequestBody Item item){
        itemRepository.save(item);
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }
}

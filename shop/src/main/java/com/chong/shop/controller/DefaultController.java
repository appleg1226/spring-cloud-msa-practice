package com.chong.shop.controller;

import com.chong.shop.domain.Item;
import com.chong.shop.repository.ItemRepository;
import com.chong.shop.source.UserBuySource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Log
@EnableBinding(UserBuySource.class)
@RequiredArgsConstructor
public class DefaultController {

    private final ItemRepository itemRepository;
    private final UserBuySource userBuySource;

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
    public ResponseEntity<String> addItem(@RequestBody Item item){
        itemRepository.save(item);
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }

    @PostMapping("/buy/{id}/{user_id}")
    public ResponseEntity<String> buyItem(@PathVariable("id") String itemId, @PathVariable("user_id") String userId){
        Optional<Item> result = itemRepository.findById(itemId);
        if(!result.isPresent()){
            return new ResponseEntity<>("no such item", HttpStatus.BAD_REQUEST);
        }

        Item itemResult = result.get();
        itemResult.setStock(itemResult.getStock() - 1);
        log.info("user " + userId + " buy item " + itemId + "/ now count: " + itemResult.getStock());
        itemRepository.save(itemResult);

        // 구매한 물건 정보와 유저의 아이디를 유저 어플리케이션으로 message 전송
        userBuySource.buyItem().send(MessageBuilder.withPayload(new ItemAndUser(itemResult, userId)).build());

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    static class ItemAndUser{
        private Item item;
        private String userId;
    }
}

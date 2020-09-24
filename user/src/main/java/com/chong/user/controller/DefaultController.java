package com.chong.user.controller;

import com.chong.user.domain.User;
import com.chong.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class DefaultController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable("id") String id){
        User result = userRepository.findById(id).orElse(null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/user/all")
    public ResponseEntity<Iterable<User>> getAlluser(){
        Iterable<User> result = userRepository.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/user/add")
    public ResponseEntity<String> addUser(@RequestBody User user){
        userRepository.save(user);
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }
}

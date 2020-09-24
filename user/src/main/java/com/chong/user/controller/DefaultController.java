package com.chong.user.controller;

import com.chong.user.domain.User;
import com.chong.user.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Log
public class DefaultController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/userinfo/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable("id") String id){
        log.info("user info called");
        User result = userRepository.findById(id).orElse(null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/userinfo/all")
    public ResponseEntity<Iterable<User>> getAlluser(){
        Iterable<User> result = userRepository.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/userinfo/add")
    public ResponseEntity<String> addUser(@RequestBody User user){
        userRepository.save(user);
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }
}

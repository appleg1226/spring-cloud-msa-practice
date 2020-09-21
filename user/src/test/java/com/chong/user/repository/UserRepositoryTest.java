package com.chong.user.repository;

import com.chong.user.domain.User;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testRepo(){
        userRepository.save(new User("1", "chong", "suwon"));

        userRepository.findById("1").ifPresent(user -> log.info(user.toString()));
    }

}
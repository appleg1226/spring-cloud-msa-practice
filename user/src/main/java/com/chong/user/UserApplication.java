package com.chong.user;

import com.chong.user.domain.User;
import com.chong.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class UserApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Autowired
	UserRepository userRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user1 = new User("1", "chong", "suwon", 100000L);
		User user2 = new User("2", "kim", "seoul", 200000L);
		User user3 = new User("3", "do", "suwon", 300000L);
		userRepository.saveAll(Arrays.asList(user1, user2, user3));
		System.out.println("initialized user information");
	}
}

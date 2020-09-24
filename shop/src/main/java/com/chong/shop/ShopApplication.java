package com.chong.shop;

import com.chong.shop.domain.Item;
import com.chong.shop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class ShopApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}

	@Autowired
	ItemRepository itemRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Item item1 = new Item("1", "coke", Item.Category.FOOD, 10L);
		Item item2 = new Item("2", "cidar", Item.Category.FOOD, 10L);
		Item item3 = new Item("3", "pants", Item.Category.CLOTHES, 5L);
		Item item4 = new Item("4", "cap", Item.Category.CLOTHES, 5L);
		Item item5 = new Item("5", "iphone", Item.Category.DEVICE, 10L);
		itemRepository.saveAll(Arrays.asList(item1, item2, item3, item4, item5));
		System.out.println("initialized item information");
	}
}

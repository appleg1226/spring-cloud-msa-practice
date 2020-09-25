package com.chong.user.cloudStream;

import com.chong.user.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@Log
public class MessageListener {

    @StreamListener(target = Sink.INPUT)
    public void getUserBuyMessage(ItemAndUser itemAndUser){
        log.info("=============message received!!=============");
        log.info(itemAndUser.toString());
    }

    @Data
    @AllArgsConstructor
    static class ItemAndUser{
        private Item item;
        private String userId;
    }
}

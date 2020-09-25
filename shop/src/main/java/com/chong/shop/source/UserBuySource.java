package com.chong.shop.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface UserBuySource {

    @Output("buyItemChannel")
    MessageChannel buyItem();

}

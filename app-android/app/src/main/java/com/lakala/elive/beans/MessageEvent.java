package com.lakala.elive.beans;

import java.util.Objects;

/**
 * 消息发送类
 */
public class MessageEvent {

    public String type;
    public Objects content;
    public String result;

    public MessageEvent(String type,Objects content) {
        this.type = type;
        this.content = content;
    }

}

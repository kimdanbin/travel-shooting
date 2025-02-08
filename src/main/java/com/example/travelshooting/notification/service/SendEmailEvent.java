package com.example.travelshooting.notification.service;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendEmailEvent<T> extends ApplicationEvent {

    private final T data;

    public SendEmailEvent(Object source, T data) {
        super(source);
        this.data = data;
    }
}
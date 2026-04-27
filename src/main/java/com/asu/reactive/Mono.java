package com.asu.reactive;

import java.util.function.Consumer;

public class Mono<T> {

    private T data;

    private Mono(T data) {
        this.data = data;
    }

    public static <T> Mono<T> just(T data) {
        return new Mono<>(data);
    }

    public void subscribe(Consumer<T> consumer) {
        new Thread(() -> consumer.accept(data)).start();
    }
}

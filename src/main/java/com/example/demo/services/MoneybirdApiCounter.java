package com.example.demo.services;

import java.time.Duration;

public class MoneybirdApiCounter {
    public static int counter = 1;

    public static void inc() {
        if (counter % 150 == 0) {
            try {
                Thread.sleep(Duration.ofMinutes(5).toMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        counter++;
    }
}

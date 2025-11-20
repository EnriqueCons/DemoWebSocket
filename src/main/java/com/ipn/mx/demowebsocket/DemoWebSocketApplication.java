package com.ipn.mx.demowebsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoWebSocketApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoWebSocketApplication.class, args);
  }

}

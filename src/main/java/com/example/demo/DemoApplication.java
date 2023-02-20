package com.example.demo;

import com.example.demo.controllers.MoneybirdController;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.MoneybirdService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

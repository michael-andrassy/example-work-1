package demo.xmlassembly.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class XmlassemblyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(XmlassemblyServiceApplication.class, args);
	}

}

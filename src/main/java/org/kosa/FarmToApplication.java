package org.kosa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class FarmToApplication implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("hi");
    }
	public static void main(String[] args) {
		SpringApplication.run(FarmToApplication.class, args);
	}


}

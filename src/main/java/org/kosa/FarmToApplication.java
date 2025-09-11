package org.kosa;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Product;
import org.kosa.enums.ProductCategory;
import org.kosa.repository.ProductImageRepository;
import org.kosa.repository.ProductRepository;
import org.kosa.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@Slf4j
@SpringBootApplication
public class FarmToApplication implements CommandLineRunner{

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;         //
  
    @Override
    public void run(String... args) throws Exception {
        log.info("hi");
    }
  
    public static void main(String[] args) {
        SpringApplication.run(FarmToApplication.class, args);
    }

}

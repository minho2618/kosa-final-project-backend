package org.kosa.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kosa.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductImageService {

    private final ProductRepository productRepository;




}



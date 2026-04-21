package com.NetworkClash.demo.controller;

import com.NetworkClash.demo.entity.CardEntity;
import com.NetworkClash.demo.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    // Endpoint untuk mengambil SEMUA data kartu
    // Method: GET
    // URL: http://localhost:8080/api/cards
    @GetMapping
    public List<CardEntity> getAllCards() {
        return cardRepository.findAll();
    }
}
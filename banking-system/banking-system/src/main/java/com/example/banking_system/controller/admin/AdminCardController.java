package com.example.banking_system.controller.admin;

import com.example.banking_system.dto.CardResponse;
import com.example.banking_system.model.Card;
import com.example.banking_system.service.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8000")
@RequestMapping("/admin/cards")
public class AdminCardController {
    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponse> getAllCards() {
        List<Card> cards = cardService.getAllCards();
        List<CardResponse> cardResponses = new ArrayList<>();
        for (Card card : cards) {
            CardResponse cr = new CardResponse();
            cr.setCardId(card.getCardId());
            cr.setAccountNo(card.getAccount().getAccountNo());
            cr.setCardNumber(card.getCardNumber());
            cr.setCardType(card.getCardType() != null ? card.getCardType().name() : null);
            cr.setExpiryDate(card.getExpiryDate() != null ? card.getExpiryDate().toString() : null);
            cr.setStatus(card.getStatus() != null ? card.getStatus().name() : null);
            cardResponses.add(cr);
        }
        return cardResponses;
    }

    @PostMapping
    public Card createCard(@RequestBody Card card) {
        return cardService.createCard(card);
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
    }
}
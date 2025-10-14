package com.example.banking_system.controller.user;

import com.example.banking_system.dto.CardResponse;
import com.example.banking_system.enums.CardStatus;
import com.example.banking_system.model.Card;
import com.example.banking_system.service.CardService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/user/cards")
public class UserCardController {
    private final CardService cardService;
    public UserCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponse> getMyCards() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Card> cards = cardService.getCardsByUsername(username);
        List<CardResponse> cardDtos = new ArrayList<>();
        for (Card c : cards) {
            CardResponse cr = new CardResponse();
            cr.setCardId(c.getCardId());
            cr.setCardNumber(c.getCardNumber());
            cr.setCardType(c.getCardType().name());
            cr.setExpiryDate(c.getExpiryDate().toString());
            cr.setStatus(c.getStatus().name());
            cardDtos.add(cr);
        }
        return cardDtos;
    }

    @GetMapping("/account/{accountId}")
    public List<CardResponse> getCardsByAccountId(@PathVariable Long accountId) {
        List<Card> cards = cardService.getCardsByAccountId(accountId.intValue());
        List<CardResponse> cardDtos = new ArrayList<>();
        for (Card c : cards) {
            CardResponse cr = new CardResponse();
            cr.setCardId(c.getCardId());
            cr.setAccountNo(accountId);
            cr.setCardNumber(c.getCardNumber());
            cr.setCardType(c.getCardType().name());
            cr.setExpiryDate(c.getExpiryDate().toString());
            cr.setStatus(c.getStatus().name());
            cardDtos.add(cr);
        }
        return cardDtos;
    }

    @PatchMapping("/{id}/status")
    public CardResponse updateCardStatus(@PathVariable Long id, @RequestParam String status) {
        Card card = cardService.updateCardStatus(id, CardStatus.valueOf(status.toLowerCase()));
        CardResponse cr = new CardResponse();
        cr.setCardId(card.getCardId());
        cr.setCardNumber(card.getCardNumber());
        cr.setCardType(card.getCardType().name());
        cr.setExpiryDate(card.getExpiryDate().toString());
        cr.setStatus(card.getStatus().toString());
        return cr;
    }
}

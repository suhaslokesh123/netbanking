package com.example.banking_system.service;
import com.example.banking_system.enums.CardStatus;
import com.example.banking_system.model.Card;
import com.example.banking_system.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    // ADMIN: List all cards
    public List<Card> getAllCards() { return cardRepository.findAll(); }

    // USER: List cards by username (implement repo method)
    public List<Card> getCardsByUsername(String username) {
        return cardRepository.findByAccountUserUsername(username);
    }

    // USER: List cards by account ID
    public List<Card> getCardsByAccountId(Integer accountId) {
        return cardRepository.findByAccountAccountNo(accountId.longValue());
    }

    public Optional<Card> getCardById(Long id) { return cardRepository.findById(id); }
    public Card createCard(Card card) { return cardRepository.save(card); }
    public void deleteCard(Long id) { cardRepository.deleteById(id); }
    public Card updateCardStatus(Long id, CardStatus newStatus) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setStatus(newStatus);
        return cardRepository.save(card);
    }
}

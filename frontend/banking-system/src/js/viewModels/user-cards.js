define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojtable',
  'ojs/ojformlayout',
  'ojs/ojinputtext',
  'ojs/ojselectsingle',
  'ojs/ojbutton'
], function (ko, ArrayDataProvider) {


  function UserCardsViewModel() {
    const self = this;

    // Observable array and data provider
    self.cards = ko.observableArray([]);
    self.cardsDataProvider = new ArrayDataProvider(self.cards, { keyAttributes: 'cardId' });

    // Status message
    self.statusMessage = ko.observable('');

    // Edit status observables
    self.selectedCard = ko.observable({});
    self.newStatus = ko.observable('');
    self.cardToEdit = ko.observable(null);

    // Status options
    // Status options
    self.statusOptions = ko.observableArray([
      { value: 'active', label: 'Active' },
      { value: 'blocked', label: 'Block' },
    ]);

    // Data provider with key mapping
    self.statusOptionsProvider = new ArrayDataProvider(self.statusOptions, {
      keyAttributes: 'value'
    });


    // Load user cards
    self.loadUserCards = function () {
      const userId = sessionStorage.getItem('userId');
      if (!userId) {
        self.statusMessage('User not logged in');
        return;
      }

      self.statusMessage('Loading your accounts...');

      // First get user's accounts, then get cards for each account
      fetch(`http://localhost:8080/user/accounts/${userId}`)
        .then(res => {
          if (!res.ok) {
            throw new Error('Failed to load accounts');
          }
          return res.json();
        })
        .then(accounts => {
          if (accounts.length === 0) {
            self.cards([]);
            self.statusMessage('No accounts found');
            return;
          }

          // Get cards for each account
          const cardPromises = accounts.map(account =>
            fetch(`http://localhost:8080/user/cards/account/${account.accountNo}`)
              .then(res => res.json())
              .catch(err => {
                console.error(`Error loading cards for account ${account.accountNo}:`, err);
                return []; // Return empty array on error
              })
          );

          return Promise.all(cardPromises).then(cardArrays => {
            // Flatten the array of card arrays into a single array
            const allCards = cardArrays.flat();
            self.cards(allCards);
            self.statusMessage(`Loaded ${allCards.length} card(s) from ${accounts.length} account(s)`);
          });
        })
        .catch(err => {
          console.error('Error loading accounts:', err);
          self.statusMessage('Error loading cards: ' + err.message);
        });
    };

    // Back to dashboard
    self.backToDashboard = function () {
      window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
    };

    // Edit card status
    self.editCardStatus = function (cardId, currentStatus) {
      // Find the card data from the cards array
      const cards = self.cards();
      const card = cards.find(c => c.cardId === cardId);

      if (card) {
        self.selectedCard(card);
        self.cardToEdit(cardId);
        self.newStatus(currentStatus);
        document.getElementById('editStatusDialog').open();
      }
    };

    // Update card status
    self.updateCardStatus = function () {
      const cardId = self.cardToEdit();
      const newStatus = self.newStatus();

      if (!cardId || !newStatus) {
        self.statusMessage('Please select a status');
        return;
      }

      self.statusMessage('Updating card status...');

      fetch(`http://localhost:8080/user/cards/${cardId}/status?status=${newStatus}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json'
        }
      })
        .then(res => {
          if (!res.ok) {
            throw new Error('Failed to update card status');
          }
          return res.json();
        })
        .then(updatedCard => {
          // Update the card in the observable array
          const cards = self.cards();
          const index = cards.findIndex(card => card.cardId === cardId);
          if (index !== -1) {
            cards[index] = updatedCard;
            self.cards(cards);
          }
          self.statusMessage('Card status updated successfully');
          document.getElementById('editStatusDialog').close();
        })
        .catch(err => {
          console.error('Error updating card status:', err);
          self.statusMessage('Error updating card status: ' + err.message);
        });
    };

    // Cancel edit dialog
    self.cancelEditDialog = function () {
      document.getElementById('editStatusDialog').close();
    };

    // Initial load
    self.connected = function () {
      self.loadUserCards();
    };
  }

  return UserCardsViewModel;
});

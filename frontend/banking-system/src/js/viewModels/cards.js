define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojtable',
  'ojs/ojbutton',
  'ojs/ojdialog',
  'ojs/ojinputtext',
  'ojs/ojselectsingle',
  'ojs/ojdatetimepicker'
], function (ko, ArrayDataProvider) {

  function CardsViewModel() {
    const self = this;

    // --- Observables ---
    self.cards = ko.observableArray([]);
    self.accounts = ko.observableArray([]);
    self.statusMessage = ko.observable('');

    // --- DataProviders ---
    self.dataProvider = ko.computed(() => {
      return new ArrayDataProvider(self.cards(), { keyAttributes: 'cardId' });
    });

    self.accountsDataProvider = ko.computed(() => {
      return new ArrayDataProvider(self.accounts(), { keyAttributes: 'accountNo' });
    });

    // Dropdown options
    self.cardTypes = [
      { value: 'credit', label: 'Credit' },
      { value: 'debit', label: 'Debit' }
    ];

    self.statuses = [
      { value: 'active', label: 'Active' },
      { value: 'blocked', label: 'Blocked' }
    ];

    self.cardTypesProvider = new ArrayDataProvider(self.cardTypes, { keyAttributes: 'value' });
    self.statusesProvider = new ArrayDataProvider(self.statuses, { keyAttributes: 'value' });

    // --- Create Card Dialog fields ---
    self.newAccountNo = ko.observable('');
    self.newCardType = ko.observable('');
    self.newExpiryDate = ko.observable('');
    self.newStatus = ko.observable('');
    self.newCvv = ko.observable('');

    // --- Fetch cards ---
    self.fetchCards = async function () {
      self.statusMessage('Fetching cards...');
      try {
        const res = await fetch('http://localhost:8080/admin/cards');
        if (!res.ok) throw new Error('Failed to fetch cards');
        const data = await res.json();
        self.cards(data);
        self.statusMessage(`Loaded ${data.length} cards.`);
      } catch (err) {
        console.error(err);
        self.statusMessage('Error fetching cards.');
      }
    };

    // --- Fetch accounts ---
    self.fetchAccounts = async function () {
      try {
        const res = await fetch('http://localhost:8080/admin/accounts');
        if (!res.ok) throw new Error('Failed to fetch accounts');
        const data = await res.json();
        self.accounts(data);
      } catch (err) {
        console.error(err);
      }
    };

    // --- Open create dialog ---
    self.openCreateDialog = function () {
      const dialog = document.getElementById('createCardDialog');
      if (dialog) dialog.open();
    };

    // --- Add new card ---
    self.addCard = async function () {
      if (!self.newAccountNo() || !self.newCardType() || !self.newExpiryDate() || !self.newStatus() || !self.newCvv()) {
        alert('Please fill all fields.');
        return;
      }

      const payload = {
        account: { accountNo: Number(self.newAccountNo()) },
        cardNumber: Math.floor(1000000000000000 + Math.random() * 9000000000000000).toString(),
        cardType: self.newCardType(),
        expiryDate: self.newExpiryDate(),
        cvv: self.newCvv(),
        status: self.newStatus()
      };

      try {
        const res = await fetch('http://localhost:8080/admin/cards', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error(await res.text());

        alert('Card created successfully!');
        self.fetchCards();

        // Reset fields
        self.newAccountNo('');
        self.newCardType('');
        self.newExpiryDate('');
        self.newStatus('');
        self.newCvv('');

        const dialog = document.getElementById('createCardDialog');
        if (dialog) dialog.close();
      } catch (err) {
        alert('Error creating card: ' + err.message);
      }
    };

    // --- Delete card ---
    self.deleteCard = async function (card) {
      if (!card) return;
      if (!confirm(`Are you sure you want to delete card ${card.cardNumber}?`)) return;

      try {
        const res = await fetch(`http://localhost:8080/admin/cards/${card.cardId}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(await res.text());

        alert('Card deleted successfully!');
        self.fetchCards();
      } catch (err) {
        alert('Error deleting card: ' + err.message);
      }
    };


    // --- Cancel create dialog ---
    self.cancelDialog = function () {
      const dialog = document.getElementById('createCardDialog');
      if (dialog) dialog.close();
    };

    // --- Auto-fetch cards and accounts ---
    self.fetchCards();
    self.fetchAccounts();
  }

  return CardsViewModel;
});

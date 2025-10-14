define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojtable',
  'ojs/ojformlayout',
  'ojs/ojinputtext',
  'ojs/ojinputnumber',
  'ojs/ojselectsingle',
  'ojs/ojbutton'
], function (ko, ArrayDataProvider) {

  function FundTransferViewModel() {
    const self = this;

    // Form observables (numeric fields initialized as null)
    self.fromAccountNo = ko.observable(null);
    self.toAccountNo = ko.observable(null);
    self.transferAmount = ko.observable(null);

    // Accounts data for dropdown
    self.fromAccounts = ko.observableArray([]);
    self.fromAccountsDataProvider = new ArrayDataProvider(self.fromAccounts, { keyAttributes: 'accountNo' });

    // Status message
    self.statusMessage = ko.observable('');

    // Load user accounts for "From Account" dropdown
    self.loadUserAccounts = function() {
      const userId = sessionStorage.getItem('userId');
      if (!userId) {
        self.statusMessage('User not logged in');
        return;
      }

      self.statusMessage('Loading your accounts...');

      fetch(`http://localhost:8080/user/accounts/${userId}`)
        .then(res => {
          if (!res.ok) {
            throw new Error('Failed to load accounts');
          }
          return res.json();
        })
        .then(accounts => {
          // Transform accounts for dropdown
          const accountOptions = accounts.map(account => ({
            accountNo: Number(account.accountNo),
            displayText: `Account ${account.accountNo} - Balance: ₹${Number(account.balance).toFixed(2)}`
          }));
          self.fromAccounts(accountOptions);
          self.statusMessage(`Loaded ${accounts.length} account(s)`);
        })
        .catch(err => {
          console.error('Error loading accounts:', err);
          self.statusMessage('Error loading accounts: ' + err.message);
        });
    };

    // Perform fund transfer
    self.performFundTransfer = function() {
      const fromAccount = self.fromAccountNo();
      const toAccount = self.toAccountNo();
      const amount = self.transferAmount();

      if (fromAccount == null || toAccount == null || amount == null) {
        self.statusMessage('Please fill in all fields');
        return;
      }

      if (fromAccount === toAccount) {
        self.statusMessage('Cannot transfer to the same account');
        return;
      }

      if (amount <= 0) {
        self.statusMessage('Amount must be greater than 0');
        return;
      }

      self.statusMessage('Processing fund transfer...');

      const transferData = {
        fromAccountNo: Number(fromAccount),
        toAccountNo: Number(toAccount),
        amount: parseFloat(amount)
      };

      fetch('http://localhost:8080/user/transactions/fund-transfer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transferData)
      })
      .then(res => {
        if (!res.ok) {
          return res.json().then(err => { throw new Error(err.message || 'Transfer failed'); });
        }
        return res.json();
      })
      .then(response => {
            // Show alert
            alert(`Fund transfer successful!\nTransfer ID: ${response.transferId}\nTransaction ID: ${response.txnId}`);

            // Reset the form
            self.resetForm();

            // Optional: update status message as well
            self.statusMessage(`Fund transfer completed successfully!`);
      })

      .catch(err => {
        console.error('Error performing fund transfer:', err);
        self.statusMessage('Error: ' + err.message);
      });
    };

    // Reset form
    self.resetForm = function() {
      self.fromAccountNo(null);
      self.toAccountNo(null);
      self.transferAmount(null);
      self.statusMessage('');
    };

    // Back to dashboard
    self.backToDashboard = function() {
      window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
    };

    // Initial load
    self.connected = function() {
      self.loadUserAccounts();
    };
  }

  return FundTransferViewModel;
});

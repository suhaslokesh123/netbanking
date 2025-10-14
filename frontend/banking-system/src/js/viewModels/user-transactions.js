define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojtable'
], function (ko, ArrayDataProvider) {

  function UserTransactionsViewModel() {
    const self = this;

    // Observable array and data provider
    self.transactions = ko.observableArray([]);
    self.transactionsDataProvider = new ArrayDataProvider(self.transactions, { keyAttributes: 'txnId' });

    // Status message
    self.statusMessage = ko.observable('');

    // Load user transactions
    self.loadUserTransactions = function() {
      const userId = sessionStorage.getItem('userId');
      if (!userId) {
        self.statusMessage('User not logged in');
        return;
      }

      self.statusMessage('Loading your accounts...');

      // First get user's accounts, then get transactions for each account
      fetch(`http://localhost:8080/user/accounts/${userId}`)
        .then(res => {
          if (!res.ok) {
            throw new Error('Failed to load accounts');
          }
          return res.json();
        })
        .then(accounts => {
          if (accounts.length === 0) {
            self.transactions([]);
            self.statusMessage('No accounts found');
            return;
          }

          // Get transactions for each account
          const transactionPromises = accounts.map(account =>
            fetch(`http://localhost:8080/user/transactions/account/${account.accountNo}`)
              .then(res => res.json())
              .catch(err => {
                console.error(`Error loading transactions for account ${account.accountNo}:`, err);
                return []; // Return empty array on error
              })
          );

          return Promise.all(transactionPromises).then(transactionArrays => {
            // Flatten the array of transaction arrays into a single array
            const allTransactions = transactionArrays.flat();
            self.transactions(allTransactions);
            self.statusMessage(`Loaded ${allTransactions.length} transaction(s) from ${accounts.length} account(s)`);
          });
        })
        .catch(err => {
          console.error('Error loading accounts:', err);
          self.statusMessage('Error loading transactions: ' + err.message);
        });
    };

    // Back to dashboard
    self.backToDashboard = function() {
      window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
    };

    // Initial load
    self.connected = function() {
      self.loadUserTransactions();
    };
  }

  return UserTransactionsViewModel;
});

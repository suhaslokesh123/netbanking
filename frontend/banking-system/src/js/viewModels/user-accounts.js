define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojtable'
], function (ko, ArrayDataProvider) {

  function UserAccountsViewModel() {
    const self = this;

    // Observable array and data provider
    self.accounts = ko.observableArray([]);
    self.accountsDataProvider = new ArrayDataProvider(self.accounts, { keyAttributes: 'accountNo' });

    // Status message
    self.statusMessage = ko.observable('');

    // Load user accounts
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
          self.accounts(accounts);
          self.statusMessage(`Loaded ${accounts.length} account(s)`);
        })
        .catch(err => {
          console.error('Error loading accounts:', err);
          self.statusMessage('Error loading accounts: ' + err.message);
        });
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

  return UserAccountsViewModel;
});

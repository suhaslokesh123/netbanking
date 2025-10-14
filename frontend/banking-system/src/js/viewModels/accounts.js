define([
  'knockout',
  'jquery',
  'ojs/ojarraydataprovider',
  'ojs/ojinputnumber',
  'ojs/ojselectsingle',
  'ojs/ojbutton',
  'ojs/ojtable',
  'ojs/ojformlayout'
], function (ko, $, ArrayDataProvider) {

  function AccountsViewModel() {
    const self = this;

    // --- Observables ---
    self.users = ko.observableArray([]);
    self.accounts = ko.observableArray([]);
    self.editUsername = ko.observable('');

    // Create Account Form
    self.selectedUserId = ko.observable('');
    self.selectedAccountType = ko.observable('');
    self.balance = ko.observable(0);
    self.selectedStatus = ko.observable('active');

    // Edit Account Form
    self.isEditing = ko.observable(false);
    self.editAccountNo = ko.observable(null);
    self.editUserId = ko.observable('');
    self.editAccountType = ko.observable('');
    self.editBalance = ko.observable(0);
    self.editStatus = ko.observable('active');

    // --- DataProviders ---
    self.userDataProvider = ko.observable(new ArrayDataProvider([], { keyAttributes: 'userId' }));
    self.accountTypeOptions = [
      { value: 'savings', label: 'Savings' },
      { value: 'current', label: 'Current' }
    ];
    self.statusOptions = [
      { value: 'active', label: 'Active' },
      { value: 'inactive', label: 'Inactive' },
      { value: 'suspended', label: 'Suspended' }
    ];

    self.accountTypeProvider = new ArrayDataProvider(self.accountTypeOptions, { keyAttributes: 'value' });
    self.statusProvider = new ArrayDataProvider(self.statusOptions, { keyAttributes: 'value' });

    // Observable DataProvider for accounts table
    self.accountsDataProvider = ko.observable(new ArrayDataProvider(self.accounts, { keyAttributes: 'accountNo' }));

    // --- Load Users ---
    self.loadUsers = function () {
      fetch('http://localhost:8080/admin/users')
        .then(res => res.json())
        .then(data => {
          self.users(data);
          self.userDataProvider(new ArrayDataProvider(data, { keyAttributes: 'userId' }));
        })
        .catch(err => console.error('Error fetching users:', err));
    };

    // --- Load Accounts ---
    self.loadAccounts = function () {
      fetch('http://localhost:8080/admin/accounts')
        .then(res => res.json())
        .then(data => {
          self.accounts(data);
          self.accountsDataProvider(new ArrayDataProvider(self.accounts, { keyAttributes: 'accountNo' }));
        })
        .catch(err => console.error('Error fetching accounts:', err));
    };

    // --- Create Account ---
    self.createAccount = function () {
      if (!self.selectedUserId() || !self.selectedAccountType() || !self.selectedStatus()) {
        alert('Please fill all required fields.');
        return;
      }

      const payload = {
        userId: Number(self.selectedUserId()),
        accountType: self.selectedAccountType(),
        balance: Number(self.balance()),
        status: self.selectedStatus()
      };

      fetch(`http://localhost:8080/admin/accounts?userId=${payload.userId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
        .then(async res => {
          if (!res.ok) throw new Error(await res.text());
          alert('Account created successfully!');
          self.selectedUserId('');
          self.selectedAccountType('');
          self.balance(0);
          self.selectedStatus('active');
          self.loadAccounts();
        })
        .catch(err => alert('Error creating account: ' + err.message));
    };

    // --- Edit Account ---
    self.startEdit = function(account) {
      if (!account) return;

      self.isEditing(true);
      self.editAccountNo(Number(account.accountNo));
      self.editUserId(String(account.userId)); // still needed for backend update
      self.editUsername(account.username);     // display in text box
      self.editAccountType(account.accountType);
      self.editBalance(Number(account.balance));
      self.editStatus(account.status);

      setTimeout(() => {
        const editForm = document.getElementById('editAccountForm');
        if (editForm) editForm.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    };


    self.cancelEdit = function () {
      self.isEditing(false);
      self.editAccountNo(null);
      self.editUserId('');
      self.editAccountType('');
      self.editBalance(0);
      self.editStatus('active');
    };

    self.updateAccount = function () {
      if (!self.editAccountNo()) return;

      const payload = {
        userId: Number(self.editUserId()),
        accountType: self.editAccountType(),
        balance: Number(self.editBalance()),
        status: self.editStatus()
      };

      fetch(`http://localhost:8080/admin/accounts/${self.editAccountNo()}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })
        .then(async res => {
          if (!res.ok) throw new Error(await res.text());
          alert('Account updated successfully!');
          self.cancelEdit();
          self.loadAccounts();
        })
        .catch(err => alert('Error updating account: ' + err.message));
    };

    // --- Delete Account ---
    self.deleteAccount = function (account) {
      if (!account) return;

      const accountNo = Number(account.accountNo);
      if (isNaN(accountNo)) {
        alert('Invalid account number');
        return;
      }

      if (!confirm(`Are you sure you want to delete account ${accountNo}?`)) return;

      fetch(`http://localhost:8080/admin/accounts/${accountNo}`, { method: 'DELETE' })
        .then(async res => {
          if (!res.ok) throw new Error(await res.text());
          alert('Account deleted successfully!');
          self.loadAccounts();
        })
        .catch(err => alert('Error deleting account: ' + err.message));
    };

    // --- Connected ---
    self.connected = function () {
      self.loadUsers();
      self.loadAccounts();
    };
  }

  return AccountsViewModel;
});

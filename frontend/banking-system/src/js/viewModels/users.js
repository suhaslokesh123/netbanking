define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojtable',
  'ojs/ojbutton',
  'ojs/ojinputtext',
  'ojs/ojselectsingle'
], function (ko, ArrayDataProvider) {

  function UsersViewModel() {
    const self = this;

    // Observable array and data provider
    self.users = ko.observableArray([]);
    self.dataProvider = new ArrayDataProvider(self.users, { keyAttributes: 'userId' });

    // Status message
    self.status = ko.observable('');

    // New user form
    self.newUsername = ko.observable('');
    self.newEmail = ko.observable('');
    self.newPhone = ko.observable('');
    self.newPassword = ko.observable('');
    self.newStatus = ko.observable('active');

    // Edit form
    self.isEditing = ko.observable(false);
    self.editUserId = ko.observable(null);
    self.editUsername = ko.observable('');
    self.editEmail = ko.observable('');
    self.editPhone = ko.observable('');
    self.editPassword = ko.observable('');
    self.editStatus = ko.observable('active');

    // Hash password function
    self.hashPassword = async function(password) {
      if (!password) return '';
      const encoder = new TextEncoder();
      const data = encoder.encode(password);
      const hashBuffer = await crypto.subtle.digest('SHA-256', data);
      return Array.from(new Uint8Array(hashBuffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
    };

    // Load all users
    self.loadUsers = function() {
      self.status('Loading users...');
      fetch('http://localhost:8080/admin/users')
        .then(res => res.ok ? res.json() : Promise.reject('Failed to load users'))
        .then(data => {
          self.users(data);
          self.status('Users loaded successfully!');
        })
        .catch(err => {
          console.error(err);
          self.status('Error loading users: ' + err);
        });
    };

    // Refresh users
    self.refreshUsers = function() {
      self.loadUsers();
    };

    // Create new user
    self.createUser = async function() {
      try {
        const hashedPassword = await self.hashPassword(self.newPassword());
        const payload = {
          username: self.newUsername(),
          email: self.newEmail(),
          phone: self.newPhone(),
          password: hashedPassword,
          status: self.newStatus()
        };

        const res = await fetch('http://localhost:8080/admin/users', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          const errorText = await res.text();
          throw new Error(errorText);
        }

        const user = await res.json();
        alert('User created: ' + user.username);

        // Reset form
        self.newUsername('');
        self.newEmail('');
        self.newPhone('');
        self.newPassword('');
        self.newStatus('active');

        self.loadUsers();
      } catch (err) {
        console.error('Create error:', err);
        alert('Error creating user: ' + err.message);
      }
    };

    // Start editing a user
    self.startEdit = function(user) {
      if (!user) return;
      self.isEditing(true);
      self.editUserId(user.userId);
      self.editUsername(user.username);
      self.editEmail(user.email);
      self.editPhone(user.phone);
      self.editPassword('');
      self.editStatus(user.status);

      setTimeout(() => {
        const editForm = document.getElementById('editForm');
        if (editForm) editForm.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    };

    // Cancel edit
    self.cancelEdit = function() {
      self.isEditing(false);
      self.editUserId(null);
      self.editUsername('');
      self.editEmail('');
      self.editPhone('');
      self.editPassword('');
      self.editStatus('active');
    };

    // Update user
    self.updateUser = async function() {
      try {
        const payload = {
          username: self.editUsername(),
          email: self.editEmail(),
          phone: self.editPhone(),
          status: self.editStatus()
        };

        const newPassword = self.editPassword();
        if (newPassword && newPassword.trim() !== '') {
          payload.password = await self.hashPassword(newPassword);
        }

        const res = await fetch(`http://localhost:8080/admin/users/${self.editUserId()}`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          const errorText = await res.text();
          throw new Error(errorText);
        }

        alert('User updated successfully!');
        self.cancelEdit();
        self.loadUsers();
      } catch (err) {
        console.error('Update error:', err);
        alert('Error updating user: ' + err.message);
      }
    };

    // Delete user
    self.deleteUser = function(user) {
      if (!user || !user.username) return alert('Invalid user');

      if (!confirm(`Are you sure you want to delete "${user.username}"?`)) return;

      fetch(`http://localhost:8080/admin/users/${user.userId}`, { method: 'DELETE' })
        .then(async res => {
          if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText);
          }
          alert('User deleted successfully!');
          self.loadUsers();
        })
        .catch(err => {
          console.error('Delete error:', err);
          alert('Error deleting user: ' + err.message);
        });
    };

    // Initial load
    self.connected = function() {
      self.loadUsers();
    };
  }

  return UsersViewModel;
});

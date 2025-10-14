define(['knockout'], function(ko) {
  function LoginViewModel() {
    const self = this;

    // --- Observables ---
    self.role = ko.observable('admin');
    self.username = ko.observable('');
    self.password = ko.observable('');
    self.errorMessage = ko.observable('');

    // --- Role Types ---
    self.roleTypes = ko.observableArray([
      { value: 'admin', label: 'Admin' },
      { value: 'user', label: 'User' }
    ]);

    // --- Hash password function ---
    self.hashPassword = async function(password) {
      if (!password) return '';
      const encoder = new TextEncoder();
      const data = encoder.encode(password);
      const hashBuffer = await crypto.subtle.digest('SHA-256', data);
      return Array.from(new Uint8Array(hashBuffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
    };

    // --- Login function ---
    self.login = async function() {
      if (!self.username() || !self.password()) {
        self.errorMessage('Please enter username and password.');
        return;
      }

      // Hash password for users (not for admins)
      let passwordToSend = self.password();
      if (self.role() === 'user') {
        passwordToSend = await self.hashPassword(self.password());
      }

      const payload = {
        role: self.role(),
        username: self.username(),
        password: passwordToSend
      };

      try {
        const res = await fetch('http://localhost:8080/api/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        const data = await res.json();

        if (data.success) {
          // Store role and userId if user
          sessionStorage.setItem('role', data.role);
          if (data.role === 'user') {
            sessionStorage.setItem('userId', data.userId);
          }

          // Update global role
          if (window.currentRole) {
            window.currentRole(data.role);
          }

          // Redirect
          if (data.role === 'admin') {
            window.location.href = 'http://localhost:8000/?ojr=dashboard';
          } else {
            window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
          }
        } else {
          self.errorMessage(data.message || 'Login failed.');
        }
      } catch (err) {
        console.error(err);
        self.errorMessage('Network error. Please try again.');
      }
    };

    // --- Connected ---
    self.connected = function() {
      // If already logged in, redirect to dashboard
      const role = sessionStorage.getItem('role');
      if (role) {
        if (window.currentRole) {
          window.currentRole(role);
        }
        if (role === 'admin') {
          window.location.href = 'http://localhost:8000/?ojr=dashboard';
        } else {
          window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
        }
      } else {
        // Clear any previous session
        sessionStorage.clear();
      }
    };
  }

  return LoginViewModel;
});

define(['knockout'], function (ko) {

  function UserProfileViewModel() {
    const self = this;

    // Profile data observable
    self.profileData = ko.observable({});

    // Status message
    self.statusMessage = ko.observable('');

    // Load user profile
    self.loadUserProfile = function() {
      const userId = sessionStorage.getItem('userId');
      if (!userId) {
        self.statusMessage('User not logged in');
        return;
      }

      self.statusMessage('Loading profile...');
      fetch(`http://localhost:8080/admin/users/${userId}`)
        .then(res => {
          if (!res.ok) {
            throw new Error('Failed to load profile');
          }
          return res.json();
        })
        .then(profile => {
          self.profileData(profile);
          self.statusMessage('Profile loaded successfully');
        })
        .catch(err => {
          console.error('Error loading profile:', err);
          self.statusMessage('Error loading profile: ' + err.message);
        });
    };

    // Back to dashboard
    self.backToDashboard = function() {
      window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
    };

    // Initial load
    self.connected = function() {
      self.loadUserProfile();
    };
  }

  return UserProfileViewModel;
});

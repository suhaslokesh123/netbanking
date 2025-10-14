define([
  'knockout',
  'ojs/ojarraydataprovider',
  'ojs/ojbutton',
  'ojs/ojinputtext',
  'ojs/ojformlayout',
  'ojs/ojselectsingle',
  'ojs/ojdialog',
  'ojs/ojtable'
], function (ko, ArrayDataProvider) {

  function UserLoansViewModel() {
    const self = this;

    // Loans data
    self.loans = ko.observableArray([]);
    self.loansDataProvider = new ArrayDataProvider(self.loans, { keyAttributes: 'loanId' });

    // Loan application form
    self.newLoanType = ko.observable('');
    self.principalAmount = ko.observable(null);
    self.interestRate = ko.observable(null);
    self.durationMonths = ko.observable(null);
    self.outstandingAmount = ko.observable(null);

    // Loan types
    self.loanTypes = ko.observableArray([
      { value: 'home', label: 'Home Loan' },
      { value: 'personal', label: 'Personal Loan' },
      { value: 'education', label: 'Education Loan' },
      { value: 'business', label: 'Business Loan' }
    ]);
    self.loanTypesDataProvider = new ArrayDataProvider(self.loanTypes);

    // Status message
    self.statusMessage = ko.observable('');

    // Load user loans
    self.loadUserLoans = function() {
      const userId = sessionStorage.getItem('userId');
      if (!userId) {
        self.statusMessage('User not logged in');
        return;
      }

      self.statusMessage('Loading your loans...');

      fetch(`http://localhost:8080/user/loans/${userId}`)
        .then(res => {
          if (!res.ok) {
            throw new Error('Failed to load loans');
          }
          return res.json();
        })
        .then(loans => {
          self.loans(loans);
          self.statusMessage(`Loaded ${loans.length} loan(s)`);
        })
        .catch(err => {
          console.error('Error loading loans:', err);
          self.statusMessage('Error loading loans: ' + err.message);
        });
    };

    // Apply for loan (form is always visible, no dialog needed)

    // Apply for loan
    self.applyForLoan = function() {
      const loanType = self.newLoanType();
      const principal = self.principalAmount();
      const interestRate = self.interestRate();
      const duration = self.durationMonths();
      const outstandingAmount = self.outstandingAmount();

      if (!loanType || !principal || !interestRate || !duration || !outstandingAmount) {
        self.statusMessage('Please fill in all fields');
        return;
      }

      if (principal < 1000) {
        self.statusMessage('Principal amount must be at least $1,000');
        return;
      }

      if (interestRate <= 0 || interestRate > 50) {
        self.statusMessage('Interest rate must be between 0.01% and 50%');
        return;
      }

      if (duration < 6 || duration > 360) {
        self.statusMessage('Duration must be between 6 and 360 months');
        return;
      }

      if (outstandingAmount < principal) {
        self.statusMessage('Outstanding amount cannot be less than principal');
        return;
      }

      self.statusMessage('Submitting loan application...');

      const userId = sessionStorage.getItem('userId');
      if (!userId) {
        self.statusMessage('User not logged in');
        return;
      }

      const loanData = {
        loanType: loanType,
        principal: parseFloat(principal),
        interestRate: parseFloat(interestRate),
        durationMonths: parseInt(duration),
        outstandingAmount: parseFloat(outstandingAmount)
      };

      fetch(`http://localhost:8080/user/loans/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(loanData)
      })
      .then(res => {
        if (!res.ok) {
          return res.json().then(err => { throw new Error(err.message || 'Loan application failed'); });
        }
        return res.json();
      })
      .then(response => {
        self.statusMessage(`Loan application submitted successfully! Loan ID: ${response.loanId}`);
        self.resetForm();
        // Reload loans to show the new one
        self.loadUserLoans();
      })
      .catch(err => {
        console.error('Error applying for loan:', err);
        self.statusMessage('Error: ' + err.message);
      });
    };

    // Reset form
    self.resetForm = function() {
      self.newLoanType('');
      self.principalAmount(null);
      self.interestRate(null);
      self.durationMonths(null);
      self.outstandingAmount(null);
    };

    // Back to dashboard
    self.backToDashboard = function() {
      window.location.href = 'http://localhost:8000/?ojr=user-dashboard';
    };

    // Initial load
    self.connected = function() {
      self.loadUserLoans();
    };
  }

  return UserLoansViewModel;
});

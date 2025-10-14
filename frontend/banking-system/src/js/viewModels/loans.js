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

  function LoansViewModel() {
    const self = this;

    // --- Observables ---
    self.loans = ko.observableArray([]);
    self.statusMessage = ko.observable('');
    self.isEditing = ko.observable(false);
    self.editLoanId = ko.observable(null);
    self.editUserId = ko.observable('');
    self.editLoanType = ko.observable('');
    self.editPrincipal = ko.observable(0);
    self.editInterestRate = ko.observable(0);
    self.editDurationMonths = ko.observable(0);
    self.editOutstandingAmount = ko.observable(0);
    self.editStatus = ko.observable('pending');

    // --- DataProvider ---
    self.dataProvider = ko.computed(() => {
      return new ArrayDataProvider(self.loans(), { keyAttributes: 'loanId' });
    });

    // --- Loan Type Options ---
    self.loanTypeOptions = [
      { value: 'home', label: 'Home Loan' },
      { value: 'personal', label: 'Personal Loan' },
      { value: 'education', label: 'Education Loan' },
      { value: 'business', label: 'Business Loan' }
    ];
    self.loanTypeProvider = new ArrayDataProvider(self.loanTypeOptions, { keyAttributes: 'value' });

    // --- Loan Status Options ---
    self.statusOptions = [
      { value: 'approved', label: 'Approved' },
      { value: 'pending', label: 'Pending' },
      { value: 'rejected', label: 'Rejected' },
      { value: 'closed', label: 'Closed' }
    ];
    self.statusOptionsProvider = new ArrayDataProvider(self.statusOptions, { keyAttributes: 'value' });

    // --- Fetch Loans ---
    self.fetchLoans = async function () {
      self.statusMessage('Fetching loans...');
      try {
        const res = await fetch('http://localhost:8080/api/admin/loans');
        if (!res.ok) throw new Error('Failed to fetch loans');
        const data = await res.json();
        self.loans(data);
        self.statusMessage(`Loaded ${data.length} loans.`);
      } catch (err) {
        console.error(err);
        self.statusMessage('Error fetching loans: ' + err.message);
      }
    };

    // --- Start Edit ---
    self.startEdit = function (loan) {
      if (!loan) return;

      self.isEditing(true);
      self.editLoanId(Number(loan.loanId));
      self.editUserId(String(loan.userId));
      self.editLoanType(loan.loanType);
      self.editPrincipal(Number(loan.principal));
      self.editInterestRate(Number(loan.interestRate));
      self.editDurationMonths(Number(loan.durationMonths));
      self.editOutstandingAmount(Number(loan.outstandingAmount));
      self.editStatus(loan.status);

      // Scroll to edit form
      setTimeout(() => {
        const editForm = document.getElementById('editLoanForm');
        if (editForm) editForm.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    };

    // --- Update Loan ---
    self.updateLoan = async function () {
      if (!self.editLoanId()) return;

      const payload = {
        loanType: self.editLoanType(),
        principal: Number(self.editPrincipal()),
        interestRate: Number(self.editInterestRate()),
        durationMonths: Number(self.editDurationMonths()),
        outstandingAmount: Number(self.editOutstandingAmount())
      };

      try {
        const res = await fetch(`http://localhost:8080/api/admin/loans/${self.editLoanId()}`, {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error(await res.text());

        // Update status separately
        if (self.editStatus()) {
          const statusRes = await fetch(`http://localhost:8080/api/admin/loans/${self.editLoanId()}/status?status=${self.editStatus()}`, {
            method: 'PATCH'
          });
          if (!statusRes.ok) throw new Error(await statusRes.text());
        }

        self.statusMessage('Loan updated successfully!');
        self.cancelEdit();
        self.fetchLoans();
      } catch (err) {
        self.statusMessage('Error updating loan: ' + err.message);
      }
    };

    // --- Cancel Edit ---
    self.cancelEdit = function () {
      self.isEditing(false);
      self.editLoanId(null);
      self.editUserId('');
      self.editLoanType('');
      self.editPrincipal(0);
      self.editInterestRate(0);
      self.editDurationMonths(0);
      self.editOutstandingAmount(0);
      self.editStatus('pending');
    };

    // --- Refresh Loans ---
    self.refreshLoans = function () {
      self.fetchLoans();
    };

    // --- Initial Load ---
    self.fetchLoans();
  }

  return LoansViewModel;
});

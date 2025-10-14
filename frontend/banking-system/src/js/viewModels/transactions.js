define(['knockout', 'ojs/ojarraydataprovider', 'ojs/ojtable', 'ojs/ojbutton'], function(ko, ArrayDataProvider) {
  function TransactionsViewModel() {
    const self = this;

    // --- Observables ---
    self.transactions = ko.observableArray([]);
    self.statusMessage = ko.observable('');

    // --- DataProvider for oj-table ---
    self.dataProvider = ko.computed(() => new ArrayDataProvider(self.transactions(), { keyAttributes: 'txnId' }));

    // --- Fetch transactions from API ---
    self.fetchTransactions = async function() {
      self.statusMessage('Fetching transactions...');
      try {
        const res = await fetch('http://localhost:8080/admin/transactions'); // Replace with your API URL
        if (!res.ok) throw new Error('Failed to fetch transactions');

        const data = await res.json();
        self.transactions(data);
        self.statusMessage(`Loaded ${data.length} transactions.`);
      } catch (err) {
        console.error(err);
        self.statusMessage('Error fetching transactions.');
      }
    };

    // --- Auto-fetch on load ---
    self.fetchTransactions();
  }

  return TransactionsViewModel;
});

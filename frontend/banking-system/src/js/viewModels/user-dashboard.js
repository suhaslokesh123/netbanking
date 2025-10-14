/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * User Dashboard ViewModel
 */
define(['knockout'], function(ko) {
    function UserDashboardViewModel() {
        const self = this;

        // Statistics observables
        self.totalAccounts = ko.observable(0);
        self.totalCards = ko.observable(0);
        self.recentTransactions = ko.observable(0);
        self.accountBalance = ko.observable(0);

        // Load user statistics
        self.loadUserStats = function() {
            const userId = sessionStorage.getItem('userId');
            if (!userId) return;

            // Load accounts for this user
            fetch(`http://localhost:8080/user/accounts/${userId}`)
                .then(res => res.json())
                .then(accounts => {
                    self.totalAccounts(accounts.length);
                    // Calculate total balance
                    const totalBalance = accounts.reduce((sum, account) => sum + (account.balance || 0), 0);
                    self.accountBalance(totalBalance.toFixed(2));
                })
                .catch(err => console.error('Error loading accounts:', err));

            // Load cards for this user (by getting accounts first, then cards for each account)
            fetch(`http://localhost:8080/user/accounts/${userId}`)
                .then(res => res.json())
                .then(accounts => {
                    if (accounts.length === 0) {
                        self.totalCards(0);
                        return;
                    }

                    // Get cards for each account
                    const cardPromises = accounts.map(account =>
                        fetch(`http://localhost:8080/user/cards/account/${account.accountNo}`)
                            .then(res => res.json())
                            .catch(err => {
                                console.error(`Error loading cards for account ${account.accountNo}:`, err);
                                return []; // Return empty array on error
                            })
                    );

                    return Promise.all(cardPromises).then(cardArrays => {
                        // Count total cards across all accounts
                        const totalCards = cardArrays.reduce((sum, cards) => sum + cards.length, 0);
                        self.totalCards(totalCards);
                    });
                })
                .catch(err => {
                    console.error('Error loading accounts for cards:', err);
                    self.totalCards(0);
                });

            // Load recent transactions (get from all user accounts)
            fetch(`http://localhost:8080/user/accounts/${userId}`)
                .then(res => res.json())
                .then(accounts => {
                    if (accounts.length === 0) {
                        self.recentTransactions(0);
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

                        // Count transactions from last 30 days
                        const thirtyDaysAgo = new Date();
                        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

                        const recentTxns = allTransactions.filter(txn => {
                            const txnDate = new Date(txn.txnDate);
                            return txnDate >= thirtyDaysAgo;
                        });

                        self.recentTransactions(recentTxns.length);
                    });
                })
                .catch(err => {
                    console.error('Error loading accounts for transactions:', err);
                    self.recentTransactions(0);
                });
        };

        // Navigation functions
        self.navigateToAccounts = function() {
            window.location.href = 'http://localhost:8000/?ojr=accounts';
        };

        self.navigateToCards = function() {
            window.location.href = 'http://localhost:8000/?ojr=cards';
        };

        self.navigateToTransactions = function() {
            window.location.href = 'http://localhost:8000/?ojr=user-transactions';
        };

        self.navigateToFundTransfer = function() {
            window.location.href = 'http://localhost:8000/?ojr=fund-transfer';
        };

        self.navigateToProfile = function() {
            alert('Profile page coming soon!');
        };

        // --- Connected ---
        self.connected = function() {
            document.title = "User Dashboard";
            self.loadUserStats();
        };

        // --- Disconnected ---
        self.disconnected = function() {
            // Implement if needed
        };

        // --- Transition Completed ---
        self.transitionCompleted = function() {
            // Implement if needed
        };
    }

    return UserDashboardViewModel;
});

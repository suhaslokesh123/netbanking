/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your dashboard ViewModel code goes here
 */
define(['knockout'], function(ko) {
    function DashboardViewModel() {
        const self = this;

        // --- Observables ---
        self.totalUsers = ko.observable(0);
        self.totalAccounts = ko.observable(0);
        self.totalCards = ko.observable(0);
        self.totalTransactions = ko.observable(0);

        // --- Load dashboard statistics ---
        self.loadStats = async function() {
            try {
                // Load users
                const usersRes = await fetch('http://localhost:8080/admin/users');
                if (usersRes.ok) {
                    const users = await usersRes.json();
                    self.totalUsers(users.length);
                }

                // Load accounts
                const accountsRes = await fetch('http://localhost:8080/admin/accounts');
                if (accountsRes.ok) {
                    const accounts = await accountsRes.json();
                    self.totalAccounts(accounts.length);
                }

                // Load cards
                const cardsRes = await fetch('http://localhost:8080/admin/cards');
                if (cardsRes.ok) {
                    const cards = await cardsRes.json();
                    self.totalCards(cards.length);
                }

                // Load transactions
                const transactionsRes = await fetch('http://localhost:8080/admin/transactions');
                if (transactionsRes.ok) {
                    const transactions = await transactionsRes.json();
                    self.totalTransactions(transactions.length);
                }
            } catch (err) {
                console.error('Error loading dashboard stats:', err);
            }
        };

        // --- Connected ---
        self.connected = function() {
            document.title = "Admin Dashboard";
            self.loadStats();
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

    return DashboardViewModel;
});

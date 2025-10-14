/**
 * @license
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates.
 * Licensed under The Universal Permissive License (UPL), Version 1.0
 * as shown at https://oss.oracle.com/licenses/upl/
 * @ignore
 */
/*
 * Your application specific code will go here
 */
define(['knockout', 'ojs/ojcontext', 'ojs/ojmodule-element-utils', 'ojs/ojknockouttemplateutils', 'ojs/ojcorerouter', 'ojs/ojmodulerouter-adapter', 'ojs/ojknockoutrouteradapter', 'ojs/ojurlparamadapter', 'ojs/ojresponsiveutils', 'ojs/ojresponsiveknockoututils', 'ojs/ojarraydataprovider',
        'ojs/ojdrawerpopup', 'ojs/ojmodule-element', 'ojs/ojknockout'],
  function(ko, Context, moduleUtils, KnockoutTemplateUtils, CoreRouter, ModuleRouterAdapter, KnockoutRouterAdapter, UrlParamAdapter, ResponsiveUtils, ResponsiveKnockoutUtils, ArrayDataProvider) {

     function ControllerViewModel() {

      this.KnockoutTemplateUtils = KnockoutTemplateUtils;

      // Handle announcements sent when pages change, for Accessibility.
      this.manner = ko.observable('polite');
      this.message = ko.observable();
      announcementHandler = (event) => {
          this.message(event.detail.message);
          this.manner(event.detail.manner);
      };

      document.getElementById('globalBody').addEventListener('announce', announcementHandler, false);


      // Media queries for responsive layouts
      const smQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.SM_ONLY);
      this.smScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(smQuery);
      const mdQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.MD_UP);
      this.mdScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(mdQuery);

      let navData = [
        { path: '', redirect: 'login' },
        { path: 'login' },
        { path: 'dashboard', detail: { label: 'Dashboard', iconClass: 'oj-ux-ico-bar-chart' } },
        { path: 'user-dashboard', detail: { label: 'User Dashboard', iconClass: 'oj-ux-ico-bar-chart' } },
        { path: 'user-accounts', detail: { label: 'My Accounts', iconClass: 'oj-ux-ico-contact-group' } },
        { path: 'user-cards', detail: { label: 'My Cards', iconClass: 'oj-ux-ico-rectangle' } },
        { path: 'user-loans', detail: { label: 'My Loans', iconClass: 'oj-ux-ico-calculator' } },
        { path: 'user-profile', detail: { label: 'My Profile', iconClass: 'oj-ux-ico-person' } },
        { path: 'user-transactions', detail: { label: 'My Transactions', iconClass: 'oj-ux-ico-cash' } },
        { path: 'fund-transfer', detail: { label: 'Fund Transfer', iconClass: 'oj-ux-ico-transfer' } },
        { path: 'users', detail: { label: 'Users', iconClass: 'oj-ux-ico-contact-group' } },
        { path: 'accounts', detail: { label: 'Accounts', iconClass: 'oj-ux-ico-contact-group' } },
        { path: 'cards', detail: { label: 'Cards', iconClass: 'oj-ux-ico-rectangle' } },
        { path: 'loans', detail: { label: 'Loans', iconClass: 'oj-ux-ico-calculator' } },
        { path: 'transactions', detail: { label: 'Transactions', iconClass: 'oj-ux-ico-rectangle' } },
      ];

      // Router setup
      let router = new CoreRouter(navData, {
        urlAdapter: new UrlParamAdapter()
      });
      router.sync();

      // Make router globally accessible
      window.router = router;

      this.moduleAdapter = new ModuleRouterAdapter(router);

      this.selection = new KnockoutRouterAdapter(router);

      // Role-based nav
      this.currentRole = ko.observable(sessionStorage.getItem('role') || '');
      window.currentRole = this.currentRole;

      // Setup the navDataProvider with the routes, excluding the first redirected
      // route, and filtered by role
      this.navDataProvider = ko.computed(() => {
        let filteredNav = navData.slice(1).filter(item => {
          if (item.path === 'login') return false; // never show login in nav
          if (this.currentRole() === 'admin') {
            // admin sees only admin pages (dashboard, users, accounts, cards, transactions)
            return !item.path.startsWith('user-') && item.path !== 'fund-transfer';
          }
          if (this.currentRole() === 'user') {
            // user sees only user-dashboard (fund-transfer is accessed via Quick Actions button)
            return item.path === 'user-dashboard';
          }
          return false; // not logged in, no nav
        });
        return new ArrayDataProvider(filteredNav, {keyAttributes: "path"});
      });

      // Drawer
      self.sideDrawerOn = ko.observable(false);

      // Close drawer on medium and larger screens
      this.mdScreen.subscribe(() => { self.sideDrawerOn(false) });

      // Called by navigation drawer toggle button and after selection of nav drawer item
      this.toggleDrawer = () => {
        self.sideDrawerOn(!self.sideDrawerOn());
      }

      // Sign out function (globally accessible for dashboard)
      window.signOut = () => {
        // Clear session
        sessionStorage.clear();
        // Update global role
        if (window.currentRole) {
          window.currentRole('');
        }
        // Redirect to root URL
        window.location.href = 'http://localhost:8000/';
      };

      // Header
      // Application Name used in Branding Area
      this.appName = ko.observable("Oracle Banking");

     
     }
     // release the application bootstrap busy state
     Context.getPageContext().getBusyContext().applicationBootstrapComplete();

     return new ControllerViewModel();
  }
);

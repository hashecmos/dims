/**
 * System configuration for Angular samples
 * Adjust as necessary for your application needs.
 */
(function (global) {
  System.config({
    // map tells the System loader where to look for things
    map: {
      // our app is within the app folder
      app: 'dist/app',
      '@ng-bootstrap/ng-bootstrap': 'npm:@ng-bootstrap/ng-bootstrap/bundles/ng-bootstrap.js',
      '@angular/core': 'npm:@angular/core/bundles/core.umd.js',
      '@angular/common': 'npm:@angular/common/bundles/common.umd.js',
      '@angular/compiler': 'npm:@angular/compiler/bundles/compiler.umd.js',
      '@angular/platform-browser': 'npm:@angular/platform-browser/bundles/platform-browser.umd.js',
      '@angular/platform-browser-dynamic': 'npm:@angular/platform-browser-dynamic/bundles/platform-browser-dynamic.umd.js',
      '@angular/http': 'npm:@angular/http/bundles/http.umd.js',
      '@angular/router': 'npm:@angular/router/bundles/router.umd.js',
      '@angular/router/upgrade': 'npm:@angular/router/bundles/router-upgrade.umd.js',
      '@angular/forms': 'npm:@angular/forms/bundles/forms.umd.js',
      '@angular/upgrade': 'npm:@angular/upgrade/bundles/upgrade.umd.js',
      '@angular/upgrade/static': 'npm:@angular/upgrade/bundles/upgrade-static.umd.js',

      // other libraries
      'rxjs':                      'npm:rxjs',
      'angular-in-memory-web-api': 'npm:angular-in-memory-web-api/bundles/in-memory-web-api.umd.js',
      'ng2-popover': 'npm:ng2-popover',
      'angular2-tree-component': 'npm:angular2-tree-component/dist/angular2-tree-component.umd.js',
      'lodash':                     'npm:lodash',
      'ng2-auto-complete': 'npm:ng2-auto-complete/dist',
      'ng2-accordion': 'npm:ng2-accordion',
      "ng2-modal": "npm:ng2-modal",
      'ng2-pagination':   'npm:ng2-pagination/dist',
      'ng2-mobx':   'npm:ng2-mobx/dist',
      'mobx':   'npm:mobx/lib',
      'mydatepicker': 'npm:mydatepicker/bundles/mydatepicker.umd.js',
      'mydaterangepicker': 'npm:mydaterangepicker/bundles/mydaterangepicker.umd.js',
      'primeng':'npm:primeng',
      'quill':'npm:quill/dist'
    },
    // packages tells the System loader how to load when no filename and/or no extension
    packages: {
      app: {
        main: './main.js',
        defaultExtension: 'js'
      },
      rxjs: {
        defaultExtension: 'js'
      },
      "ng2-popover": {
              "main": "index.js",
              "defaultExtension": "js"
            },
      'ng2-auto-complete'         : { main: 'ng2-auto-complete.umd.js', defaultExtension: 'js' },
      "ng2-accordion"             : { main: 'index.js', defaultExtension: 'js' },
      "ng2-modal": { "main": "index.js", "defaultExtension": "js" },
      'ng2-mobx':             { main: 'ng2-mobx.umd.js', defaultExtension: 'js' },
      'mobx':             { main: 'mobx.js', defaultExtension: 'js' },
      'ng2-pagination':             { main: 'ng2-pagination.js', defaultExtension: 'js' },
      'primeng':     {main:'primeng.js',defaultExtension:'js'},
      'quill':			{main:'quill.js',defaultExtension:'js'},
      'lodash':                    {main:'index.js', defaultExtension:'js'}
    }
  });
})(this);

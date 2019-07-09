// http://stackoverflow.com/a/37874643/171968

var gulp = require('gulp'),
  tsc = require('gulp-typescript'),
  Builder = require('systemjs-builder'),
  inlineNg2Template = require('gulp-inline-ng2-template'),
  exec = require('child_process').exec,
  rimraf = require('rimraf');

var builderConfig = {
  paths: {
    // paths serve as alias
    'npm:': 'node_modules/'
  },
  // map tells the System loader where to look for things
  map: {
    // our app is within the app folder
    app: 'dist/app',

    // angular bundles
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
//packages tells the System loader how to load when no filename and/or no extension
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
}

gulp.task('bundle', ['bundle-app'], function(){});

gulp.task('inline-templates', function () {
  rimraf('dist/app', function(){});
  return gulp.src('app/**/*.ts')
    .pipe(inlineNg2Template({ UseRelativePaths: true, indent: 0, removeLineBreaks: true}))
    .pipe(tsc({
      "target": "es5",
      "module": "commonjs",
      "moduleResolution": "node",
      "sourceMap": true,
      "emitDecoratorMetadata": true,
      "experimentalDecorators": true,
      "lib": [ "es2015", "dom" ],
      "noImplicitAny": true,
      "suppressImplicitAnyIndexErrors": true,
      "noImplicitAny": false,
      "outDir": "dist",
    }))
    .pipe(gulp.dest('dist/app'));
});

gulp.task('bundle-app', ['inline-templates'], function() {
  // optional constructor options
  // sets the baseURL and loads the configuration file
  var builder = new Builder('./', builderConfig);

  builder
  .bundle('dist/app/**/* - [@angular/**/*.js] - [rxjs/**/*.js]', 'bundles/app.bundle.js', { minify: true})
  .then(function() {
    console.log('App Build complete');
    builder
      .bundle('dist/app/* - [dist/app/**/*]', 'bundles/dependencies.bundle.js', { minify: true})
      .then(function() {
        console.log('Dependencies Build complete');
        fs = require('fs');

        fs.createReadStream('styles.css').pipe(fs.createWriteStream('bundles/styles.css'));
        fs.createReadStream('stylesie.css').pipe(fs.createWriteStream('bundles/stylesie.css'));
        fs.createReadStream('jquery.min.js').pipe(fs.createWriteStream('bundles/jquery.min.js'));
        
        rimraf('bundles/bootstrap', function(){});
        rimraf('bundles/css', function(){});
        rimraf('bundles/images', function(){});
        rimraf('bundles/fonts', function(){});

        gulp.src([
          '*bootstrap/js/*',
          '*bootstrap/css/*',
          '*bootstrap/fonts/*',
          '*css/**/*',
          '*fonts/**/*',
          '*images/**/*',
        ])
        .pipe(gulp.dest('bundles/'));
        console.log('Static files copied')
      })
      .catch(function(err) {
        console.log('Dependencies Build error');
        console.log(err);
      });
  })
  .catch(function(err) {
    console.log('App Build error');
    console.log(err);
  });
});

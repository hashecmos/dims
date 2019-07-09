import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }              from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';
import {Component} from '@angular/core'
import {BrowseService} from '../.././app/services/browse.service'
import {Router, ActivatedRoute} from '@angular/router';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import global = require('../.././app/global.variables')
import { Output, EventEmitter } from '@angular/core';

describe('HelpSupportLogoutService description', () => {
  let subject: BrowseService = null;
  let backend: MockBackend = null;
  let http: Http = null

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: BrowseService }, { provide: MockBackend }, { provide: Http }]
    })
      .compileComponents().then(() => {

      });
  }));


  beforeEach(inject([BrowseService, MockBackend], (logoutService: BrowseService, mockBackend: MockBackend) => {
    subject = logoutService;
    backend = mockBackend;
  }));

  it('#BrowseService getfolderTree result', inject([BrowseService], (browseservice: BrowseService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/FilenetService/getfolderTree?folderPath=/&osName=ECM');
    });
  }));

  it('#BrowseService getContainees result', inject([BrowseService], (helpsupport: BrowseService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/FilenetService/getContainees?osName=ECM&folderId=1');
    });
  }));

  it('#BrowseService getCurrentFolderDocs result', inject([BrowseService], (helpsupport: BrowseService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/FilenetService/getContainees?osName=ECM&folderId=1');
    });
  }));

  it('#BrowseService getNodes Definition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getNodes).toBeDefined();
  }));
});

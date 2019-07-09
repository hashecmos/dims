import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }              from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';
import {Component} from '@angular/core'
import {BrowseService} from '../.././app/services/browse.service'
import {Router, ActivatedRoute} from '@angular/router';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import global = require('./../../app/global.variables')
import { Output, EventEmitter } from '@angular/core';


describe('BrowseService component', () => {
  let subject: BrowseService = null;
  let backend: MockBackend = null;
  let http: Http = null;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: BrowseService }, { provide: MockBackend }, { provide: Http }]
    })
      .compileComponents().then(() => {

      });
  }));


  beforeEach(inject([BrowseService, MockBackend], (browseservice: BrowseService, mockBackend: MockBackend) => {
    subject = browseservice;
    backend = mockBackend;
  }));



  it('getfolderTree function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/FilenetService/getfolderTree?folderPath=/&osName=ECM');
    });
  }));


  it('getContainees function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/FilenetService/getContainees?osName=ECM&folderId=1');
    });
  }));


  it('getNodes function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getNodes).toBeDefined();
  }));

  it('addNode function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.addNode).toBeDefined();
  }));

  it('moveNode function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.moveNode).toBeDefined();
  }));

  it('pluckNodeFromList function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.pluckNodeFromList).toBeDefined();
  }));

  it('getChildrenForNode function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getChildrenForNode).toBeDefined();
  }));

  it('totalFoldersCount function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.totalFoldersCount).toBeDefined();
  }));


  it('findNode function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.findNode).toBeDefined();
  }));

  it('getDocuments function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getDocuments).toBeDefined();
  }));

  it('moveDocument function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.moveDocument).toBeDefined();
  }));


  it('launchExistingDocument function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.launchExistingDocument).toBeDefined();
  }));

  it('getDocVersions function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getDocVersions).toBeDefined();
  }));

  it('getDocFolders function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getDocFolders).toBeDefined();
  }));

  it('getDocHistory function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.getDocHistory).toBeDefined();
  }));

  it('SearchDocuments function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.SearchDocuments).toBeDefined();
  }));

  it('AdvanceSearchDocuments function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.AdvanceSearchDocuments).toBeDefined();
  }));

  it('docCheckIn function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.docCheckIn).toBeDefined();
  }));

  it('docCheckOut function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.docCheckOut).toBeDefined();
  }));

  it('cancelCheckout function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.cancelCheckout).toBeDefined();
  }));

  it('newFolder function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.newFolder).toBeDefined();
  }));

  it('sendDocumentMail function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.sendDocumentMail).toBeDefined();
  }));


  it('loadDocumentsFileProperties function defnition', inject([BrowseService], (browseservice: BrowseService) => {
    browseservice = new BrowseService(http);
    expect(browseservice.loadDocumentsFileProperties).toBeDefined();
  }));

});

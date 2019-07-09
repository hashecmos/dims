import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }              from '@angular/platform-browser';
import { DebugElement }    from '@angular/core';
import {Component} from '@angular/core'
import {BrowseSharedService} from '../.././app/services/browseEvents.shared.service'
import {Router, ActivatedRoute} from '@angular/router';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import global = require('./../../app/global.variables')
import { Output, EventEmitter } from '@angular/core';
import { Subject } from 'rxjs/Subject';


describe('BrowseSharedService component', () => {
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: BrowseSharedService }]
    })
      .compileComponents();
  }));

  it('BrowseSharedService Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared).toBeDefined();
  }));

  it('BrowseSharedService attributes Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    let emitchange = new Subject();
    browseshared.emitChangeSource = emitchange;
    browseshared.emitChangeActiveNode = emitchange;
    browseshared.emitBrowseLaunchPopup = emitchange;
    browseshared.emitSideNavReload = emitchange;
    browseshared.emitAddedUsers = emitchange;
    browseshared.emitFolderfilterForInbox = emitchange;
    browseshared.emitFolderfilterForSent = emitchange;
    browseshared.emitFolderfilterForArchive = emitchange;
    expect(browseshared.emitChangeSource).toBe(emitchange);
    expect(browseshared.emitChangeActiveNode).toBe(emitchange);
    expect(browseshared.emitBrowseLaunchPopup).toBe(emitchange);
    expect(browseshared.emitSideNavReload).toBe(emitchange);
    expect(browseshared.emitAddedUsers).toBe(emitchange);
    expect(browseshared.emitFolderfilterForInbox).toBe(emitchange);
    expect(browseshared.emitFolderfilterForSent).toBe(emitchange);
    expect(browseshared.emitFolderfilterForArchive).toBe(emitchange);
  }));

  it('emitChangeNode function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitChangeNode).toBeDefined();
  }));

  it('emitChange function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitChange).toBeDefined();
  }));

  it('emitBrowseLaunchPopupOpen function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitBrowseLaunchPopupOpen).toBeDefined();
  }));

  it('emitSideNavigationReload function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitSideNavigationReload).toBeDefined();
  }));

  it('emitSelectedUsers function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitSelectedUsers).toBeDefined();
  }));


  it('emitFolderFilterForInboxItems function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitFolderFilterForInboxItems).toBeDefined();
  }));

  it('emitFolderFilterForSentItems function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitFolderFilterForSentItems).toBeDefined();
  }));

  it('emitFolderFilterForArchiveItems function Definition', inject([BrowseSharedService], (browseshared: BrowseSharedService) => {
    browseshared = new BrowseSharedService();
    expect(browseshared.emitFolderFilterForArchiveItems).toBeDefined();
  }));




});

import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {SharedService} from '../.././app/services/advanceSearchTasks.service'
import { Subject } from 'rxjs/Subject';

describe('advanceSearchTasks', function() {
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SharedService }]
    })
      .compileComponents();
  }));

  it('advanceSearchTasks Definition', inject([SharedService], (sharedservice: SharedService) => {
    sharedservice = new SharedService();
    expect(sharedservice).toBeDefined();
  }));

  it('advanceSearchTasks attributes Definition', inject([SharedService], (sharedservice: SharedService) => {
    sharedservice = new SharedService();
    let emitsearch = new Subject();
    let emitsearchdoc = new Subject();
    sharedservice.emitSearchedTasks = emitsearch;
    sharedservice.emitSearchedDocuments = emitsearchdoc;
    expect(sharedservice.emitSearchedTasks).toBe(emitsearch);
    expect(sharedservice.emitSearchedDocuments).toBe(emitsearchdoc);
  }));

  it('advanceSearchTasks emitTasks Definition', inject([SharedService], (sharedservice: SharedService) => {
    sharedservice = new SharedService();
    expect(sharedservice.emitTasks).toBeDefined();
  }));

  it('advanceSearchTasks emitDocuments Definition', inject([SharedService], (sharedservice: SharedService) => {
    sharedservice = new SharedService();
    expect(sharedservice.emitDocuments).toBeDefined();
  }));
});

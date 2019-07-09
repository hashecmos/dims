import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {SimpleSearchForm} from '../.././app/model/simpleSearchForm.model'

describe('Directorate', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SimpleSearchForm }]
    })
      .compileComponents();
  }));

  it('SimpleSearchForm Definition', inject([SimpleSearchForm], (simplesearch: SimpleSearchForm) => {
    simplesearch = new SimpleSearchForm();
    expect(simplesearch).toBeDefined();
  }));

  it('SimpleSearchForm attributeDataTypeMap Definition', inject([SimpleSearchForm], (simplesearch: SimpleSearchForm) => {
    simplesearch = new SimpleSearchForm();
    expect(simplesearch.attributeDataTypeMap).toBeDefined();
  }));

  it('SimpleSearchForm attributes Definition', inject([SimpleSearchForm], (simplesearch: SimpleSearchForm) => {
    simplesearch = new SimpleSearchForm();
    simplesearch.DocumentTitle = "DocumentTitle";
    simplesearch.Subject = "Subject";
    simplesearch.dateCreated = "dateCreated";
    expect(simplesearch.DocumentTitle).toBe("DocumentTitle");
    expect(simplesearch.Subject).toBe("Subject");
    expect(simplesearch.dateCreated).toBe("dateCreated");
  }));



});

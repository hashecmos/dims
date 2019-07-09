import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {AdvanceSearchRequestObject} from '../.././app/model/advanceSearchRequestObject.model'

describe('AdvanceSearchForm', function() {
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AdvanceSearchRequestObject }]
    })
      .compileComponents();
  }));

  it('AdvanceSearchForm Definition', inject([AdvanceSearchRequestObject], (advancesearch: AdvanceSearchRequestObject) => {
    advancesearch = new AdvanceSearchRequestObject();
    expect(advancesearch).toBeDefined();
  }));
});

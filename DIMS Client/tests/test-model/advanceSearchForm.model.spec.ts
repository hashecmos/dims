import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {AdvanceSearchForm} from '../.././app/model/advanceSearchForm.model'

describe('AdvanceSearchForm', function() {
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AdvanceSearchForm }]
    })
      .compileComponents();
  }));

  it('AdvanceSearchForm Definition', inject([AdvanceSearchForm], (advance: AdvanceSearchForm) => {
    advance = new AdvanceSearchForm();
    expect(advance).toBeDefined();
  }));

  it('AdvanceSearchForm attributes Definition', inject([AdvanceSearchForm], (advance: AdvanceSearchForm) => {
    advance = new AdvanceSearchForm();
    advance.DocumentTitleSearchOperator = "DocumentTitleSearchOperator";
    advance.idSearchOperator = "idSearchOperato";
    advance.SubjectSearchOperator = "SubjectSearchOperator";
    advance.dateCreatedSearchOperator = "dateCreatedSearchOperator";
    expect(advance.DocumentTitleSearchOperator).toBe("DocumentTitleSearchOperator");
    expect(advance.idSearchOperator).toBe("idSearchOperato");
    expect(advance.SubjectSearchOperator).toBe("SubjectSearchOperator");
    expect(advance.dateCreatedSearchOperator).toBe("dateCreatedSearchOperator");
  }));
});

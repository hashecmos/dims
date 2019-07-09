import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {DocumentResult} from '../.././app/model/documentResult.model'

describe('DocumentResult', function() {
  let de: DebugElement;
  let className = "DocumentResult";
  let symbolicName = "symbolicName";
  let subject = "subject";
  let document_id = 1;
  let dateCreated = "10/03/17";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DocumentResult }]
    })
      .compileComponents();
  }));


  it('DocumentInFolder Definition', inject([DocumentResult], (docres: DocumentResult) => {
    docres = new DocumentResult(className, symbolicName, subject, document_id, dateCreated);
    expect(docres).toBeDefined();
  }));


  it('DocumentInFolder attributes Definition', inject([DocumentResult], (docres: DocumentResult) => {
    docres = new DocumentResult(className, symbolicName, subject, document_id, dateCreated);
    docres.formatedCreatedDate = "11/02/2017";
    expect(docres.formatedCreatedDate).toBe("11/02/2017");
  }));

});

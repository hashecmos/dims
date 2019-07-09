import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {DocumentVersion} from '../.././app/model/docVersionContainee.model'

describe('DocumentVersion', function() {
  let de: DebugElement;
  let attrs = "attr";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DocumentVersion }]
    })
      .compileComponents();
  }));

  it('DocumentVersion Definition', inject([DocumentVersion], (docversion: DocumentVersion) => {
    docversion = new DocumentVersion(attrs);
    expect(docversion).toBeDefined();
  }));

  it('DocumentVersion attributes Definition', inject([DocumentVersion], (docversion: DocumentVersion) => {
    docversion = new DocumentVersion(attrs);
    docversion.createdBy = "alex";
    docversion.dateCreated = "11/02/2017";
    docversion.hasLinks = true;
    docversion.hasMultipleAttachments = true;
    docversion.id = "1";
    docversion.majorVersion = 2;
    docversion.reserved = true;
    docversion.size = 25;
    expect(docversion.createdBy).toBe("alex");
    expect(docversion.dateCreated).toBe("11/02/2017");
    expect(docversion.hasLinks).toBe(true);
    expect(docversion.hasMultipleAttachments).toBe(true);
    expect(docversion.id).toBe("1");
    expect(docversion.majorVersion).toBe(2);
    expect(docversion.reserved).toBe(true);
    expect(docversion.size).toBe(25);

  }));

});

import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {DocumentAttachment} from '../.././app/model/docAttachmentContainee.model'

describe('DocumentAttachment', function() {
  let attr = [1, 2, 3];
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DocumentAttachment }]
    })
      .compileComponents();
  }));

  it('DocumentAttachment Definition', inject([DocumentAttachment], (docattach: DocumentAttachment) => {
    docattach = new DocumentAttachment(attr);
    expect(docattach).toBeDefined();
  }));

  it('DocumentAttachment attributes Definition', inject([DocumentAttachment], (docattach: DocumentAttachment) => {
    docattach = new DocumentAttachment(attr);
    docattach.elementSequenceNo = "1";
    docattach.fileName = "docfile";
    docattach.mimeType = "mimeType";
    expect(docattach.elementSequenceNo).toBe("1");
    expect(docattach.fileName).toBe("docfile");
    expect(docattach.mimeType).toBe("mimeType");
  }));

});

import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {DocumentInFolder} from '../.././app/model/docFolderContainee.model'

describe('DocumentInFolder', function() {
  let de: DebugElement;
  let data = "xyz";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DocumentInFolder }]
    })
      .compileComponents();
  }));

  it('DocumentInFolder Definition', inject([DocumentInFolder], (docin: DocumentInFolder) => {
    docin = new DocumentInFolder(data);
    expect(docin).toBeDefined();
  }));

  it('DocumentInFolder attributes Definition', inject([DocumentInFolder], (docin: DocumentInFolder) => {
    docin = new DocumentInFolder(data);
    docin.classDescription = "classDescription";
    docin.className = "className";
    docin.createdBy = "john";
    docin.dateCreated = "11/01/2017";
    docin.dateModified = "12/01/2017";
    docin.hasLinks = true;
    docin.hasMultipleAttachments = true;
    docin.id = "1";
    docin.lastModifiedBy = "amar";
    docin.majorVersion = 2016;
    docin.mimeType = "mime";
    docin.objectStoreName = "objname";
    docin.objectType = "objtype";
    docin.parentPath = "parentpath";
    docin.reserved = true;
    docin.size = 60;
    docin.symbolicName = "symbolicName";
    docin.versionSeriesId = "versionSeriesId";
    docin.is_checked = false;
    docin.showOptions = false;
    expect(docin.classDescription).toBe("classDescription");
    expect(docin.className).toBe("className");
    expect(docin.createdBy).toBe("john");
    expect(docin.dateCreated).toBe("11/01/2017");
    expect(docin.dateModified).toBe("12/01/2017");
    expect(docin.hasLinks).toBe(true);
    expect(docin.hasMultipleAttachments).toBe(true);
    expect(docin.id).toBe("1");
    expect(docin.lastModifiedBy).toBe("amar");
    expect(docin.majorVersion).toBe(2016);
    expect(docin.mimeType).toBe("mime");
    expect(docin.objectType).toBe("objtype");
    expect(docin.objectStoreName).toBe("objname");
    expect(docin.parentPath).toBe("parentpath");
    expect(docin.reserved).toBe(true);
    expect(docin.size).toBe(60);
    expect(docin.symbolicName).toBe("symbolicName");
    expect(docin.versionSeriesId).toBe("versionSeriesId");
    expect(docin.is_checked).toBe(false);
    expect(docin.showOptions).toBe(false);
  }));
});

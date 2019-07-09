import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {SimpleSearchRequestObject} from '../.././app/model/simpleSearchRequestObject.model'

describe('SimpleSearchRequestObject', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SimpleSearchRequestObject }]
    })
      .compileComponents();
  }));

  it('SimpleSearchRequestObject Definition', inject([SimpleSearchRequestObject], (simplesearch: SimpleSearchRequestObject) => {
    simplesearch = new SimpleSearchRequestObject();
    expect(simplesearch).toBeDefined();
  }));
  it('SimpleSearchRequestObject attributes Definition', inject([SimpleSearchRequestObject], (simplesearch: SimpleSearchRequestObject) => {
    simplesearch = new SimpleSearchRequestObject();
    simplesearch.searchBaseType = "searchBaseType";
    simplesearch.objectStore = "objectStore";
    simplesearch.folderName = "folderName";
    simplesearch.includeSubClasses = true;
    simplesearch.searchType = "searchType";
    simplesearch.includeSubFolders = true;
    expect(simplesearch.searchBaseType).toBe("searchBaseType");
    expect(simplesearch.objectStore).toBe("objectStore");
    expect(simplesearch.folderName).toBe("folderName");
    expect(simplesearch.includeSubClasses).toBe(true);
    expect(simplesearch.searchType).toBe("searchType");
    expect(simplesearch.includeSubFolders).toBe(true);

  }));

});

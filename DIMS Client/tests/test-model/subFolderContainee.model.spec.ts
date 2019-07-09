import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {SubFolder} from '../.././app/model/subFolderContainee.model'

describe('SubFolder', function() {
  let de: DebugElement;
  let attrs = "attr";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SubFolder }]
    })
      .compileComponents();
  }));

  it('SubFolder Definition', inject([SubFolder], (subFolder: SubFolder) => {
    subFolder = new SubFolder(attrs);
    expect(subFolder).toBeDefined();
  }));

  it('SubFolder getFolderName Definition', inject([SubFolder], (subFolder: SubFolder) => {
    subFolder = new SubFolder(attrs);
    expect(subFolder.getFolderName).toBeDefined();
  }));

  it('SubFolder attributes Definition', inject([SubFolder], (subFolder: SubFolder) => {
    subFolder = new SubFolder(attrs);
    subFolder.classDescription = "classDescription";
    subFolder.className = "className";
    subFolder.createdBy = "john";
    subFolder.dateCreated = "30/11/2017";
    subFolder.folderPath = "folderPath";
    subFolder.id = "1";
    subFolder.isFilingAllowed = "isFilingAllowed";
    subFolder.lastModifiedBy = "lastModifiedBy";
    subFolder.objectStoreName = "objectStoreName";
    subFolder.objectType = "objectType";
    subFolder.parentPath = "parentPath";
    subFolder.path = "path";
    subFolder.symbolicName = "symbolicName";
    subFolder.type = "type";
    subFolder.Name = "name";
    subFolder.is_checked = false;
    subFolder.visible = false;

    expect(subFolder.classDescription).toBe("classDescription");
    expect(subFolder.className).toBe("className");
    expect(subFolder.createdBy).toBe("john");
    expect(subFolder.dateCreated).toBe("30/11/2017");
    expect(subFolder.folderPath).toBe("folderPath");
    expect(subFolder.id).toBe("1");
    expect(subFolder.isFilingAllowed).toBe("isFilingAllowed");
    expect(subFolder.lastModifiedBy).toBe("lastModifiedBy");
    expect(subFolder.objectStoreName).toBe("objectStoreName");
    expect(subFolder.objectType).toBe("objectType");
    expect(subFolder.parentPath).toBe("parentPath");
    expect(subFolder.path).toBe("path");
    expect(subFolder.symbolicName).toBe("symbolicName");
    expect(subFolder.type).toBe("type");
    expect(subFolder.Name).toBe("name");
    expect(subFolder.is_checked).toBe(false);
    expect(subFolder.visible).toBe(false);

  }));

});

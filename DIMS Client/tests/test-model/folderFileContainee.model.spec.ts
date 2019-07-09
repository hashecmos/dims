import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {FolderFiledIn} from '../.././app/model/folderFileContainee.model'

describe('FolderFileContainee', function() {
  let de: DebugElement;
  let attrs = "attr";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: FolderFiledIn }]
    })
      .compileComponents();
  }));

  it('FolderFileContainee Definition', inject([FolderFiledIn], (folderfile: FolderFiledIn) => {
    folderfile = new FolderFiledIn(attrs);
    expect(folderfile).toBeDefined();
  }));

  it('FolderFileContainee Definition', inject([FolderFiledIn], (folderfile: FolderFiledIn) => {
    folderfile = new FolderFiledIn(attrs);
    folderfile.createdBy = "john";
    folderfile.dateCreated = "13/12/2017";
    folderfile.folderPath = "path";
    folderfile.id = "1";
    folderfile.path = "path";
    folderfile.symbolicName = "symbolicName";
    folderfile.type = "type";
    expect(folderfile.createdBy).toBe("john");
    expect(folderfile.dateCreated).toBe("13/12/2017");
    expect(folderfile.folderPath).toBe("path");
    expect(folderfile.id).toBe("1");
    expect(folderfile.path).toBe("path");
    expect(folderfile.symbolicName).toBe("symbolicName");
    expect(folderfile.type).toBe("type");
  }));
});

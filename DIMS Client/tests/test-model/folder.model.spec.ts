import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {FolderTree} from '../.././app/model/folder.model'
import {Node} from '../.././app/model/node.model'

describe('FolderTree', function() {
  let de: DebugElement;


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: FolderTree }]
    })
      .compileComponents();
  }));

  it('folder service definition', inject([FolderTree], (folder: FolderTree) => {
    folder = new FolderTree();
    expect(folder).toBeDefined();
  }));

  it('folder functions definition', inject([FolderTree], (folder: FolderTree) => {
    folder = new FolderTree();
    console.log("folderrrr")
    expect(folder.getFolderName).toBeDefined();
  }));

  it('folder service attributes definition', inject([FolderTree], (folder: FolderTree) => {
    folder = new FolderTree();
    folder.type = "type";
    folder.path = "path";
    folder.id = "1";
    folder.name = "fname";
    folder.is_checked = true;
    expect(folder.type).toBe("type");
    expect(folder.path).toBe("path");
    expect(folder.id).toBe("1");
    expect(folder.name).toBe("fname");
    expect(folder.is_checked).toBe(true);
  }));
});

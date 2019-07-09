import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {Containees} from '../.././app/model/containees.model'
import {DocumentInFolder} from '../.././app/model/docFolderContainee.model'
import {SubFolder} from '../.././app/model/subFolderContainee.model'

describe('Containees', function() {
  let de: DebugElement;
  let data = "xyz";
  let attrs = "attr";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: Containees }]
    })
      .compileComponents();
  }));

  it('Containees Definition', inject([Containees], (contain: Containees) => {
    contain = new Containees();
    expect(contain).toBeDefined();
  }));

  it('Containees attributes Definition', inject([Containees], (contain: Containees) => {
    contain = new Containees();
    expect(contain).toBeDefined();
  }));

  it('Containees attributes Definition', inject([Containees], (contain: Containees) => {
    contain = new Containees();
    let doc = new DocumentInFolder(data);
    let sub = new SubFolder(attrs);
    contain.documents = [doc];
    contain.folders = [sub];

  }));

});

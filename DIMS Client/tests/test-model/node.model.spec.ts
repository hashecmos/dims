import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {Node} from '../.././app/model/node.model'

describe('Node', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: Node }]
    })
      .compileComponents();
  }));

  it('Node Definition', inject([Node], (node: Node) => {
    node = new Node();
    expect(node).toBeDefined();
  }));


  it('Node Definition', inject([Node], (node: Node) => {
    node = new Node();
    node.id = "1";
    node.name = "nodename";
    node.path = "nodepath";
    node.is_checked = true;
    node.children = "children"
    expect(node.id).toBe("1");
    expect(node.name).toBe("nodename");
    expect(node.is_checked).toBe(true);
    expect(node.children).toBe("children");
  }));


});

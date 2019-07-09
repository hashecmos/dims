import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {WorkItemProperty} from '../.././app/model/workItemProperty.model'

describe('WorkItemProperty', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: WorkItemProperty }]
    })
      .compileComponents();
  }));

  it('WorkItemProperty Definition', inject([WorkItemProperty], (workitemproperty: WorkItemProperty) => {
    workitemproperty = new WorkItemProperty();
    expect(workitemproperty).toBeDefined();
  }));

  it('WorkItemProperty attributes Definition', inject([WorkItemProperty], (workitemproperty: WorkItemProperty) => {
    workitemproperty = new WorkItemProperty();
    workitemproperty.propertyName = "property";
    workitemproperty.propertyValue = "propertyvalue"
    expect(workitemproperty.propertyName).toBe("property");
    expect(workitemproperty.propertyValue).toBe("propertyvalue");
  }));
});

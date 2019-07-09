import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {WorkflowRecipientList} from '../.././app/model/workflowRecipientList.model'

describe('WorkflowRecipientList', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: WorkflowRecipientList }]
    })
      .compileComponents();
  }));

  it('workflowrecipientList Definition', inject([WorkflowRecipientList], (workflowrecipientList: WorkflowRecipientList) => {
    workflowrecipientList = new WorkflowRecipientList();
    expect(workflowrecipientList).toBeDefined();
  }));

  it('workflowrecipientList attributes Definition', inject([WorkflowRecipientList], (workflowrecipientList: WorkflowRecipientList) => {
    workflowrecipientList = new WorkflowRecipientList();
    workflowrecipientList.workflowRecipientName = "name";
    workflowrecipientList.workflowRecipient = "recipient";
    workflowrecipientList.workflowWorkItemType = "type";
    expect(workflowrecipientList.workflowRecipientName).toBe("name");
    expect(workflowrecipientList.workflowRecipient).toBe("recipient");
    expect(workflowrecipientList.workflowWorkItemType).toBe("type");
  }));
});

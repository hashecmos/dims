import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {TaskItems} from '../.././app/model/taskItems.model'

describe('TaskItems', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: TaskItems }]
    })
      .compileComponents();
  }));

  it('TaskItems Definition', inject([TaskItems], (taskItems: TaskItems) => {
    taskItems = new TaskItems();
    expect(taskItems).toBeDefined();
  }));

  it('TaskItems attributes Definition', inject([TaskItems], (taskItems: TaskItems) => {
    taskItems = new TaskItems();
    taskItems.workflowWorkItemID = "1";
    taskItems.workflowStepNo = 2;
    taskItems.workflowSender = "workflowSender";
    taskItems.workflowItemReceivedOn = 1;
    taskItems.workflowItemDeadline = "workflowItemDeadline";
    taskItems.workflowItemStatus = "workflowItemStatus";
    taskItems.workflowItemSenderDepartment = 4;
    taskItems.workflowItemStatus = "workflowItemStatus";
    taskItems.workflowItemReceiverDepartment = 2;
    taskItems.workflowItemSenderDiv = 3;
    taskItems.workflowItemReceiverDiv = 4;
    taskItems.workflowItemSubject = "workflowItemSubject";
    taskItems.workflowSenderName = "workflowSenderName";
    taskItems.workflowWorkItemType = "workflowWorkItemType";
    taskItems.workflowItemPriority = "workflowItemPriority";
    taskItems.is_checked = true;
    expect(taskItems.workflowWorkItemID).toBe("1");
    expect(taskItems.workflowStepNo).toBe(2);
    expect(taskItems.workflowSender).toBe("workflowSender");
    expect(taskItems.workflowItemReceivedOn).toBe(1);
    expect(taskItems.workflowItemDeadline).toBe("workflowItemDeadline");
    expect(taskItems.workflowItemStatus).toBe("workflowItemStatus");
    expect(taskItems.workflowItemSenderDepartment).toBe(4);
    expect(taskItems.workflowItemReceiverDepartment).toBe(2);
    expect(taskItems.workflowItemSenderDiv).toBe(3);
    expect(taskItems.workflowItemReceiverDiv).toBe(4);
    expect(taskItems.workflowItemSubject).toBe("workflowItemSubject");
    expect(taskItems.workflowWorkItemType).toBe("workflowWorkItemType");
    expect(taskItems.workflowSenderName).toBe("workflowSenderName");
    expect(taskItems.workflowItemPriority).toBe("workflowItemPriority");
    expect(taskItems.is_checked).toBe(true);
  }));

});

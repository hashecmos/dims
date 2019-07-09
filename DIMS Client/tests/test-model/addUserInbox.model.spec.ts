import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {AddUserTask} from '../.././app/model/addUserInbox.model'

describe('AddUserTask', function() {
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AddUserTask }]
    })
      .compileComponents();
  }));

  it('AddUserTask Definition', inject([AddUserTask], (adduser: AddUserTask) => {
    adduser = new AddUserTask();
    expect(adduser).toBeDefined();
  }));

  it('AddUserTask attributes Definition', inject([AddUserTask], (adduser: AddUserTask) => {
    adduser = new AddUserTask();
    adduser.workflowItemActionComment = "workflowItemActionComment";
    adduser.workflowItemAction = "workflowItemAction";
    adduser.workflowItemActionBy = "workflowItemActionBy";
    adduser.workflowItemActionOnBehalf = "workflowItemActionOnBehalf";
    adduser.workflowSender = "workflowSender";
    expect(adduser.workflowItemActionComment).toBe("workflowItemActionComment");
    expect(adduser.workflowItemAction).toBe("workflowItemAction");
    expect(adduser.workflowItemActionBy).toBe("workflowItemActionBy");
    expect(adduser.workflowItemActionOnBehalf).toBe("workflowItemActionOnBehalf");
    expect(adduser.workflowSender).toBe("workflowSender");
  }));
});

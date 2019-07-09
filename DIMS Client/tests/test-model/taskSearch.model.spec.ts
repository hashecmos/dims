import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {TaskAdvanceSearch} from '../.././app/model/taskSearch.model'

describe('TaskAdvanceSearch', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: TaskAdvanceSearch }]
    })
      .compileComponents();
  }));

  it('TaskAdvanceSearch Definition', inject([TaskAdvanceSearch], (tasksearch: TaskAdvanceSearch) => {
    tasksearch = new TaskAdvanceSearch();
    expect(tasksearch).toBeDefined();
  }));

  it('TaskAdvanceSearch attributes Definition', inject([TaskAdvanceSearch], (tasksearch: TaskAdvanceSearch) => {
    tasksearch = new TaskAdvanceSearch();
    tasksearch.subject = "subject";
    tasksearch.status = "status";
    tasksearch.workFlowSearchType = "workFlowSearchType";
    tasksearch.loginUser = "amar";
    tasksearch.initiatedDate = "10/10/17";
    tasksearch.initiatedBy = "alex";
    expect(tasksearch.subject).toBe("subject");
    expect(tasksearch.status).toBe("status");
    expect(tasksearch.workFlowSearchType).toBe("workFlowSearchType");
    expect(tasksearch.loginUser).toBe("amar");
    expect(tasksearch.initiatedDate).toBe("10/10/17");
    expect(tasksearch.initiatedBy).toBe("alex");
  }));


});

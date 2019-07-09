import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {LaunchWorkflow} from '../.././app/model/launchWorkflow.model'

describe('LaunchWorkflow', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: LaunchWorkflow }]
    })
      .compileComponents();
  }));

  it('LaunchWorkflow Definition', inject([LaunchWorkflow], (launchwork: LaunchWorkflow) => {
    launchwork = new LaunchWorkflow();
    expect(launchwork).toBeDefined();
  }));

  it('LaunchWorkflow attributes Definition', inject([LaunchWorkflow], (launchwork: LaunchWorkflow) => {
    launchwork = new LaunchWorkflow();
    launchwork.subject = "subject";
    launchwork.name = "name";
    launchwork.to = "to";
    launchwork.cc = "cc";
    launchwork.comment = "comment";
    launchwork.deadline = "deadline";
    launchwork.instruction = "instruction";
    expect(launchwork.subject).toBe("subject");
    expect(launchwork.name).toBe("name");
    expect(launchwork.to).toBe("to");
    expect(launchwork.cc).toBe("cc");
    expect(launchwork.comment).toBe("comment");
    expect(launchwork.deadline).toBe("deadline");
    expect(launchwork.instruction).toBe("instruction");

  }));

});

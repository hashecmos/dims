import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {TaskSimpleSearch} from '../.././app/model/taskSimpleSearch.model'

describe('TaskSimpleSearch', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: TaskSimpleSearch }]
    })
      .compileComponents();
  }));

  it('TaskSimpleSearch Definition', inject([TaskSimpleSearch], (tasksimple: TaskSimpleSearch) => {
    tasksimple = new TaskSimpleSearch();
    expect(tasksimple).toBeDefined();
  }));

  it('TaskSimpleSearch attributes Definition', inject([TaskSimpleSearch], (tasksimple: TaskSimpleSearch) => {
    tasksimple = new TaskSimpleSearch();
    tasksimple.subject = "subject";
    tasksimple.loginUser = "amar";
    expect(tasksimple.subject).toBe("subject");
    expect(tasksimple.loginUser).toBe("amar");
  }));

});

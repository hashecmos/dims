import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {EmployeeDirectorate} from '../.././app/model/employeeDirectorate.model'

describe('EmployeeDirectorate', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: EmployeeDirectorate }]
    })
      .compileComponents();
  }));

  it('EmployeeDirectorate Definition', inject([EmployeeDirectorate], (empdir: EmployeeDirectorate) => {
    empdir = new EmployeeDirectorate();
    expect(empdir).toBeDefined();
  }));

  it('EmployeeDirectorate attributes Definition', inject([EmployeeDirectorate], (empdir: EmployeeDirectorate) => {
    empdir = new EmployeeDirectorate();
    empdir.employeeDirectorateCode = "123";
    empdir.EmployeeDirectorate = "EmployeeDirectorate";
    expect(empdir.employeeDirectorateCode).toBe("123");
    expect(empdir.EmployeeDirectorate).toBe("EmployeeDirectorate");
  }));



});

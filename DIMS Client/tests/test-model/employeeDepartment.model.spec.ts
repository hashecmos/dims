import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {EmployeeDepartment} from '../.././app/model/employeeDepartment.model'

describe('EmployeeDepartment', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: EmployeeDepartment }]
    })
      .compileComponents();
  }));

  it('EmployeeDepartment Definition', inject([EmployeeDepartment], (empdept: EmployeeDepartment) => {
    empdept = new EmployeeDepartment();
    expect(empdept).toBeDefined();
  }));

  it('EmployeeDepartment attributes Definition', inject([EmployeeDepartment], (empdept: EmployeeDepartment) => {
    empdept = new EmployeeDepartment();
    empdept.departmentCode = 1;
    empdept.department = "cse";
    expect(empdept.departmentCode).toBe(1);
    expect(empdept.department).toBe("cse");
  }));

});

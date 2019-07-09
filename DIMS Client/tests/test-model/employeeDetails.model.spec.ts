import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {EmployeeDetails} from '../.././app/model/employeeDetails.model'
import {EmployeeDivision} from '../.././app/model/employeeDivision.model'
import {EmployeeDirectorate} from '../.././app/model/employeeDirectorate.model'
import {EmployeeDepartment} from '../.././app/model/employeeDepartment.model'

describe('EmployeeDetails', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: EmployeeDetails }]
    })
      .compileComponents();
  }));

  it('EmployeeDetails Definition', inject([EmployeeDetails], (empdet: EmployeeDetails) => {
    empdet = new EmployeeDetails();
    expect(empdet).toBeDefined();
  }));

  it('EmployeeDetails attributes Definition', inject([EmployeeDetails], (empdet: EmployeeDetails) => {
    empdet = new EmployeeDetails();
    let empdir = new EmployeeDirectorate();
    let empdep = new EmployeeDepartment();
    let empdiv = new EmployeeDivision();
    empdet.employeeID = 1;
    empdet.employeeName = "employeeName";
    empdet.employeeLogin = "employeeLogin";
    empdet.admin = true;
    empdet.employeeDirectorate = empdir;
    empdet.employeeDepartment = empdep;
    empdet.employeeDivision = empdiv;
    expect(empdet.employeeID).toBe(1);
    expect(empdet.employeeName).toBe("employeeName");
    expect(empdet.employeeLogin).toBe("employeeLogin");
    expect(empdet.admin).toBe(true);
    expect(empdet.employeeDirectorate).toBe(empdir);
    expect(empdet.employeeDepartment).toBe(empdep);
    expect(empdet.employeeDivision).toBe(empdiv);
  }));

});

import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {EmployeeDivision} from '../.././app/model/employeeDivision.model'

describe('EmployeeDivision', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: EmployeeDivision }]
    })
      .compileComponents();
  }));


  it('EmployeeDivision Definition', inject([EmployeeDivision], (empdiv: EmployeeDivision) => {
    empdiv = new EmployeeDivision();
    expect(empdiv).toBeDefined();
  }));

  it('EmployeeDivision attributes Definition', inject([EmployeeDivision], (empdiv: EmployeeDivision) => {
    empdiv = new EmployeeDivision();
    empdiv.empDivisionCode = 1;
    empdiv.empDivision = "empdiv";
    expect(empdiv.empDivisionCode).toBe(1);
    expect(empdiv.empDivision).toBe("empdiv");
  }));

});

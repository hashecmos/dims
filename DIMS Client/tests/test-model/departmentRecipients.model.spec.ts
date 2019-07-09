import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {Department} from '../.././app/model/departmentRecipients.model'

describe('Department', function() {
  let data = "xyz";
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: Department }]
    })
      .compileComponents();
  }));

  it('Department Definition', inject([Department], (contain: Department) => {
    contain = new Department(data);
    expect(contain).toBeDefined();
  }));

  it('Department attributes Definition', inject([Department], (contain: Department) => {
    contain = new Department(data);
    contain.departmentCode = 1;
    contain.department = "CSE";
    contain.users = "antony";
    contain.divisions = "divisions";
    expect(contain.departmentCode).toBe(1);
    expect(contain.department).toBe("CSE");
    expect(contain.users).toBe("antony");
    expect(contain.divisions).toBe("divisions");
  }));
});

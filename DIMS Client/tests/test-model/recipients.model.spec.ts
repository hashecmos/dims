import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {Directorate} from '../.././app/model/recipients.model'

describe('Directorate', function() {
  let de: DebugElement;
  let data = "data";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: Directorate }]
    })
      .compileComponents();
  }));


  it('directorate Definition', inject([Directorate], (directorate: Directorate) => {
    directorate = new Directorate(data);
    expect(directorate).toBeDefined();
  }));

  it('directorate attributes Definition', inject([Directorate], (directorate: Directorate) => {
    directorate = new Directorate(data);
    directorate.employeeDirectorateCode = "1";
    directorate.employeeDirectorate = "employeeDirectorate";
    directorate.users = "alex";
    directorate.departments = "computer science";
    expect(directorate.employeeDirectorateCode).toBe("1");
    expect(directorate.employeeDirectorate).toBe("employeeDirectorate");
    expect(directorate.users).toBe("alex");
    expect(directorate.departments).toBe("computer science");
  }));


});

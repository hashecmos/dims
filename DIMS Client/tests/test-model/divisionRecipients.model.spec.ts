import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {Division} from '../.././app/model/divisionRecipients.model'

describe('Division', function() {
  let data = "xyz";
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: Division }]
    })
      .compileComponents();
  }));

  it('Division Definition', inject([Division], (division: Division) => {
    division = new Division(data);
    expect(division).toBeDefined();
  }));

  it('Division attributes Definition', inject([Division], (division: Division) => {
    division = new Division(data);
    division.divisionCode = 1;
    division.division = "division";
    division.users = "alex";
    expect(division.divisionCode).toBe(1);
    expect(division.division).toBe("division");
    expect(division.users).toBe("alex");
  }));

});

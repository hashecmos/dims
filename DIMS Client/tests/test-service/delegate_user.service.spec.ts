import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {DelegateUserService} from '../.././app/services/delegate_user.service'

describe('DelegateUserService', function() {
  let de: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DelegateUserService }]
    })
      .compileComponents();
  }));

  it('DelegateUserService Definition', inject([DelegateUserService], (delegateservice: DelegateUserService) => {
    delegateservice = new DelegateUserService();
    expect(delegateservice).toBeDefined();
  }));

});

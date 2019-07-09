import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import {UserService} from '../.././app/services/user.service'


describe('userservice', function() {
  let de: DebugElement;
  let http: Http = null;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: UserService }]
    })
      .compileComponents();
  }));

  it('userservice Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice).toBeDefined();
  }));


  it('userservice getCurrentUser Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.getCurrentUser).toBeDefined();
  }));

  it('userservice getCurrentDelegatedForEmployeeLogin Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.getCurrentDelegatedForEmployeeLogin).toBeDefined();
  }));

  it('userservice getEmployeeDetails Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.getEmployeeDetails).toBeDefined();
  }));

  it('userservice getSupportStaff Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.getSupportStaff).toBeDefined();
  }));

  it('userservice delegateForUser Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.delegateForUser).toBeDefined();
  }));

  it('userservice unDelegateForUser Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.unDelegateForUser).toBeDefined();
  }));

  it('userservice getCurrentDelegatedUserOrCurrentUserLogin Definition', inject([UserService], (userservice: UserService) => {
    userservice = new UserService(http);
    expect(userservice.getCurrentDelegatedUserOrCurrentUserLogin).toBeDefined();
  }));

});

import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import {UserService} from '../.././app/services/user.service';
import {SettingsDelegateUserService} from '../.././app/services/settingsDelegateUser.service';
import global = require('./../../app/global.variables')
import { Subject } from 'rxjs/Subject';


describe('SettingsDelegateUserService', function() {
  let de: DebugElement;
  let http: Http = null;
  let backend: MockBackend = null;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: SettingsDelegateUserService }, { provide: Http }]
    })
      .compileComponents();
  }));

  it('SettingsDelegateUserService getDelegateUsers function defnition', inject([SettingsDelegateUserService], (settings: SettingsDelegateUserService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/getDelegatedUsers?user_login=alex');
    });
  }));




  it('SettingsDelegateUserService addDelegateUser function defnition', inject([SettingsDelegateUserService], (settings: SettingsDelegateUserService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Post);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/addDelegateUsers');
    });
  }));

  it('SettingsDelegateUserService deleteDelegateUser function defnition', inject([SettingsDelegateUserService], (settings: SettingsDelegateUserService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/deleteDelegateUsers?delegationId=1');
    });
  }));


  it('SettingsDelegateUserService modifyDelegateUser function defnition', inject([SettingsDelegateUserService], (settings: SettingsDelegateUserService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Post);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/modifyDelegateUsers');
    });
  }));





});

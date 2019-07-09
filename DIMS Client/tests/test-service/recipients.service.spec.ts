import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import {RecipientsService} from '../.././app/services/recipients.service';
import global = require('./../../app/global.variables')
import { Subject } from 'rxjs/Subject';
import {Directorate} from '../.././app/model/recipients.model'

describe('LaunchService', function() {
  let de: DebugElement;
  let http: Http = null;
  let backend: MockBackend = null;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: RecipientsService }, { provide: Http }]
    })
      .compileComponents();
  }));

  it('recipientservice Definition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    recipientservice = new RecipientsService(http);
    expect(recipientservice).toBeDefined();
  }));

  it('getDirectorates function defnition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/getDirectorate');
    });
  }));

  it('getUsersForDirectorate function defnition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/getDirectorateUsers?dir_code=1');
    });
  }));


  it('getDepartmentsForDirectorate function defnition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/getDepartments?dir_code=1');
    });
  }));

  it('getUsersForDepartment function defnition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/getDepartmentUsers?dept_code=1');
    });
  }));

  it('getUsersForDivision function defnition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    backend = new MockBackend;
    backend.connections.subscribe((connection: MockConnection) => {
      expect(connection.request.method).toBe(RequestMethod.Get);
      expect(connection.request.url).toBe(
        'http://10.10.2.219:9080/DIMS/resources/EmployeeService/getDivisionUsers?division_code=1');
    });
  }));

  it('recipientservice parseDirectorates Definition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    recipientservice = new RecipientsService(http);
    expect(recipientservice.parseDirectorates).toBeDefined();
  }));

  it('recipientservice addUsersToSection Definition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    recipientservice = new RecipientsService(http);
    expect(recipientservice.addUsersToSection).toBeDefined();
  }));

  it('recipientservice addDepartmentsToDirectorate Definition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    recipientservice = new RecipientsService(http);
    expect(recipientservice.addDepartmentsToDirectorate).toBeDefined();
  }));


  it('recipientservice addDivisionsToDepartment Definition', inject([RecipientsService], (recipientservice: RecipientsService) => {
    recipientservice = new RecipientsService(http);
    expect(recipientservice.addDivisionsToDepartment).toBeDefined();
  }));



});

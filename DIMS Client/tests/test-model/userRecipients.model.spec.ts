import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {User} from '../.././app/model/userRecipients.model'

describe('User', function() {
  let de: DebugElement;
  let data = "data";
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: User }]
    })
      .compileComponents();
  }));

  it('User Definition', inject([User], (user: User) => {
    user = new User(data);
    expect(user).toBeDefined();
  }));

  it('User attributes Definition', inject([User], (user: User) => {
    user = new User(data);
    user.admin = true;
    user.employeeID = 1;
    user.employeeName = "alex";
    user.employeeLogin = "alex123";
    user.employeeEmail = "alex@gmail.com";
    user.employeeDesignation = "asst";
    expect(user.admin).toBe(true);
    expect(user.employeeID).toBe(1);
    expect(user.employeeName).toBe("alex");
    expect(user.employeeLogin).toBe("alex123");
    expect(user.employeeEmail).toBe("alex@gmail.com");
    expect(user.employeeDesignation).toBe("asst");
  }));

});

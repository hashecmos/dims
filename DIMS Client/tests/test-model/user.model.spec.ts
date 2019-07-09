import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {User} from '../.././app/model/user.model'

describe('User', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: User }]
    })
      .compileComponents();
  }));

  it('User Definition', inject([User], (user: User) => {
    user = new User();
    expect(user).toBeDefined();
  }));

  it('User attributes', inject([User], (user: User) => {
    user = new User();
    user.username = "alex";
    user.password = "password";
    expect(user.username).toBe("alex");
    expect(user.password).toBe("password");
  }));


});

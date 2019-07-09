import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {UserList} from '../.././app/model/userList.model'

describe('User', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: UserList }]
    })
      .compileComponents();
  }));

  it('UserList Definition', inject([UserList], (userlist: UserList) => {
    userlist = new UserList();
    expect(userlist).toBeDefined();
  }));

  it('UserList Definition', inject([UserList], (userlist: UserList) => {
    userlist = new UserList();
    userlist.listId = "1";
    userlist.listName = "listname";
    userlist.loginUser = "amar";
    userlist.listType = "listtype";
    userlist.listMember = "listmember";
    expect(userlist.listId).toBe("1");
    expect(userlist.listName).toBe("listname");
    expect(userlist.loginUser).toBe("amar");
    expect(userlist.listType).toBe("listtype");
    expect(userlist.listMember).toBe("listmember");
  }));
});

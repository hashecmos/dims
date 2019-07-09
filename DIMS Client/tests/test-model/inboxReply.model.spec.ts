import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {InboxReply} from '../.././app/model/inboxReply'

describe('InboxReply', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: InboxReply }]
    })
      .compileComponents();
  }));

  it('InboxReply Definition', inject([InboxReply], (inboxreply: InboxReply) => {
    inboxreply = new InboxReply();
    expect(inboxreply).toBeDefined();
  }));

  it('InboxReply attributes Definition', inject([InboxReply], (inboxreply: InboxReply) => {
    inboxreply = new InboxReply();
    inboxreply.doc_title = "doc_title";
    inboxreply.subject = "subject";
    inboxreply.name = "name";
    inboxreply.deadline = "deadline";
    inboxreply.to = "to";
    inboxreply.cc = "cc";
    inboxreply.reply_type = "reply_type";
    inboxreply.message = "message";
    inboxreply.taskItem_id = "1";
    expect(inboxreply.doc_title).toBe("doc_title");
    expect(inboxreply.subject).toBe("subject");
    expect(inboxreply.name).toBe("name");
    expect(inboxreply.deadline).toBe("deadline");
    expect(inboxreply.to).toBe("to");
    expect(inboxreply.cc).toBe("cc");
    expect(inboxreply.reply_type).toBe("reply_type");
    expect(inboxreply.message).toBe("message");
    expect(inboxreply.taskItem_id).toBe("1");

  }));

});

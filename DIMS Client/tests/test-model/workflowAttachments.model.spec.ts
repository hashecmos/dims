import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {WorkflowAttachments} from '../.././app/model/workflowAttachments.model'

describe('WorkflowAttachments', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: WorkflowAttachments }]
    })
      .compileComponents();
  }));

  it('WorkflowAttachments Definition', inject([WorkflowAttachments], (workflowattachments: WorkflowAttachments) => {
    workflowattachments = new WorkflowAttachments();
    expect(workflowattachments).toBeDefined();
  }));

  it('WorkflowAttachments attributes Definition', inject([WorkflowAttachments], (workflowattachments: WorkflowAttachments) => {
    workflowattachments = new WorkflowAttachments();
    workflowattachments.workflowDocumentId = "1";
    workflowattachments.workflowAttachmentType = "zip";
    workflowattachments.fileName = "fileName";
    workflowattachments.mimeType = "mimeType";
    expect(workflowattachments.workflowDocumentId).toBe("1");
    expect(workflowattachments.workflowAttachmentType).toBe("zip");
    expect(workflowattachments.fileName).toBe("fileName");
    expect(workflowattachments.mimeType).toBe("mimeType");
  }));
});

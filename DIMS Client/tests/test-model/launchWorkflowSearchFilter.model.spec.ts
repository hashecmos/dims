import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import {LaunchWorkflowSearchFilter} from '../.././app/model/launchWorkflowSearchFilter.model'

describe('LaunchWorkflowSearchFilter', function() {
  let de: DebugElement;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: LaunchWorkflowSearchFilter }]
    })
      .compileComponents();
  }));

  it('LaunchWorkflowSearchFilter Definition', inject([LaunchWorkflowSearchFilter], (launchworkflow: LaunchWorkflowSearchFilter) => {
    launchworkflow = new LaunchWorkflowSearchFilter();
    expect(launchworkflow).toBeDefined();
  }));


  it('LaunchWorkflowSearchFilter attributes Definition', inject([LaunchWorkflowSearchFilter], (launchworkflow: LaunchWorkflowSearchFilter) => {
    launchworkflow = new LaunchWorkflowSearchFilter();
    launchworkflow.filterName = "alex";
    launchworkflow.filterValue = "filterValue";
    launchworkflow.filterCondition = "filterCondition";
    launchworkflow.filterDataType = "filterDataType";
    expect(launchworkflow.filterName).toBe("alex");
    expect(launchworkflow.filterValue).toBe("filterValue");
    expect(launchworkflow.filterCondition).toBe("filterCondition");
    expect(launchworkflow.filterDataType).toBe("filterDataType");



  }));



});

import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import {} from 'jasmine';
import { Http, BaseRequestOptions, Response, ResponseOptions, RequestMethod } from '@angular/http';
import { MockBackend, MockConnection } from '@angular/http/testing';
import {LaunchService} from '../.././app/services/launch.service'
import { Subject } from 'rxjs/Subject';

describe('LaunchService', function() {
  let de: DebugElement;
  let http: Http = null;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: LaunchService }, { provide: Http }]
    })
      .compileComponents();
  }));

  it('LaunchService Definition', inject([LaunchService], (launchservice: LaunchService) => {
    launchservice = new LaunchService(http);
    expect(launchservice).toBeDefined();
  }));

  it('LaunchService getLaunchClasses Definition', inject([LaunchService], (launchservice: LaunchService) => {
    launchservice = new LaunchService(http);
    expect(launchservice.getLaunchClasses).toBeDefined();
  }));

  it('LaunchService advSearchClass Definition', inject([LaunchService], (launchservice: LaunchService) => {
    launchservice = new LaunchService(http);
    expect(launchservice.advSearchClass).toBeDefined();
  }));

  it('LaunchService advSearchTitle Definition', inject([LaunchService], (launchservice: LaunchService) => {
    launchservice = new LaunchService(http);
    expect(launchservice.advSearchTitle).toBeDefined();
  }));


  it('LaunchService advSearchID Definition', inject([LaunchService], (launchservice: LaunchService) => {
    launchservice = new LaunchService(http);
    expect(launchservice.advSearchID).toBeDefined();
  }));

  it('LaunchService advSearchDate Definition', inject([LaunchService], (launchservice: LaunchService) => {
    launchservice = new LaunchService(http);
    expect(launchservice.advSearchDate).toBeDefined();
  }));

});

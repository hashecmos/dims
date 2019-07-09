import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import {UserService} from './user.service'


import global = require('./../global.variables')

@Injectable()
export class SearchService {
  base_url: string;
  constructor(private http: Http, private userService: UserService) {
    this.base_url = global.base_url;
  }

  getSearchTaskItems(formdata) {
    let headers: Headers = new Headers();
    headers.append('Content-Type', undefined);
    return this.http.post(`${this.base_url}/WorkflowSearchService/searchWorkFlow?random=${new Date().getTime()}`, formdata).map(res => res.json());
  }
  getSearchLevels() {
    return [
      { num: 0, name: "Active" },
      { num: 1, name: "Archive" }
    ];
  }
  getSearchClasses() {
    return [
      { num: 0, name: "Inbox" },
      { num: 1, name: "Sent" },
      { num: 2, name: "Archive" }
    ];
  }

  getSimpleSearchTaskItem(simpleSearch: any, searchFilter: any) {
    if (searchFilter == "simple") {
      return this.http.get(`${this.base_url}/WorkflowSearchService/quickSearchWorkFlow?subject=${simpleSearch.subject}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
    } else {
      return this.http.get(`${this.base_url}/WorkflowSearchService/quickSearchWorkFlow?${searchFilter}=${simpleSearch.subject}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
    }
  }
}

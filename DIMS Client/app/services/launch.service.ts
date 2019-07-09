import { Injectable } from '@angular/core';
import { Http, Response} from '@angular/http';
import global = require('./../global.variables')

@Injectable()
export class LaunchService {
  private base_url: String;
  private os_name: String;
  constructor(private http: Http) {
    this.base_url = global.base_url;
    this.os_name = global.os_name;
  }

  getLaunchClasses() {
    return this.http.get(`${this.base_url}/FilenetService/getDocumentClasses?osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  advSearchClass() {
    return [
      { num: 0, name: "Correspondence" },
      { num: 1, name: "Employee" },
      { num: 2, name: "Invoice" }
    ];
  }
  advSearchTitle() {
    return [
      { num: 0, name: "Contains" },
      { num: 1, name: "starts with" },
      { num: 2, name: "=" }
    ];
  }
  advSearchID() {
    return [
      { num: 0, name: "=" },
      { num: 1, name: ">" },
      { num: 2, name: "<" },
      { num: 3, name: ">=" },
      { num: 4, name: "<=" }
    ];
  }
  advSearchDate() {
    return [
      { num: 0, name: "Period" },
      { num: 1, name: "Between" },
      { num: 2, name: "=" },
      { num: 3, name: ">" },
      { num: 4, name: "<" },
      { num: 5, name: ">=" },
      { num: 6, name: "<=" }
    ];
  }
}

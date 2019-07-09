import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import {UserService} from './user.service'
import global = require('./../global.variables')

@Injectable()
export class SettingsDelegateUserService {
  private base_url: string;
  private current_user;
  private current_user_name: string;
  constructor(private http: Http, private userService: UserService) {
    this.base_url = global.base_url;
    if(this.userService.getCurrentUser()){
      this.current_user = this.userService.getCurrentUser();
      this.current_user_name = this.current_user.employeeLogin;
    }
    
  }

  getDelegateUsers() {
    return this.http.get(`${this.base_url}/EmployeeService/getDelegatedUsers?user_login=${this.current_user_name}&random=${new Date().getTime()}`).map(res => res.json());
  }
  
  getDelegateUserList() {
    return this.http.get(`${this.base_url}/EmployeeService/getDelegatedUserList?user_login=${this.current_user_name}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
  addDelegateUser(delegateUser: any) {
    return this.http.post(`${this.base_url}/EmployeeService/addDelegateUsers`, delegateUser).map(res => res);
  }

  deleteDelegateUser(delegationId: any) {
    return this.http.get(`${this.base_url}/EmployeeService/deleteDelegateUsers?delegationId=${delegationId}&random=${new Date().getTime()}`).map(res => res.json());
  }

  modifyDelegateUser(delegateUser: any) {
    return this.http.post(`${this.base_url}/EmployeeService/modifyDelegateUsers`, delegateUser).map(res => res);
  }
}

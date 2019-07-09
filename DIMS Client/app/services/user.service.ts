import { Injectable } from '@angular/core';
import { Http} from '@angular/http';
import { Output, EventEmitter } from '@angular/core';
import global = require('./../global.variables')

@Injectable()
export class UserService {
  private base_url: string
  private result: boolean;
  private json : any;
  
  @Output() notifyUserColoumnPreference: EventEmitter<any> = new EventEmitter();
  @Output() notifyDelegateForChange: EventEmitter<any> = new EventEmitter();

  constructor(private http: Http) {
    this.base_url = global.base_url
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem("CurrentUser"))
  }
  
  getDelegatedJobTitle(){
    return localStorage.getItem("delegatedJobTitle");
  }
  setDelegatedJobTitle( data ){
      localStorage.setItem("delegatedJobTitle",data)
  }

  
  getDelegatedisHaveAccessReports(){
    return localStorage.getItem("DelegatedisHaveAccessReports");
  }
  setDelegatedisHaveAccessReports( data ){
      localStorage.setItem("DelegatedisHaveAccessReports",data)
  }

  
  getCurrentUserPreference(){
     return JSON.parse(localStorage.getItem("CurrentUserPreference")) 
  }
  getCurrentDelegatedForEmployeeLogin() {
    return localStorage.getItem("delegateForEmployeeLogin") || "";
  }

  getEmployeeDetails(username,password): any {
    return this.http.get(`${this.base_url}/EmployeeService/getUserDetails?user_login=${username}&password=${password}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
 
//http://localhost:9080/DIMS/resources/EmployeeService/getColumnPreference?user_login=fatima
  getColumnPreferences(user_login : any){
    return this.http.get(`${this.base_url}/EmployeeService/getColumnPreference?user_login=${user_login}&random=${new Date().getTime()}`).map(res => res.json());
  }
  getFirstLevelSupportStaff() {
    let first_level_support_staff = [
      {name:"Husam Abu Daoud",phone_num:"87345"},
      {name:"Mohamed Nazir Karbouj",phone_num:"87325"},
      {name:"Samar Saad",phone_num:"87834"},
      {name:"Shiraz Malik",phone_num:"87343"},
      {name:"Syed Imran",phone_num:"87613"}
    ];
    return first_level_support_staff;
  }

  getSecondLevelSupportStaff() {
    let second_level_support_staff = [
      {name:"Taibah Sulaiman Ali Alasfoor",phone_num:"89364"}
    ];
    return second_level_support_staff;
  }

  getThirdLevelSupportStaff() {
    let third_level_support_staff = [
      {name:"Syed Ali Abbas Rizvi",phone_num:"89399"}
    ];
    return third_level_support_staff;
  }
    
  delegateForUser(employeeLogin: any) {
    localStorage.setItem("delegateForEmployeeLogin", employeeLogin);
    this.notifyDelegateForChange.next(employeeLogin);
  }

  unDelegateForUser() {
    localStorage.removeItem("delegateForEmployeeLogin");
    this.notifyDelegateForChange.next('');
  }

  getCurrentDelegatedUserOrCurrentUserLogin() {
    var loginToUse = this.getCurrentDelegatedForEmployeeLogin();
      
    if (loginToUse == "") {
      return loginToUse = this.getCurrentUser().employeeLogin;
    }else{
      this.json = JSON.parse(loginToUse);
      
      return this.json.userLogin;
    }
  }
    
  removeUserCookies(): any{
    return this.http.get(`${this.base_url}/EmployeeService/removeCookies?random=${new Date().getTime()}`).map(res => res.json());
  }
    
  getColumnPreference(user_login : any){
      return this.http.get(`${this.base_url}/EmployeeService/getColumnPreference?user_login=${this.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
   getDepartmentName(code: any) {
        return this.http.get(`${this.base_url}/EmployeeService/getDepartmentName?department_code=${code}`).map((res => res.text()));
    }
    
  isSecretary(){
       return this.http.get(`${this.base_url}/EmployeeService/isSecretary?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
  getSupervisorDetails(delegatedUser : any){
      return this.http.get(`${this.base_url}/EmployeeService/getSupervisorDetails?user_login=${delegatedUser}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
  getUserDetails(username): any {
    return this.http.get(`${this.base_url}/EmployeeService/getDelegatedUserDetails?user_login=${username}&random=${new Date().getTime()}`).map(res => res.json());
  }
}

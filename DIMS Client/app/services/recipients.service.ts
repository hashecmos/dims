import { Injectable, EventEmitter } from '@angular/core';
import { Http, Response} from '@angular/http';
import {Directorate} from '../model/recipients.model'
import {User} from '../model/userRecipients.model'
import {Department} from '../model/departmentRecipients.model'
import {Division} from '../model/divisionRecipients.model'
import {KNPCHierarchy} from '../model/KNPCHierarchyRecipients.model'
import global = require('./../global.variables')


@Injectable()
export class RecipientsService {
  tasks_items: any = [];
  base_url: string;
  directorates: any = [];
  division: any =[];
  department: any=[];
  knpcHierarchy: any =[];
  
  constructor(private http: Http) {
    this.base_url = global.base_url;
  }
  getDirectorates() {
    return this.http.get(`${this.base_url}/EmployeeService/getDirectorate?random=${new Date().getTime()}`).map(res => this.parseDirectorates(res.json()));
  }

  getUsersForDirectorate(directorate: Directorate){
    return this.http.get(`${this.base_url}/EmployeeService/getDirectorateUsers?dir_code=${directorate.employeeDirectorateCode}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(directorate, res.json()));
  }

  getDepartmentsForDirectorate(directorate: Directorate){
    return this.http.get(`${this.base_url}/EmployeeService/getDepartments?dir_code=${directorate.employeeDirectorateCode}&random=${new Date().getTime()}`).map(res => this.addDepartmentsToDirectorate(directorate, res.json()));
  }

  getUsersForDepartment(department: Department,searchCrtieria : string){
    //added by Ravi Boni 0n 13-12-2017
    //return this.http.get(`${this.base_url}/EmployeeService/getDepartmentUsers?dept_code=${department.departmentCode}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(department, res.json()));
    return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code=${department.departmentCode}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(department, res.json()));
  }

  //added by Ravi Boni 0n 14-12-2017
  getUsersForDepartment_1(department: Department,searchCrtieria : string){
    
    return this.http.get(`${this.base_url}/EmployeeService/getDepartmentUsers?dept_code=${department.departmentCode}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(department, res.json()));
    //return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code=${department.departmentCode}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(department, res.json()));
  }

  getDivisionsForDepartment(department: Department){
    return this.http.get(`${this.base_url}/EmployeeService/getDivisions?dept_code=${department.departmentCode}&random=${new Date().getTime()}`).map(res => this.addDivisionsToDepartment(department, res.json()));
  }
  
  getUsersForDivision(division: Division, userLogin: any,searchCrtieria : string){
    
    return this.http.get(`${this.base_url}/EmployeeService/getDivisionUsers?division_code=${division.divisionCode}&user_login=${userLogin}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(division, res.json()));
  }

  parseDirectorates(data: any){
    this.directorates = data.map(d => new Directorate(d));
    return this.directorates;
  }

  getDivision(divisionCode: any,userLogin: any){
     return this.http.get(`${this.base_url}/EmployeeService/getUserDivision?division_code=${divisionCode}&user_login=${userLogin}&random=${new Date().getTime()}`).map(res => this.parseDivision(res.json())); 
  }
    
  parseDivision(data : any){
    this.division = data.map(d => new Division(d));
    return this.division;
  }
    
  getUserDepartment(departmentCode: any,userLogin :any){
    return this.http.get(`${this.base_url}/EmployeeService/getUserDepartment?department_code=${departmentCode}&user_login=${userLogin}&random=${new Date().getTime()}`).map(res => this.parseDepartment(res.json()));
  }
    
  loadKNPCHierarchy(userLogin :any){
      return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchy?user_login=${userLogin}&random=${new Date().getTime()}`).map(res => this.parseKNPCHierarchy(res.json()));
  }
  parseDepartment(data : any){
    this.department = data.map(d => new Department(d));
    return this.department;
  }
  addUsersToSection(section: any, data: any){
    console.log('Data1   :', data);
    var users = data.map(d => new User(d));
    section.users = users;
    console.log('Users1   :', users);
    return users;
  }

  //added by Ravi Boni
  addUsersToSection_1(data: any){
    console.log('data   :', data);
    var users = data.map(d => new User(d));
    //section.users = users;
    console.log('Users   :', users);
    return users;
  }
  //end

  addDepartmentsToDirectorate(directorate: Directorate, data: any){
    var departments = data.map(d => new Department(d));
    directorate.departments = departments;
    return departments;
  }

  addDivisionsToDepartment(department: Department, data: any){
    var divisions = data.map(d => new Division(d));
    department.divisions = divisions;
    return divisions;
  }
  
  parseKNPCHierarchy(data :any){
      this.knpcHierarchy = data.map(d => new KNPCHierarchy(d));
      return this.knpcHierarchy;
  }
    
  getCrossDepartmentUsers(searchCrtieria: any){
      return this.http.get(`${this.base_url}/EmployeeService/getCrossDepartmentUsers?searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.userList(res.json()));
  }
  
  loadDeafultUserList(searchCrtieria: any,user_login:any,list :any){
      return this.http.get(`${this.base_url}/EmployeeService/loadDeafultUserList?searchCrtieria=${searchCrtieria}&listId=${list.listId}&random=${new Date().getTime()}`).map(res => this.userList(res.json()));
  }

  getUserList(userLogin :any){
      return this.http.get(`${this.base_url}/EmployeeService/getUserList?user_name=${this.getUserOrDelegate(userLogin)}&type=private&random=${new Date().getTime()}`).map(res => this.userList(res.json()));
  }
  userList(data :any){
      return data;
  }
    
  getKNPCHierarchyUsersForDepartment(department: Department,searchCrtieria : string){
      return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code=${department.departmentCode}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.addUsersToSection(department, res.json()));
   }

  getKNPCHierarchyUsersForDepartment_1(searchCrtieria : string){
    return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchyUsersForDepartment_1?searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res => this.parseKNPCHierarchy(res.json()));
    //return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchy?user_login=${userLogin}&random=${new Date().getTime()}`).map(res => this.parseKNPCHierarchy(res.json()));
  }

  getKNPCHierarchyUsersForGlobalDepartment(dep_codes:any,  globalsearchCrtieria : string){
    return this.http.get(`${this.base_url}/EmployeeService/getDepartmentUsersForGlobal?dept_code=${dep_codes}&searchCrtieria=${globalsearchCrtieria}&random=${new Date().getTime()}`).map(res =>  res.json());
    //return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchyUsersForDepartment_1?searchCrtieria=${globalsearchCrtieria}&dep_codes=${dep_codes}&random=${new Date().getTime()}`).map(res => this.parseKNPCHierarchy(res.json()));
    //return this.http.get(`${this.base_url}/EmployeeService/getKNPCHierarchy?user_login=${userLogin}&random=${new Date().getTime()}`).map(res => this.parseKNPCHierarchy(res.json()));
  }
  
    
  getDivisionsDetailForDepartment(department : any){
        return this.http.get(`${this.base_url}/EmployeeService/getDivisions?dept_code=${department}&random=${new Date().getTime()}`).map(res => res.json());
  }
  
  getDocumentType(){
      return this.http.get(`${this.base_url}/EmployeeService/getDocumentType?random=${new Date().getTime()}`).map(res => res.json());
  }
    
  getUsersForDepartment1(department: any,searchCrtieria : string){
    return this.http.get(`${this.base_url}/EmployeeService/getDepartmentUsers?dept_code=${department}&searchCrtieria=${searchCrtieria}&random=${new Date().getTime()}`).map(res =>  res.json());
  }
    
  getUsersForFilter(filter: any, deptCode:any){
    return this.http.get(`${this.base_url}/EmployeeService/getFilterUsers?filter=${filter}&deptCode=${deptCode}`).map(res =>  res.json());
  }
    
  getUsersForDivisionReports(deptCode: any, divisionCode: any){
    
    return this.http.get(`${this.base_url}/EmployeeService/getUsersForDivisionReports?division_code=${divisionCode}&dept_code=${deptCode}&random=${new Date().getTime()}`).map(res => res.json());
  }
  getUserOrDelegate(userLogin){
    
    if(localStorage.getItem("isDelegate") == "true"){
      return JSON.parse(JSON.parse(localStorage.getItem("selectedDelegateUser"))).userLogin;
    }else{
      return userLogin;
    }
  }
}

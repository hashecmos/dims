import { Injectable } from '@angular/core';
import { Http, Headers, Response} from '@angular/http';
import { ActivatedRoute } from '@angular/router'
import { EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import {UserService} from './../user.service'

import global = require('./../../global.variables')

@Injectable()
export class WorkflowService {
  current_user_name: string = "";
  current_user: any;
  base_url: string;
  os_name : string;
  private snapshot : any; 
  private url : string;  
  formData: FormData;
  public placeHolder: any = "Subject";
  public simpleSearchFilter: any = "EmailSubject";
  private delegateForEmployeeLogin : any;
    
  constructor(private http: Http, private userService: UserService, private route: ActivatedRoute) {
    
    this.url = "getWorkItemDetails";
    this.snapshot = route.snapshot;
    this.base_url = global.base_url;
    this.os_name = global.os_name;
    if(this.userService.getCurrentUser()){
      this.current_user = this.userService.getCurrentUser();
      this.current_user_name = this.current_user.employeeLogin;
    }
    
  }

  //workflow side Nav APIs

  getInboxList(): any {
    return this.http.get(`${this.base_url}/WorkflowService/getInboxItems?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
  }

  getSentItems(): any {
    return this.http.get(`${this.base_url}/WorkflowService/getSentItems?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
  }
  getSentItemsWorkItems(id: any): any { // AMZ changes
    return this.http.get(`${this.base_url}/WorkflowService/getWorkitemSentItems?id=${id}&random=${new Date().getTime()}`).map(res => res.json());
  } // End AMZ change

  // workflow filter APIs
  buildUrlWithParams(url, filters) {
      
    if(filters.flagFilter=="sort"){
         if (filters.primaryFilter) {
            url = url + `&sortOrder=${filters.primaryFilter}`
         }
         if (filters.folderFilter) {
            url = url + `&folderFilter=${filters.folderFilter}`
         }
         if (filters.sortColumn) {
            url = url + `&sortColumn=${filters.sortColumn}`
         }
         if (filters.filterType) { 
            url = url + `&filterType=${filters.filterType}`
         }
    }else{
         if (filters.primaryFilter) {
            url = url + `&filterType=${filters.primaryFilter}`
         }
         if (filters.folderFilter) {
            url = url + `&folderFilter=${filters.folderFilter}`
         }
    }
    return url
  }

  getFilterdInboxItems(filters, page, perPage): any {
    let requestUrl = `${this.base_url}/WorkflowService/filterInboxItems?page=${page}&perPage=${perPage}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`
    requestUrl = this.buildUrlWithParams(requestUrl, filters)
    return this.http.get(requestUrl).map(res => res.json());
  }

  getFilterdSentItems(filters, page, perPage): any {
    let requestUrl = `${this.base_url}/WorkflowService/filterSentItems?page=${page}&perPage=${perPage}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`
    requestUrl = this.buildUrlWithParams(requestUrl, filters)
    return this.http.get(requestUrl).map(res => res.json());
  }

  getArchiveItems(filters, page, perPage): any {
    let requestUrl = `${this.base_url}/WorkflowService/getArchiveItems?page=${page}&perPage=${perPage}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`
    requestUrl = this.buildUrlWithParams(requestUrl, filters)
    return this.http.get(requestUrl).map(res => res.json());
  }
    
  getDailyDocumentItems(filters, page, perPage,isSecretary): any {
    let requestUrl = `${this.base_url}/WorkflowService/getDailyDocumentItems?page=${page}&perPage=${perPage}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&isSecretary=${isSecretary}&random=${new Date().getTime()}`
    requestUrl = this.buildUrlWithParams(requestUrl, filters)
    return this.http.get(requestUrl).map(res => res.json());
  }

  //workflow details/selected Actions APIs

  archiveItem(archive_item_id: any): any {
    return this.http.get(`${this.base_url}/WorkflowService/archiveWorkItem?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&workItemID=${archive_item_id}&actionBy=${this.current_user_name}&random=${new Date().getTime()}`).map(res => res);
  }

  completeTask(complete_item_id: any) {
    return this.http.get(`${this.base_url}/WorkflowService/completeWorkItem?workItemId=${complete_item_id}&user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`);
  }

  forwardTaskItem(formData) {
    return this.http.post(`${this.base_url}/WorkflowService/forwardWorkItem`, formData).map(res => res)
  }

  addUserTaskItem(workItemDetails: any) {
    return this.http.post(`${this.base_url}/WorkflowService/addUserWorkItem`, { workItemDetails: workItemDetails }).map(res => res)
  }

  reAssignWorkItem(workItemDetails: any,selectedRecipient : any) {
    return this.http.post(`${this.base_url}/WorkflowService/reassignWorkItem`, { workItemDetails: workItemDetails , selectedRecipient : selectedRecipient }, { headers: new Headers({ 'Content-Type': 'application/json' }) }).map(res => res);
  }

  doneTaskRequest(formData) {
    return this.http.post(`${this.base_url}/WorkflowService/doneWorkItem`, formData)
  }

  //workflow detail APIs

   
    
    
  getTask(taskId: any) {
      
    if(this.snapshot.routeConfig.path == "inbox/task-details/:id"){
        return this.http.get(`${this.base_url}/WorkflowService/getAndReadWorkItemDetails?witem_id=${taskId}&user_login=${this.current_user_name}&random=${new Date().getTime()}`).map(res => res.json());
    }else{
        return this.http.get(`${this.base_url}/WorkflowService/getWorkItemDetails?witem_id=${taskId}&random=${new Date().getTime()}`).map(res => res.json());
    }
  }

  getItemsHistory(workitemId: string, workflowId: string) {
    let param;
    workitemId ? param = `?workItemId=${workitemId}` : param = `?workflowId=${workflowId}`
    return this.http.get(`${this.base_url}/WorkflowService/getWorkFlowHistory${param}&random=${new Date().getTime()}`).map(res => res.json());
  }


  getItemsHistoryByDept(workitemId: string, workflowId: string) {
    let param;
    workitemId ? param = `?workItemId=${workitemId}` : param = `?workflowId=${workflowId}`
    //if(this.current_user_name && this.current_user_name == '')
      this.current_user_name = this.userService.getCurrentUser().employeeLogin;
      console.log("this.current_user_name  GGGGGGGGGGG  ::: "+this.current_user_name);
    return this.http.get(`${this.base_url}/WorkflowService/getWorkFlowHistoryByDept${param}&user_login=${this.current_user_name}&random=${new Date().getTime()}`).map(res => res.json());
  } 


  getAccordionMenu() {
    return { heading_1: "Maintenance MAB", heading_2: "Maintenance SHU", heading_3: "Management Support", heading_4: "MOG", heading_5: "Operations MAA", heading_6: "Operations MAB" };
  }

  getLaunchInstructions() {
    return this.http.get(`${this.base_url}/WorkflowService/getWorkflowInstructions?random=${new Date().getTime()}`).map(res => res.json());
  }

  getWorkitemActions(witemId: string, queue: string, user: string) {
    return this.http.get(`${this.base_url}/WorkflowService/getWorkitemActions?witemid=${witemId}&queue=${queue}&userid=${user}&random=${new Date().getTime()}`).map(res => res.json());
  }
  
  //workflow document property popup APIs

  getDocumentProperties(documentId) {
    return this.http.get(`${this.base_url}/FilenetService/getObjectPropertyValues?docId=${documentId}&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  getAttachmentVersions(attachmentId) {
    return this.http.get(`${this.base_url}/FilenetService/getVersions?docId=${attachmentId}&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  getAttachmentHistory(attachmentId) {
    return this.http.get(`${this.base_url}/WorkflowService/getDocumentHistory?docId=${attachmentId}&random=${new Date().getTime()}`).map(res => res.json());
  }

  getAttachmentFolders(attachmentId) {
    return this.http.get(`${this.base_url}/FilenetService/getFoldersFileIn?docId=${attachmentId}&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  launchDocuments(docs: any, params: any, toList: any, ccList: any, attachmentFiles : any) {
    var launchParams = [];
    var formattedDeadline = params.deadline;
    if (formattedDeadline) {
      formattedDeadline = formattedDeadline.formatted;
    }
      var json = this.userService.getCurrentDelegatedForEmployeeLogin();
    if(json =="")
    {
        this.delegateForEmployeeLogin = "";
    }else{
        this.delegateForEmployeeLogin = JSON.parse(json);
    }
      
    for (let doc of docs) {
      var submissionValues: any = {
        "workflowName": params.name,
        "workflowSubject": encodeURIComponent(params.subject),
        "workflowDeadline": formattedDeadline,
        "workflowReminderDate": null,
        "workflowPriority": params.priority,
        "workflowLaunchedBy": this.current_user_name,
        "workflowLaunchedOnBehalf": this.userService.getCurrentDelegatedUserOrCurrentUserLogin(),
        "workflowBeginDate": null,
        "workflowEndDate": null,
        "workflowStatus": "Active",
        "workflowPrimaryDocument": doc.document_id,
        "workflowAttachment": {
          "workflowDocumentId": doc.document_id,
          "workflowAttachmentType": "Primary"
        },
        "workItemDetails": {
          "workflowSender": this.userService.getCurrentDelegatedUserOrCurrentUserLogin(),
          "workflowStepNo": 1,
          "workflowInstruction": params.instruction,
          "workflowItemActionComment": encodeURIComponent(params.comment),
          "workflowItemReceivedOn": null,
          "workflowItemDeadline": formattedDeadline,
          "workflowItemReminderDate": null,
          "workflowItemRootSender": this.userService.getCurrentDelegatedUserOrCurrentUserLogin(),
          "workflowItemStatus": "New",
          "workflowItemActionBy":this.current_user_name,
          "workflowItemSenderDepartment": this.current_user.employeeDepartment.departmentCode,
          "workflowItemReceiverDepartment": this.current_user.employeeDepartment.departmentCode,
          "workflowItemSenderDiv": this.current_user.employeeDivision.empDivisionCode,
          "workflowItemReceiverDiv": this.current_user.employeeDivision.empDivisionCode,
          "workflowRecipientList": []
        },
      };
      toList.map(u => submissionValues["workItemDetails"]["workflowRecipientList"].push({
        "workflowRecipient": u,
        "workflowWorkItemType": "to"
      }));
      ccList.map(u => submissionValues["workItemDetails"]["workflowRecipientList"].push({
        "workflowRecipient": u,
        "workflowWorkItemType": "cc"
      }));
      launchParams.push(submissionValues)
    }
    console.log("launchParams :: ",launchParams);
    this.formData = new FormData();
    this.formData.append('workflowJSONString', JSON.stringify(launchParams));
    /*this.formData.append('documentJSONString', JSON.stringify({
        properties: [
          { propertyName: "EmailSubject", propertyValue: docs[0].symbolicName }, //SK 23-May subject to Email Subject
          { propertyName: "DocumentTitle", propertyValue: docs[0].symbolicName }
        ]
      }))*/
    this.formData.append('DocumentClass', 'Correspondence');
    var i = 1;
      for (let attachedFile of attachmentFiles) {
        const name = encodeURIComponent(attachedFile.name);
       // attachedFile = new File([attachedFile], name, { type: attachedFile.type });
        this.formData.append('file' + i, attachedFile, name);
        i++;
      }
      console.log("FInal formData :: ",this.formData);
        return this.http.post(`${this.base_url}/WorkflowService/launchWorkFlow`, this.formData );
  }

  //workflow folders API

  getworkflowFolder(folder) {
   
    return this.http.get(`${this.base_url}/WorkflowService/getWorkflowFolder?type=${folder}&user_name=${this.getUserOrDelegate()}&random=${new Date().getTime()}`).map(res => res.json());
  }

  createWorkflowFolder(type, folderName) {
    
    return this.http.get(`${this.base_url}/WorkflowService/createWorkflowFolder?type=${type}&folderName=${folderName}&user_name=${this.getUserOrDelegate()}&random=${new Date().getTime()}`);
  }

  getWorkitemParentType(workitemId) {
    //return this.http.get(`${this.base_url}/WorkflowService/getWorkitemParentType?witem_id=${workitemId}&random=${new Date().getTime()}`).map(res => res.json());
     return this.http.get(`${this.base_url}/WorkflowService/getWorkitemParentType?witem_id=${workitemId}&random=${new Date().getTime()}`);
  }

  // User list APIs

  getUserlist() {
    return this.http.get(`${this.base_url}/EmployeeService/getUserList?user_name=${this.getUserOrDelegate()}&type=private&random=${new Date().getTime()}`).map(res => res.json());
  }

  getUsersOfList(listId) {
    return this.http.get(`${this.base_url}/EmployeeService/getUserListMembers?listId=${listId}&random=${new Date().getTime()}`).map(res => res.json());
  }

  createUserList(formdata) {
    formdata.loginUser =this.getUserOrDelegate();
    console.log(JSON.stringify(formdata));
    return this.http.post(`${this.base_url}/EmployeeService/createUserList`, JSON.stringify(formdata)).map(res => res.json());
  }

  modifyUserList(formdata) {
    
    formdata.loginUser = this.getUserOrDelegate();
    console.log(JSON.stringify(formdata));
    return this.http.post(`${this.base_url}/EmployeeService/modifyUserList`, JSON.stringify(formdata), { headers: new Headers({ 'Content-Type': 'application/json' }) });
  }

  modifyUserListPriority(formdata){
    return this.http.post(`${this.base_url}/EmployeeService/modifyUserListPriority`, JSON.stringify(formdata), { headers: new Headers({ 'Content-Type': 'application/json' }) });
  }

  deleteUserList(listId) {
    return this.http.get(`${this.base_url}/EmployeeService/deleteUserList?listId=${listId}&random=${new Date().getTime()}`);
  }

  getAdvanceSearchProperties() {
    return this.http.get(`${this.base_url}/FilenetService/getPropertyDefinitions?docClassName=Correspondence&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }
    

  //added on 23-01-2018 By Ravi
  getDelegateUsers1(userLogin) {
    return this.http.get(`${this.base_url}/EmployeeService/getDelegatedUsers?user_login=${userLogin}&random=${new Date().getTime()}`).map(res => res.json());
  }


  isSecretary1(delUserLogin11){
    if(delUserLogin11!=undefined) {
      return this.http.get(`${this.base_url}/EmployeeService/isSecretary?user_login=${delUserLogin11}&random=${new Date().getTime()}`).map(res => res.json());
    } else {
      return this.http.get(`${this.base_url}/EmployeeService/isSecretary?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
      
    }
   //return this.http.get(`${this.base_url}/EmployeeService/isSecretary?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
   
}

  isSecretary(){
   
       return this.http.get(`${this.base_url}/EmployeeService/isSecretary?user_login=${this.userService.getCurrentDelegatedUserOrCurrentUserLogin()}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
  deleteFolder(folderId){
      return this.http.get(`${this.base_url}/WorkflowService/deleteFolder?folderId=${folderId}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
  getSiteItems(dept_code:any, siteDesc:any){
     return this.http.get(`${this.base_url}/WorkflowService/getSiteItems?dept_code=${dept_code}&siteDesc=${siteDesc}&random=${new Date().getTime()}`).map(res => res.json());
  }
  updateSiteItems(siteId :any ,siteType : any ,siteDesc : any,departmentCode : any){
     return this.http.get(`${this.base_url}/WorkflowService/updateSiteItems?dept_code=${departmentCode}&siteId=${siteId}&siteType=${siteType}&siteDesc=${siteDesc}&random=${new Date().getTime()}`).map(res => res.json()); 
  }
  addSiteItems(siteType : any ,siteDesc : any ,departmentCode : any){
     return this.http.get(`${this.base_url}/WorkflowService/addSiteItems?dept_code=${departmentCode}&siteType=${siteType}&siteDesc=${siteDesc}&random=${new Date().getTime()}`).map(res => res.json());
  }
    
  getSentItemRecipients(witmId :any){
      return this.http.get(`${this.base_url}/WorkflowService/getSentItemRecipients?witmId=${witmId}&random=${new Date().getTime()}`).map(res => res.json());
      
  }
    
  addColumnPreference(columnPreference: any, user_login:any,preferenceFor:any){
     return this.http.post(`${this.base_url}/EmployeeService/addColumnPreference`,{coloumnPref : JSON.stringify(columnPreference),userLogin: this.userService.getCurrentDelegatedUserOrCurrentUserLogin(), prefFor :preferenceFor}, { headers: new Headers({ 'Content-Type': 'application/json' }) }).map(res => res.json()); 
  }
    
  updateDocumentProperties(propData: any){
    var headers = new Headers();
    headers.append("Content-Type", "application/json");
    return this.http.post(`${this.base_url}/FilenetService/updateDocumentProperties`,propData,{ headers: headers });
  }
    
    
    
  isDelegatedUserSecretary(loginUser){
       return this.http.get(`${this.base_url}/EmployeeService/isSecretary?user_login=${loginUser}&random=${new Date().getTime()}`).map(res => res.json());
  }
  getUserOrDelegate(){
    
    if(localStorage.getItem("isDelegate") == "true"){
      
      return JSON.parse(JSON.parse(localStorage.getItem("selectedDelegateUser"))).userLogin;
    }else{
      console.log(this.userService.getCurrentUser().employeeLogin);
      return this.userService.getCurrentUser().employeeLogin;
    }
  }
}

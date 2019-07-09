import {Component, Output, EventEmitter, AfterViewInit} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseService} from '../services/browse.service'
import {ReportsService} from '../services/reports.service'
import global = require('../global.variables')
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker,IMyInputFieldChanged} from 'mydatepicker';
import {ReportDocumentsScanned, DocumentsScannedProperties, DocumentsScannedPropertiesList} from '../model/documentsScanned.model'
import {WorkflowStatistics} from '../model/workFlowStatistics.model'
import {UserService} from '../services/user.service'
import {RecipientsService} from '../services/recipients.service'
import {SelectItem} from 'primeng/primeng';
import global = require('../global.variables')

@Component({
  selector: 'document-scanned',
  providers: [BrowseService, ReportsService,RecipientsService],
  templateUrl: 'app/ReportComponent/documentsScanned.component.html'
})

export class DocumentsScanned implements AfterViewInit {
  private workflowStatistics: WorkflowStatistics;
  private myDatePickerOptions: IMyOptions = global.date_picker_options_disable_Reports;
  private myDatePickerDisableOptions: IMyOptions = global.date_picker_disabled_Reports;
  private documentsScannedReport: ReportDocumentsScanned;
  private documentsScannedProperties: DocumentsScannedProperties;
  private documentsScannedPropertiesList: DocumentsScannedPropertiesList;
  private current_user: any;
  private divisions : any;
  private recepient: SelectItem[];
  private selectedrecepients: string[];
  private recipientList : any;
  private base_url : any;
  private showReport= false;
  private documentScanned:any;
  private divCode : any;
  private totalCount : any;
  private noResult = false;
  private tmpSelRecep : any[];
  private divCodeUI : any;
  private tempValue : any;
  private fromDate : any;
  private toDate : any;
  private docCount : any;
  private delegatedJobTitle : any;
  public delegatedUserDetails : any;
    
  constructor(private reportsService: ReportsService, private userService: UserService, private recipientsService: RecipientsService) {
    this.workflowStatistics = new WorkflowStatistics();
    this.documentsScannedReport = new ReportDocumentsScanned();
    this.documentsScannedProperties = new DocumentsScannedProperties();
    this.documentsScannedPropertiesList = new DocumentsScannedPropertiesList();
    this.recepient = [];
    this.base_url = global.base_url
    this.current_user = this.userService.getCurrentUser();
    this.delegatedJobTitle  = this.userService.getDelegatedJobTitle();
    console.log("current_user  ::: DDDDDDDDDDDDDDDDDDDDDDDDDDD   LLLLLLLLLLLLLLLLL  :: ",this.current_user);
    console.log("this.userService.getCurrentDelegatedUserOrCurrentUserLogin() :: ",this.userService.getCurrentDelegatedUserOrCurrentUserLogin())
    console.log("delegatedJobTitle ;; in out side :: "+this.delegatedJobTitle)
    
    this.delegatedUserDetails = '';
    /*if(this.delegatedJobTitle != null && this.delegatedJobTitle != undefined){
        if(this.delegatedJobTitle =='SEC' || this.delegatedJobTitle =='CRDEP'){
             this.userService.getSupervisorDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.getReports(data));
        }else{
            this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.getReports(data));
        }
    }else if(this.current_user.employeeJobTitle == 'CEO'){
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,"-1").subscribe(data => this.populateFilterUser(data));
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    } else if(this.current_user.employeeJobTitle == 'DCEO'){
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeDivision.empDivisionCode).subscribe(data => this.populateFilterUser(data));
    } else if(this.current_user.employeeJobTitle == 'MGR'){
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,"-1").subscribe(data => this.populateFilterUser(data));
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    } else {
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeDivision.empDivisionCode).subscribe(data => this.populateFilterUser(data));
    }*/

    if(this.delegatedJobTitle != null && this.delegatedJobTitle != undefined){
        console.log("delegatedJobTitle ;; in if :: "+this.delegatedJobTitle)
        if(this.delegatedJobTitle =='SEC' || this.delegatedJobTitle =='CRDEP'){ 
            console.log("in if");
             this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.getReports(data));
        }else{
            console.log("in else");
            this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.getReports(data));
        }
    } else if(this.current_user.employeeJobTitle == 'CEO'){
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,"-1").subscribe(data => this.populateFilterUser(data));
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    } else if(this.current_user.employeeJobTitle == 'DCEO'){
        let _ref = this
        setTimeout( function() {  
            let data:any =[]
                data.push(_ref.current_user.employeeDivision);
                _ref.populateDisvision(data);
            }, 200 );
            this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeDivision.empDivisionCode).subscribe(data => this.populateFilterUser(data));
    } else if(this.current_user.employeeJobTitle == 'MGR'){
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,"-1").subscribe(data => this.populateFilterUser(data));
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    } else{
        //this else block will be in case of TL, Sr eng, or below
        let _ref = this
        setTimeout( function() {  
            let data:any =[]
                data.push(_ref.current_user.employeeDivision);
                _ref.populateDisvision(data);
            }, 200 );
            this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeDivision.empDivisionCode).subscribe(data => this.populateFilterUser(data));
    }
  }
   
  getReports(data){ 
    this.current_user = data;
    console.log("Daily DOCS :::AFTER :: current_user ::  ",this.current_user);
    if(this.current_user.employeeJobTitle == 'CEO' || this.current_user.employeeJobTitle == 'MGR'){
        this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,"-1").subscribe(data => this.populateFilterUser(data));
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    }else {
        //this else block will be in case of TL, Sr eng, or below
        let _ref = this
        setTimeout( function() {  
            let data:any =[]
                data.push(_ref.current_user.employeeDivision);
                _ref.populateDisvision(data);
            }, 200 );
            this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeDivision.empDivisionCode).subscribe(data => this.populateFilterUser(data));
    }
    /*else if(data.employeeJobTitle == 'TL'){
        this.recipientsService.getUsersForDivisionReports(data.employeeDepartment.departmentCode,data.employeeDivision.empDivisionCode).subscribe(data => this.populateFilterUser(data));
    }*/
  }
    
  ngAfterViewInit(){

  }
    
  populateDisvision(data){
      this.divisions = data;
      document.getElementById("division").disabled = false;
      for(let division of this.divisions){
          document.getElementById("division").options[document.getElementById("division").options.length] = new Option(division.empDivision,division.empDivisionCode);
      }
  }
  getDeptUser(){
      this.divCode = document.getElementById("division").options[document.getElementById("division").options.selectedIndex].value;
      this.tempValue = document.getElementById("division").options[document.getElementById("division").options.selectedIndex].text;
       this.recipientsService.getUsersForDivisionReports(this.current_user.employeeDepartment.departmentCode,this.divCode).subscribe(data => this.populateFilterUser(data));
  }
  
    
  populateFilterUser(data: any ) {
      console.log("GOOOOOOOOOOOOOTTTTT  ::: ",data);
        this.recipientList =[];
        this.recepient = [];
        this.selectedrecepients = [];
        this.recipientList = data;
        for(let i=0;i<data.length;i++){
            this.recepient.push({label:this.recipientList[i].employeeName, value:this.recipientList[i].employeeLogin});
        }
      
  }
  
  getScannedDocumentReport(){
      document.getElementById("displayLoader").style.display="block";
      this.fromDate = '';
      this.toDate ='';
      this.fromDate = this.workflowStatistics.fromDate.formatted;
      this.toDate = this.workflowStatistics.toDate.formatted;
      
      this.tmpSelRecep =[];
      for(let recp of this.recepient){
          
          for(let selRecp of this.selectedrecepients){
              if(recp.value == selRecp){
                  this.tmpSelRecep.push({label:recp.label, value:recp.value, count: 0 });
              }
          }
          
      }
      this.workflowStatistics.department = document.getElementById("departmentId").options[document.getElementById("departmentId").options.selectedIndex].value
      this.workflowStatistics.division = document.getElementById("division").options[document.getElementById("division").options.selectedIndex].value
      if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
            this.reportsService.getdocumentsScanned(this.workflowStatistics.department,this.workflowStatistics.division,this.selectedrecepients,this.fromDate,this.toDate).subscribe(data => this.scannedDocument(data));
        }
        else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
            this.reportsService.getdocumentsScanned(this.workflowStatistics.department,this.workflowStatistics.division,this.selectedrecepients,this.fromDate,this.toDate).subscribe(data => this.scannedDocument(data));
        }
         else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
            this.reportsService.getdocumentsScanned(this.workflowStatistics.department,this.workflowStatistics.division,this.selectedrecepients,this.fromDate,this.toDate).subscribe(data => this.scannedDocument(data));
        }
         else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
            this.reportsService.getdocumentsScanned(this.workflowStatistics.department,this.workflowStatistics.division,this.selectedrecepients,this.fromDate,this.toDate).subscribe(data => this.scannedDocument(data));
        }
        
  }
    
  exportData() {
   
        var startExport = true;
        var selected = document.getElementById("options");
        var selectedOption = selected.options[selected.selectedIndex].text;
        //this.workflowStatistics.status = document.getElementById("itemStatus").options[document.getElementById("itemStatus").options.selectedIndex].text;
        if(selectedOption != 'PDF' && selectedOption != 'Excel') {
            startExport = false;
        }
        
        if(startExport) {
            if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
                window.location.assign(this.base_url+'/FilenetService/documentsScannedReport?dpt='+this.workflowStatistics.department+'&div='+this.workflowStatistics.division+'&user='+this.selectedrecepients+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption)
            }
            else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
                window.location.assign(this.base_url+'/FilenetService/documentsScannedReport?dpt='+this.workflowStatistics.department+'&div='+this.workflowStatistics.division+'&user='+this.selectedrecepients+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption)
            }
             else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
                window.location.assign(this.base_url+'/FilenetService/documentsScannedReport?dpt='+this.workflowStatistics.department+'&div='+this.workflowStatistics.division+'&user='+this.selectedrecepients+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption)        
            }
             else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
                window.location.assign(this.base_url+'/FilenetService/documentsScannedReport?dpt='+this.workflowStatistics.department+'&div='+this.workflowStatistics.division+'&user='+this.selectedrecepients+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption)
            }
        }
      
  }    
    hideDiv(){
        if(document.getElementsByClassName("errormessage") != undefined){
           document.getElementsByClassName("errormessage")[0].style.display = "none";
          }
    }
  scannedDocument(data){
    document.getElementById("displayLoader").style.display="none";
      this.divCodeUI ="";
     if(this.tempValue == undefined){
         this.tempValue = "All Division";
     }
     this.divCodeUI = this.tempValue;
      if(data.length>0)
      {
       this.showReport = true;  
          this.noResult = false;  
      }else{
         this.noResult = true; 
          this.showReport = false;
      }
      this.totalCount = data.length;
      this.documentScanned = data;
      let objects = {};
      for(let rep of this.tmpSelRecep){
          for(let recep of this.tmpSelRecep){
              if(rep.value == recep.value){
                  let counter = 0;
                  for(let docScan of this.documentScanned){
                      if(docScan.Creator == recep.value){
                        counter++;  
                      }
                  }
                recep.count = counter;  
              }
          }
      }
      this.docCount = JSON.stringify(objects);
      
      console.log(this.docCount);
  }
    
  submitReport(event: any) {

  }
 
  disableToDate(event: IMyInputFieldChanged){
    this.toDate = "";
    this.workflowStatistics.toDate = '';
   let todayDate = new Date();
   let date = event.value.split("/");
   let targetDate = new Date(date[2]+"-"+date[1]+"-"+date[0]);
   let copy: IMyOptions = this.getCopyOfEndDateOptions();
   todayDate.setHours(0, 0, 0, 0);
   targetDate.setHours(0, 0, 0, 0);
   if(todayDate.getTime() === targetDate.getTime()){ 
       let targetDateNew = new Date(date[2]+"-"+date[1]+"-"+date[0]);
       targetDateNew.setDate(targetDateNew.getDate()-1);
       copy.disableUntil =  {year: targetDateNew.getFullYear(), month: (targetDateNew.getMonth() + 1), day: (targetDateNew.getDate())};
       copy.disableSince = {year: todayDate.getFullYear(), month: (todayDate.getMonth() + 1), day: (todayDate.getDate()+1)};
   }else
      {
       if(parseInt(date[0]) == 1){
           copy.disableUntil = {year: targetDate.getFullYear(), month: targetDate.getMonth()+1, day: targetDate.getDate()};
       }else{
           copy.disableUntil = {year: targetDate.getFullYear(), month: targetDate.getMonth()+1, day: targetDate.getDate()-1};
       }
       copy.disableSince = {year: todayDate.getFullYear(), month: (todayDate.getMonth() + 1), day: (todayDate.getDate()+1)};
      }
   this.myDatePickerDisableOptions = copy;
   this.myDatePickerDisableOptions.componentDisabled = false
}
   
    getCopyOfEndDateOptions(): IMyOptions {
        return JSON.parse(JSON.stringify(this.myDatePickerDisableOptions));
    } 

    
}

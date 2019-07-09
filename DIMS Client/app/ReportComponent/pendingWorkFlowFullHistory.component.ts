import {Component, Output, EventEmitter,AfterViewInit} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseService} from '../services/browse.service'
import {ReportsService} from '../services/reports.service'
import {WorkflowStatistics} from '../model/workFlowStatistics.model'
import global = require('../global.variables')
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker,IMyInputFieldChanged} from 'mydatepicker';
import {PendingWorkflowsFullHistory, PendingWorkflowsFullHistoryProperties, PendingWorkflowsFullHistoryPropertiesList} from '../model/pendingWorkflowsFullHistory.model'

import {UserService} from '../services/user.service'
import {Department} from '../model/departmentRecipients.model'
import {RecipientsService} from '../services/recipients.service'
import {Reports} from '../model/reports.model'
import {SelectItem} from 'primeng/primeng';
import { Http, Headers } from '@angular/http';

@Component({
  selector: 'pending-work-flow-full-history',
  providers: [BrowseService, ReportsService,RecipientsService],
  templateUrl: 'app/ReportComponent/pendingWorkFlowFullHistory.component.html'
})

export class PendingWorkFlowFullHistory  implements AfterViewInit {
  workflowStatistics: WorkflowStatistics;

  private myDatePickerOptions: IMyOptions = global.date_picker_options_disable_Reports;
  private myDatePickerDisableOptions: IMyOptions = global.date_picker_disabled_Reports;
 
  private knpcHierarchy : any = [];
  private current_user: any;
  private divisions : any;
  private reports = new Reports();
  private userList : any;
  private recipientList : any;
     private pendingWFsFullHistoryList : any;
  private report : any;
    private doneWorkitems : number;
    private totalWorkitems : number;
    private notDoneWorkitems : number;
    private senderName : any;
    private donePercentage : number;
    private showReport= false;
    private table : any;
     private base_url: string;
    private receiveDate : string;
    private deadline : string;
    private recipientFullName : string;
    private key : any =[];
    dropdownRecepients: SelectItem[];
    dropdownSender: SelectItem[];
    selected_recepient: string;
     private noResult = false; 
  private selectOption : any ;
  private selectOptionText : any;
  private divisionReport : any;
  private selectedRecipient : any;
      private fromDate : any;
  private toDate : any;
  private currentUserLogin = '';
  private currentUserEmpName = '';


  pendingWorkflowsFullHistory: PendingWorkflowsFullHistory;
  pendingWorkflowsFullHistoryProperties: PendingWorkflowsFullHistoryProperties;
  PendingWorkflowsFullHistoryPropertiesList: PendingWorkflowsFullHistoryPropertiesList;

  constructor(private reportsService: ReportsService,private recipientsService: RecipientsService, private userService: UserService, private http: Http) {
    this.workflowStatistics = new WorkflowStatistics();
    this.dropdownRecepients = [];
    this.dropdownSender =[];
    this.pendingWorkflowsFullHistory = new PendingWorkflowsFullHistory();
    this.pendingWorkflowsFullHistoryProperties = new PendingWorkflowsFullHistoryProperties();
    this.PendingWorkflowsFullHistoryPropertiesList = new PendingWorkflowsFullHistoryPropertiesList();
    //ADDED
    this.current_user = this.userService.getCurrentUser();
    //this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.knpcHierarchy = data);
    this.workflowStatistics.sender = this.current_user.employeeLogin;
    this.base_url = global.base_url;
      //end
        
  }
      
      
      ngAfterViewInit(){
     
       
        this.loadUsersForFilter();
      }
      
      loadUsersForFilter() {
       
		
		if(this.userService.getCurrentDelegatedForEmployeeLogin()){
            this.currentUserLogin = JSON.parse(this.userService.getCurrentDelegatedForEmployeeLogin()).userLogin;
            this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assignUserName(data));
           
		}else{
            this.currentUserLogin = this.current_user.employeeLogin; 
            this.currentUserEmpName = this.current_user.employeeName;
            this.workflowStatistics.sender = this.current_user.employeeName;
            this.userService.getDepartmentName(this.current_user.employeeDepartment.departmentCode).subscribe(data => {this.current_user.employeeDepartment.department = data;});
            this.recipientsService.getUsersForDepartment1(this.current_user.employeeDepartment.departmentCode,"").subscribe(data => this.populateDepartUser(data));
            if(document.getElementById("userId").options.selectedIndex != -1){
                //this.recipientsService.getUsersForFilter(document.getElementById("userId").options[document.getElementById("userId").options.selectedIndex].value, this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateFilterUser(data));
                this.recipientsService.getUsersForFilter(document.getElementById("userId").options[document.getElementById("userId").options.selectedIndex].value, this.currentUserLogin).subscribe(data => this.populateFilterUser(data));
           } 
		}
		
		//console.log("user Clling :: ",this.currentUserLogin);	
        
       
        //this.recipientsService.getUsersForDepartment(department,searchCrtieria).subscribe();
        
    }

    assignUserName(data){
        console.log("DATATATATTATTATATATTATTA ::: ",data)
        this.currentUserEmpName = data.employeeName;
        this.current_user = data;
        this.userService.getDepartmentName(this.current_user.employeeDepartment.departmentCode).subscribe(data => {this.current_user.employeeDepartment.department = data;});
        this.recipientsService.getUsersForDepartment1(this.current_user.employeeDepartment.departmentCode,"").subscribe(data => this.populateDepartUser(data));
        this.workflowStatistics.sender = this.current_user.employeeLogin;
        if(document.getElementById("userId").options.selectedIndex != -1){
            //this.recipientsService.getUsersForFilter(document.getElementById("userId").options[document.getElementById("userId").options.selectedIndex].value, this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateFilterUser(data));
            this.recipientsService.getUsersForFilter(document.getElementById("userId").options[document.getElementById("userId").options.selectedIndex].value, this.currentUserLogin).subscribe(data => this.populateFilterUser(data));
       } 
    }
    
    populateDepartUser(data: any ) {
        let foundIndex;
        data.forEach((item,index) => {
            if(item.employeeName == this.current_user.employeeName) {
                foundIndex = index;
            }
            });
    
        data.splice(foundIndex,1); 
        this.userList = data;
        this.dropdownSender =[];
        for(let i=0;i<data.length;i++){
        //this.recepient.push({label:this.recipientList[i].employeeName, value:this.recipientList[i].employeeLogin});
        this.dropdownSender.push({label:data[i].employeeName, value:data.employeeLogin});
 
        }
        
        
    }
    
    populateFilterUser(data: any ) {
        
        this.dropdownRecepients =[];
        this.recipientList = [];
        this.workflowStatistics.recipient = null;
        this.recipientList = data;
        for(let i=0;i<data.length;i++){
        //this.recepient.push({label:this.recipientList[i].employeeName, value:this.recipientList[i].employeeLogin});
        this.dropdownRecepients.push({label:this.recipientList[i].employeeName, value:this.recipientList[i].employeeLogin});
 
        }
    }
      
    pendingWorkitemsFullHistory() {
        document.getElementById("displayLoader").style.display="block";
         this.fromDate = '';
      this.toDate ='';
      if(this.workflowStatistics.fromDate != undefined){
            this.fromDate = this.workflowStatistics.fromDate.formatted;
        }else{
            this.fromDate = undefined ;
        }
        if(this.workflowStatistics.toDate != undefined){
            this.toDate = this.workflowStatistics.toDate.formatted;
        }else{
            this.toDate = undefined ;
        }
        this.workflowStatistics.status = document.getElementById("itemStatus").options[document.getElementById("itemStatus").options.selectedIndex].text;
        if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
              this.reportsService.getPendingWorkflowsFullHistory(this.currentUserLogin, this.workflowStatistics.status, this.workflowStatistics.recipient, this.fromDate, this.toDate).subscribe(data => this.pendingWFsFullHistory(data));
        }
        else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
              this.reportsService.getPendingWorkflowsFullHistory(this.currentUserLogin, this.workflowStatistics.status, this.workflowStatistics.recipient, this.fromDate, this.toDate).subscribe(data => this.pendingWFsFullHistory(data));
        }
         else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
              this.reportsService.getPendingWorkflowsFullHistory(this.currentUserLogin, this.workflowStatistics.status, this.workflowStatistics.recipient, this.fromDate, this.toDate).subscribe(data => this.pendingWFsFullHistory(data));
        }
        else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
              this.reportsService.getPendingWorkflowsFullHistory(this.currentUserLogin, this.workflowStatistics.status, this.workflowStatistics.recipient, this.fromDate, this.toDate).subscribe(data => this.pendingWFsFullHistory(data));
        }
    }
    hideDiv(){
        if(document.getElementsByClassName("errormessage") != undefined){
           document.getElementsByClassName("errormessage")[0].style.display = "none";
          }
    }
    pendingWFsFullHistory(data: any) {
         document.getElementById("displayLoader").style.display="none";
        this.selectedRecipient ="";
        for(let dropdwnrecp of this.dropdownRecepients){
            if(dropdwnrecp.value == this.workflowStatistics.recipient){
               this.selectedRecipient = dropdwnrecp.label;
            }
        }
        this.pendingWFsFullHistoryList = data;
        this.key =[];
        let count = 0;
        for (var key in this.pendingWFsFullHistoryList) {
            this.key.push(key);
            ++count;
        } 
        
        if(count >0)
      {
       this.showReport = true;  
          this.noResult = false;  
      }else{
         this.noResult = true; 
          this.showReport = false;
      }
        this.selectOption = document.getElementById("senderName").options[document.getElementById("senderName").options.selectedIndex].value;
        this.selectOptionText = document.getElementById("senderName").options[document.getElementById("senderName").options.selectedIndex].text;
       // this.recipientFullName = document.getElementById("recipient").options[document.getElementById("recipient").options.selectedIndex].text;
        //this.recipientFullName = this.selected_recepient;

        }
    
     //export data for pending workitems in PDF or Excel
    exportData() {
        var startExport = true;
        var selected = document.getElementById("options");
        var selectedOption = selected.options[selected.selectedIndex].text;
        
        if(selectedOption != 'PDF' && selectedOption != 'Excel') {
            startExport = false;
        }
        this.workflowStatistics.status = document.getElementById("itemStatus").options[document.getElementById("itemStatus").options.selectedIndex].text;
        if(startExport) {
            if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
                window.location.assign(this.base_url+'/WorkflowSearchService/pendingWorkflowsFullHistoryReport?sender='+this.currentUserLogin+'&status='+this.workflowStatistics.status+'&recipient='+this.workflowStatistics.recipient+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption+'&random='+new Date().getTime())
            }
            else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
                //this.reportsService.getPendingWoritemsReport(this.workflowStatistics.sender, this.workflowStatistics.status,this.workflowStatistics.overdue, this.selectedrecepients, this.workflowStatistics.fromDate, this.workflowStatistics.toDate, selectedOption).subscribe(data=>data);
                window.location.assign(this.base_url+'/WorkflowSearchService/pendingWorkflowsFullHistoryReport?sender='+this.currentUserLogin+'&status='+this.workflowStatistics.status+'&recipient='+this.workflowStatistics.recipient+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption+'&random='+new Date().getTime())
            }
             else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
                window.location.assign(this.base_url+'/WorkflowSearchService/pendingWorkflowsFullHistoryReport?sender='+this.currentUserLogin+'&status='+this.workflowStatistics.status+'&recipient='+this.workflowStatistics.recipient+'&from='+this.fromDate.formatted+'&to='+this.toDate+'&exportType='+selectedOption+'&random='+new Date().getTime())        
            }
             else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
                window.location.assign(this.base_url+'/WorkflowSearchService/pendingWorkflowsFullHistoryReport?sender='+this.currentUserLogin+'&status='+this.workflowStatistics.status+'&recipient='+this.workflowStatistics.recipient+'&from='+this.fromDate+'&to='+this.toDate+'&exportType='+selectedOption+'&random='+new Date().getTime())
            }
        }
       // window.location.assign(this.base_url+'/WorkflowSearchService/pendingWorkflowsReport?sender=fatima&recipient=Jasem&exportType=pdf');
        //http://localhost:9080/DIMS/resources/WorkflowSearchService/pendingWorkflowsReport?random=hgjghjgj
    }

  submitReport(event: any) {
  }

    loadReportList(sender : any){
        
        this.divisionReport = this.pendingWFsFullHistoryList[sender];
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

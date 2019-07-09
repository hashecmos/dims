import {Component, Output, EventEmitter} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseService} from '../services/browse.service'
import {ReportsService} from '../services/reports.service'
import {WorkflowStatistics} from '../model/workFlowStatistics.model'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker,IMyInputFieldChanged} from 'mydatepicker';
import {RecipientsService} from '../services/recipients.service'
import {UserService} from '../services/user.service'
import {Department} from '../model/departmentRecipients.model'
import {Reports} from '../model/reports.model'

import global = require('../global.variables')
import { Http, Headers } from '@angular/http';

@Component({
  selector: 'report-list',
  providers: [BrowseService, ReportsService,RecipientsService],
  templateUrl: 'app/ReportComponent/report.component.html'
})

export class Report {
  workflowStatistics: WorkflowStatistics;
  private myDatePickerOptions: IMyOptions = global.date_picker_options_disable_Reports;
  private myDatePickerDisableOptions: IMyOptions = global.date_picker_disabled_Reports;
  statisticsReportList: any;
  private current_user: any;
  private divisions : any;
  private report : any;
  private showReport= false;
  private reports = new Reports();
  private wfStatistics : any;  
  private wfStatisticsList : any;
  private base_url: string;  
  private workflowStatisticReport : any = [];
  private selectOption : any ;
  private selectOptionText : any;
  private divisionReport : any;
  private totalWFCount : number =0;
  private totalNewWFCount : number = 0;
  private totalActiveWFCount : number = 0;
  private totalOverdueWFCount : number = 0;
  private divisionEmpCount : number = 0;
  private noResult = false;
    
  private all : any;
  private new : any;
  private active : any;
  private overdue : any;
  private delegatedJobTitle : any;
 // public delegatedUserDetails : any;
    
  constructor(private reportsService: ReportsService, private recipientsService: RecipientsService, private userService: UserService, private http: Http) {
    this.workflowStatistics = new WorkflowStatistics();
    this.statisticsReportList = reportsService.getStatisticsReportList();
    this.current_user = this.userService.getCurrentUser();
    this.delegatedJobTitle  = this.userService.getDelegatedJobTitle();
   // this.delegatedUserDetails = '';
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
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    } else if(this.current_user.employeeJobTitle == 'DCEO'){
        let _ref = this
        setTimeout( function() {  
            let data:any =[]
                data.push(_ref.current_user.employeeDivision);
                _ref.populateDisvision(data);
            }, 200 );
    } else if(this.current_user.employeeJobTitle == 'MGR'){
        this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
    } else{
        //this else block will be in case of TL, Sr eng, or below
        let _ref = this
        setTimeout( function() {  
            let data:any =[]
                data.push(_ref.current_user.employeeDivision);
                _ref.populateDisvision(data);
            }, 200 );
    }
  }
    
    getReports(data){
        this.current_user = data
        console.log("Came to delegatedUserDetails  AFTER current_user:: ",this.current_user);
        //if(data.employeeJobTitle == 'DCEO' || data.employeeJobTitle == 'MGR')
        if(this.current_user.employeeJobTitle == 'CEO' || this.current_user.employeeJobTitle == 'MGR'){
            this.recipientsService.getDivisionsDetailForDepartment(this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateDisvision(data));
        } else {
            //this else block will be in case of TL, Sr eng, or below
            let _ref = this
            setTimeout( function() {  
                let data:any =[]
                    data.push(_ref.current_user.employeeDivision);
                    _ref.populateDisvision(data);
                }, 200 );
        }
    }

  getDepartmentCode(){
     if(document.getElementById("departmentId").options.selectedIndex != -1){
             this.recipientsService.getDivisionsDetailForDepartment(document.getElementById("departmentId").options[document.getElementById("departmentId").options.selectedIndex].value).subscribe(data => this.populateDisvision(data));
        } 
  }

  populateDisvision(data){
      this.divisions = data;
      document.getElementById("division").disabled = false;
      for(let division of this.divisions){
          document.getElementById("division").options[document.getElementById("division").options.length] = new Option(division.empDivision,division.empDivisionCode);
      }
  }
    
  getWorkflowStatistics() {
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
      
      this.workflowStatistics.department = document.getElementById("departmentId").options[document.getElementById("departmentId").options.selectedIndex].value
      this.workflowStatistics.division = document.getElementById("division").options[document.getElementById("division").options.selectedIndex].value

      console.log("TEST :: ",this.current_user.employeeLogin);
      console.log("TEST :: ",this.workflowStatistics.department);
      console.log("TEST :: ",this.workflowStatistics.division);
      
      if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
            this.reportsService.getWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate).subscribe(data => this.populateWFStatistics(data));
        }
        else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
            this.reportsService.getWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate).subscribe(data => this.populateWFStatistics(data));
        }
         else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
            this.reportsService.getWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate).subscribe(data => this.populateWFStatistics(data));
        }
         else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
            this.reportsService.getWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate).subscribe(data => this.populateWFStatistics(data));
        }
        
  }
    
  populateWFStatistics(data: any) {
       document.getElementById("displayLoader").style.display="none";
      if(document.getElementsByClassName("errormessage") != undefined){
           document.getElementsByClassName("errormessage")[0].style.display = "none";
          }
        this.wfStatisticsList = [];
        this.wfStatisticsList = data;
        let count = 0;
        for (var key in this.wfStatisticsList) {
            ++count; 
        }
    
        if(count>0)
          {
           this.showReport = true;  
              this.noResult = false;  
          }else{
             this.noResult = true; 
              this.showReport = false;
          }
        this.selectOption = document.getElementById("division").options[document.getElementById("division").options.selectedIndex].value;
        this.selectOptionText = document.getElementById("division").options[document.getElementById("division").options.selectedIndex].text;
        if(this.selectOption != -1){
            this.divisionEmpCount = this.wfStatisticsList[this.selectOptionText].length;
        }
        
        this.totalWFCount = 0
        this.totalNewWFCount = 0
        this.totalActiveWFCount = 0
        this.totalOverdueWFCount = 0;
        
        this.all = [];
        this.new = [];
        this.active = [];
        this.overdue = [];
        
        /*if(this.delegatedUserDetails.employeeJobTitle =='MGR' || this.delegatedUserDetails.employeeJobTitle =='DCEO'|| this.current_user.employeeJobTitle == 'MGR' || this.current_user.employeeJobTitle == 'DCEO'){
              for(let division of this.divisions){
                if(this.selectOption == -1){
                    let div = this.wfStatisticsList[division.empDivision];
                    let allcount =0;
                    let newcount =0;
                    let activecount =0;
                    let overduecount = 0;
                    if(div != undefined){
                        for(var i=0; i< div.length ; i++){
                            this.totalWFCount = this.totalWFCount + (+div[i].totWF);
                            this.totalNewWFCount = this.totalNewWFCount+ (+div[i].totNWF);
                            this.totalActiveWFCount = this.totalActiveWFCount+ (+div[i].totAWF);
                            this.totalOverdueWFCount = this.totalOverdueWFCount+ (+div[i].totOWF);
                            
                            allcount = allcount + (+div[i].totWF);
                            newcount = newcount+ (+div[i].totNWF);
                            activecount = activecount+ (+div[i].totAWF);
                            overduecount = overduecount+ (+div[i].totOWF);
                        }
                        this.all.push(allcount);
                        this.new.push(newcount);
                        this.active.push(activecount);
                        this.overdue.push(overduecount);
                    }
                    
                }else{
                    let div = this.wfStatisticsList[division.empDivision];
                    if(div!=undefined){
                        let allcount =0;
                        let newcount =0;
                        let activecount =0;
                        let overduecount = 0;
                        for(var i=0; i< div.length ; i++){
                           this.totalWFCount = this.totalWFCount + (+div[i].totWF);
                            this.totalNewWFCount = this.totalNewWFCount+ (+div[i].totNWF);
                            this.totalActiveWFCount = this.totalActiveWFCount+ (+div[i].totAWF);
                            this.totalOverdueWFCount = this.totalOverdueWFCount+ (+div[i].totOWF);
                            
                             allcount = allcount + (+div[i].totWF);
                            newcount = newcount+ (+div[i].totNWF);
                            activecount = activecount+ (+div[i].totAWF);
                            overduecount = overduecount+ (+div[i].totOWF);
                        }
                        this.all.push(allcount);
                        this.new.push(newcount);
                        this.active.push(activecount);
                        this.overdue.push(overduecount);
                    }
                }
            }
        }else if(this.current_user.employeeJobTitle == 'TL' || this.delegatedUserDetails.employeeJobTitle =='MGR'){
            let div = this.wfStatisticsList[this.selectOptionText];
            if(div!=undefined){
                let allcount =0;
                let newcount =0;
                let activecount =0;
                let overduecount = 0;
                for(var i=0; i< div.length ; i++){
                   this.totalWFCount = this.totalWFCount + (+div[i].totWF);
                    this.totalNewWFCount = this.totalNewWFCount+ (+div[i].totNWF);
                    this.totalActiveWFCount = this.totalActiveWFCount+ (+div[i].totAWF);
                    this.totalOverdueWFCount = this.totalOverdueWFCount+ (+div[i].totOWF);
                    
                    allcount = allcount + (+div[i].totWF);
                    newcount = newcount+ (+div[i].totNWF);
                    activecount = activecount+ (+div[i].totAWF);
                    overduecount = overduecount+ (+div[i].totOWF);
                }
                this.all.push(allcount);
                this.new.push(newcount);
                this.active.push(activecount);
                this.overdue.push(overduecount);
            }
        }*/

        for(let division of this.divisions){
            let div = this.wfStatisticsList[division.empDivision];
                          let allcount =0;
                          let newcount =0;
                          let activecount =0;
                          let overduecount = 0;
                          if(div != undefined){
                              for(var i=0; i< div.length ; i++){
                                  this.totalWFCount = this.totalWFCount + (+div[i].totWF);
                                  console.log("div[i].totWF  ::: ",div[i].totWF)
                                  this.totalNewWFCount = this.totalNewWFCount+ (+div[i].totNWF);
                                  console.log("div[i].totNWF  ::: ",div[i].totNWF)
                                  this.totalActiveWFCount = this.totalActiveWFCount+ (+div[i].totAWF);
                                  console.log("div[i].totAWF  ::: ",div[i].totAWF)
                                  this.totalOverdueWFCount = this.totalOverdueWFCount+ (+div[i].totOWF);
                                  console.log("div[i].totOWF  ::: ",div[i].totOWF)
                                  
                                  allcount = allcount + (+div[i].totWF);
                                  newcount = newcount+ (+div[i].totNWF);
                                  activecount = activecount+ (+div[i].totAWF);
                                  overduecount = overduecount+ (+div[i].totOWF);
                              }
                              this.all[division.empDivision] = allcount;
                              this.new[division.empDivision] = newcount;
                              this.active[division.empDivision] = activecount;
                              this.overdue[division.empDivision] = overduecount;
                          }
                      console.log('this.all        :', this.all);
                      console.log('this.new        :', this.new);
                      console.log('this.active     :', this.active);
                      console.log('this.overdue    :', this.overdue);
            }
         console.log(this.all)
    }
    
     //export data for pending workitems in PDF or Excel
    exportData() {
        var startExport = true;
        var selected = document.getElementById("options");
        var selectedOption = selected.options[selected.selectedIndex].text;
        this.workflowStatistics.department = document.getElementById("departmentId").options[document.getElementById("departmentId").options.selectedIndex].value
        if(selectedOption != 'PDF' && selectedOption != 'Excel') {
            startExport = false;
        }
        
        if(startExport) {
            if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
                this.reportsService.exportWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate,selectedOption)
            }
            else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
                this.reportsService.exportWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate,selectedOption)
            }
             else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
                this.reportsService.exportWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate.formatted,this.toDate,selectedOption)
            }
             else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
                this.reportsService.exportWorkflowStatistics(this.current_user.employeeLogin,this.workflowStatistics.department,this.workflowStatistics.division,this.fromDate,this.toDate,selectedOption)
            }
        }
    }
    
    loadReportList(divisionName : any){
        this.divisionReport = this.wfStatisticsList[divisionName];
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

import {ViewChild,Component, Output, EventEmitter, AfterViewInit,ElementRef} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseService} from '../services/browse.service'
import {WorkflowStatistics} from '../model/workFlowStatistics.model'
import {ReportsService} from '../services/reports.service'
import global = require('../global.variables')
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker,IMyInputFieldChanged} from 'mydatepicker';
import {PendingWorkflows, PendingWorkflowsProperties, PendingWorkflowsPropertiesList} from '../model/pendingWorkflows.model'

import {UserService} from '../services/user.service'
import {Department} from '../model/departmentRecipients.model'
import {RecipientsService} from '../services/recipients.service'
import {Reports} from '../model/reports.model'
import {SelectItem} from 'primeng/primeng';
import { UIChart } from "primeng/primeng";
import { Http, Headers } from '@angular/http';
@Component({
  selector: 'pending-work-flow',
  providers: [BrowseService,ReportsService,RecipientsService],
  templateUrl: 'app/ReportComponent/pendingWorkFlows.component.html',
  directives: [UIChart]
})

export class PendingWorkFlows implements AfterViewInit{
  @ViewChild('barChart') barChart;
  private myDatePickerOptions: IMyOptions = global.date_picker_options_disable_Reports;
  private myDatePickerDisableOptions: IMyOptions = global.date_picker_disabled_Reports;
  //private knpcHierarchy : any = [];
  private current_user: any;
  private divisions : any;
  workflowStatistics: WorkflowStatistics;
  private reports = new Reports();
  private userList : any;
  private recipientList : any;
  private pendingWFsList : any;
  private report : any;
  private doneWorkitems : number;
  private totalWorkitems : number;
  private notDoneWorkitems : number;
  private senderName : any;
  private donePercentage : number;
  private recepient: SelectItem[];
  private selectedrecepients: string[];
  private showReport= false;
  private table : any;
  private formData: FormData;
  private base_url: string;
  private data: any;
  private option: any;
  private senderFullName : any;
  private keys : any;
  private pendingWorkflows: PendingWorkflows;
  private pendingWorkflowsProperties: PendingWorkflowsProperties;
  private pendingWorkflowsPropertiesList: PendingWorkflowsPropertiesList;
  private test: any;
    
  private label : any;
  private doneData : any;
  private notDoneData : any;
  private datasets : any;
  private percentage : any;
  private base64 : any;
  private self : any;
  private noResult = false;
  private fromDate : any;
  private toDate : any;
  private currentUserLogin = '';
  private currentUserEmpName = '';
  
  constructor(private reportsService: ReportsService,private recipientsService: RecipientsService, private userService: UserService, private http: Http) {
    this.workflowStatistics = new WorkflowStatistics();
    this.pendingWorkflows = new PendingWorkflows();
    this.pendingWorkflowsProperties = new PendingWorkflowsProperties();
    this.pendingWorkflowsPropertiesList = new PendingWorkflowsPropertiesList();
    this.current_user = this.userService.getCurrentUser();
    //this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.knpcHierarchy = data);
    this.workflowStatistics.sender = this.current_user.employeeLogin;
   // this.userService.getDepartmentName(this.current_user.employeeDepartment.departmentCode).subscribe(data => {this.current_user.employeeDepartment.department = data;});
    this.recepient = [];
    this.base_url = global.base_url
    this.self = this;  
    this.option = {
        tooltips: {
            enabled : false,
        },
        //interactivityenabled: false,
        scales: {
                    xAxes: [{
                        stacked: true,
                        gridLines: {
                            display: false
                        },
                        categoryPercentage: 1.0,
                        barPercentage: 1.0,
                        position:'bottom',
                        ticks: {
                        callback: function(value, index, values) {
                            var regex = /^[0-9]*(?:\d{1,2})?$/;    // allow only numbers [0-9] 
                            if( regex.test(value) ) {
                                  return  value;
                            }
                            
                            }
                        }
                    }],
                    yAxes: [{
                        stacked: true,
                        maxBarThickness:20,
                        gridLines: {
                            display: false
                        },
                        categoryPercentage: 1.0,
                        barPercentage: 1.0,
                        ticks: {
                            beginAtZero:true,
                            callback: function(value, index, values) {
                                return value;
                                
                                }
                            }
                    }]
                },
        animation: {
          duration: 1,
          onComplete: function(animationObject) {
            if(animationObject.chart.ctx != undefined){
                
                var ctx = animationObject.chart.ctx;
                this.data.datasets.forEach(function(dataset, i) {
                  var meta = animationObject.chart.controller.getDatasetMeta(i);
                  meta.data.forEach(function(bar, index) {
                    var data = dataset.data[index];
                    ctx.fillStyle = "#000";
                    ctx.textAlign = 'right';
                    if(data!=0 && (bar._chart.legend.legendItems[0].hidden == false && bar._model.datasetLabel=='Done')){
                        ctx.fillStyle = "#000";                        
                        ctx.fillText(data, bar._model.x, bar._model.y - 5);
                    }
                    if(data!=0 && (bar._chart.legend.legendItems[1].hidden == false && bar._model.datasetLabel=='Not Done')){
                        ctx.fillStyle = "#fff";                        
                        ctx.fillText(data, bar._model.x, bar._model.y - 5);
                    }
                    
                  });
                });
               }
            }
        }
     }
      
  }
   
  ngAfterViewInit(){

    this.loadUsersForFilter();
      
     
  }
    
    changeFrom() {
        var fromVal = document.getElementById("getValFrom").checked;
      
      if(fromVal) {
          document.getElementById("fromDt").disabled = true;
      }
      else {
          document.getElementById("fromDt").disabled = false;
      }
    }

  submitReport(event: any) {
    
      //this.reportsService.getPendingWorkflows(this.workflowStatistics.sender, this.workflowStatistics.user, this.workflowStatistics.status, this.workflowStatistics.recipient, this.workflowStatistics.fromDate.formatted, this.workflowStatistics.toDate.formatted).subscribe(data => this.report);
        
  }

    pending_Workflows() {
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
        
        for(var i=0;i<document.getElementsByName("overDue").length;i++)
        {
            if(document.getElementsByName("overDue")[i].checked){
                this.workflowStatistics.overdue = document.getElementsByName("overDue")[i].value;
            }
        }
        this.workflowStatistics.status = document.getElementById("itemStatus").options[document.getElementById("itemStatus").options.selectedIndex].text;
        if(this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate != undefined) {
            this.reportsService.getPendingWorkflows(this.currentUserLogin, this.workflowStatistics.status,this.workflowStatistics.overdue, this.selectedrecepients, this.fromDate, this.toDate).subscribe(data => this.pendindWFs(data));
        
        }
        else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate == undefined) {
            this.reportsService.getPendingWorkflows(this.currentUserLogin, this.workflowStatistics.status,this.workflowStatistics.overdue, this.selectedrecepients, this.fromDate, this.toDate).subscribe(data => this.pendindWFs(data));
        
        }
         else if (this.workflowStatistics.fromDate != undefined && this.workflowStatistics.toDate == undefined) {
            this.reportsService.getPendingWorkflows(this.currentUserLogin, this.workflowStatistics.status,this.workflowStatistics.overdue, this.selectedrecepients, this.fromDate, this.toDate).subscribe(data => this.pendindWFs(data));
        
        }
         else if (this.workflowStatistics.fromDate == undefined && this.workflowStatistics.toDate != undefined) {
            this.reportsService.getPendingWorkflows(this.currentUserLogin, this.workflowStatistics.status,this.workflowStatistics.overdue, this.selectedrecepients, this.fromDate, this.toDate).subscribe(data => this.pendindWFs(data));
        
        }
    }
    
     loadUsersForFilter() {
      	
		if(this.userService.getCurrentDelegatedForEmployeeLogin()){
            this.currentUserLogin = JSON.parse(this.userService.getCurrentDelegatedForEmployeeLogin()).userLogin;
            this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assignUserName(data));
           
		}else{
            this.currentUserLogin = this.current_user.employeeLogin;
            this.currentUserEmpName = this.current_user.employeeName;
            this.workflowStatistics.sender = this.current_user.employeeName;
            this.recipientsService.getUsersForDepartment1(this.current_user.employeeDepartment.departmentCode,"").subscribe(data => this.populateDepartUser(data));
            this.userService.getDepartmentName(this.current_user.employeeDepartment.departmentCode).subscribe(data => {this.current_user.employeeDepartment.department = data;});
            console.log("user Clling :: ",this.currentUserLogin);	
            if(document.getElementById("userId").options.selectedIndex != -1){
                //this.recipientsService.getUsersForFilter(document.getElementById("userId").options[document.getElementById("userId").options.selectedIndex].value, this.current_user.employeeDepartment.departmentCode).subscribe(data => this.populateFilterUser(data));
                 this.recipientsService.getUsersForFilter(document.getElementById("userId").options[document.getElementById("userId").options.selectedIndex].value, this.currentUserLogin).subscribe(data => this.populateFilterUser(data));
            } 
		}
		
		
      
    }
    assignUserName(data){
        console.log("DATATATATTATTATATATTATTA ::: ",data)
        this.currentUserEmpName = data.employeeName;
        this.current_user = data;
        this.recipientsService.getUsersForDepartment1(this.current_user.employeeDepartment.departmentCode,"").subscribe(data => this.populateDepartUser(data));
        this.userService.getDepartmentName(this.current_user.employeeDepartment.departmentCode).subscribe(data => {this.current_user.employeeDepartment.department = data;});
        this.workflowStatistics.sender = this.current_user.employeeLogin;
        console.log("user Clling :: ",this.currentUserLogin);	
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
    }
    
    populateFilterUser(data: any ) {
        this.recipientList =[];
        this.recipientList = data;
        this.recepient = [];
        this.selectedrecepients = [];
        for(let i=0;i<data.length;i++){
            this.recepient.push({label:this.recipientList[i].employeeName, value:this.recipientList[i].employeeLogin});
        }
    }
    hideDiv(){
        if(document.getElementsByClassName("errormessage") != undefined){
           document.getElementsByClassName("errormessage")[0].style.display = "none";
          }
    }
    pendindWFs(data: any) {
         document.getElementById("displayLoader").style.display="none";
        this.senderFullName = document.getElementById("senderName").options[document.getElementById("senderName").options.selectedIndex].text;
        this.pendingWFsList = data;
        this.label = new Array();
        this.keys= new Array();
        this.notDoneData = new Array();
        this.doneData = new Array();
        this.data = new Object();
        this.percentage = new Array();
        
        let count = 0;
        for (var key in this.pendingWFsList) {
            ++count;
            //Added By Malik
            //this.label.push(key);
            this.label.push(key +"");
            this.keys.push(key);
            var wfList = this.pendingWFsList[key];
            let doneCount =0;
            let notDoneCount =0;
            
            for (var i = 0; i < wfList.length; i++) { 
                if(wfList[i].workitemStatus != "Done"){
                    notDoneCount++ ;
                }
                if(wfList[i].workitemStatus == "Done"){
                    doneCount++
                }
            }
            let percentageCal = Math.round((doneCount/wfList.length)*100);
            
            this.notDoneData.push(notDoneCount);
            this.notDoneData = this.notDoneData.slice();
            this.doneData.push(doneCount);
            this.doneData = this.doneData.slice();
            this.percentage.push(percentageCal);
            this.percentage =this.percentage.slice();
            
        } 
        
         if(count>0)
          {
           this.showReport = true;  
              this.noResult = false;  
          }else{
             this.noResult = true; 
              this.showReport = false;
          }
        
        this.data = {
                labels: this.label,
                datasets : [
                    {
                        label: 'Done',
                        backgroundColor: '#4377f1',
                        stack: 'Stack 0',
                        borderColor: '#1E88E5',
                        data: this.doneData
                    },
                    {
                        label: 'Not Done',
                        backgroundColor: '#b31958',
                        stack: 'Stack 0',
                        borderColor: '#7CB342',
                        data: this.notDoneData
                    }
                
                    ],
            
            };
        
      
        this.data.labels = this.label;
        
        if(this.barChart != undefined){
             setTimeout(() => {
                 if(this.barChart != undefined){
                     this.barChart.reinit();
                     this.barChart.refresh();
                 }
            }, 100);
        }
        
    }
    
            
   dataURLtoBlob(dataurl) {
       let arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
       bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
       while(n--){
           u8arr[n] = bstr.charCodeAt(n);
       }
       return new Blob([u8arr], {type:mime});
   }
    
    dataURItoBlob (dataURI) {
        
        // convert base64/URLEncoded data component to raw binary data held in a string
        let byteString;
        if (dataURI.split(',')[0].indexOf('base64') >= 0)
            byteString = atob(dataURI.split(',')[1]);
        else
            byteString = unescape(dataURI.split(',')[1]);
    
        // separate out the mime component
        let mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    
        // write the bytes of the string to a typed array
        let ia = new Uint8Array(byteString.length);
        for (let i = 0; i < byteString.length; i++) {
            ia[i] = byteString.charCodeAt(i);
        }
    
        return new Blob([ia], {type:mimeString});
    
    }
    
    pendingWFsCanvasImage(data: any,selectedOption : any) {
        var fileName = '';
        if(selectedOption == 'PDF'){
            fileName = "Pending Workflow.pdf";
        }else if(selectedOption =='Excel'){
            fileName = "Pending Workflow.xls";
        }
            
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(data, fileName);
        } else {
           var objectUrl = URL.createObjectURL(data);
           window.open(objectUrl);
        }
    }
    
    //export data for pending workitems in PDF or Excel
    exportData() {
       
        var startExport = true;
        var selected = document.getElementById("options");
        var selectedOption = selected.options[selected.selectedIndex].text;
        this.workflowStatistics.status = document.getElementById("itemStatus").options[document.getElementById("itemStatus").options.selectedIndex].text;
        if(selectedOption != 'PDF' && selectedOption != 'Excel') {
            startExport = false;
        }
        
        if(startExport) {
        
            let canvas = document.getElementsByTagName("canvas");
            let canvasOne = <HTMLCanvasElement> canvas[0];
            let dataURL = canvasOne.toDataURL('image/png', 0.5);
            let blob = this.dataURItoBlob(dataURL);
            let fd = new FormData(document.forms[0]);
            fd.append("canvasImage", blob);            
            fd.append("sender", this.currentUserLogin);
            fd.append("status", this.workflowStatistics.status);
            fd.append("overdue", this.workflowStatistics.overdue);
            fd.append("recipient", this.selectedrecepients);
            if(this.workflowStatistics.fromDate!=undefined){
                fd.append("from", this.fromDate);
            }else{
                fd.append("from", "undefined");
                }
            if(this.workflowStatistics.toDate!=undefined){
                 fd.append("to", this.toDate);
            }else{
                fd.append("to", "undefined");
            }
           
            fd.append("exportType", selectedOption);
            
            this.reportsService.canvasImage(fd,selectedOption).subscribe(data => this.pendingWFsCanvasImage(data,selectedOption));
        }
             
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

import {Component, Output, EventEmitter} from '@angular/core'
import {Router} from '@angular/router'
import {TaskAdvanceSearch} from '../../model/taskSearch.model'
import { TaskSimpleSearch} from '../../model/taskSimpleSearch.model'
import {AdvanceSearchForm} from '../../model/advanceSearchForm.model'
import {AdvanceSearchRequestObject} from '../../model/advanceSearchRequestObject.model'
import {SimpleSearchRequestObject} from '../../model/simpleSearchRequestObject.model'
import {LaunchWorkflowSearchFilter} from '../../model/launchWorkflowSearchFilter.model'
import {TaskItems} from '../../model/taskItems.model'
import {User} from '../../model/user.model'
import {SearchService} from '../../services/search.service'
import {SharedService} from '../../services/advanceSearchTasks.service'
import {UserService} from '../../services/user.service'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';
import {IMyDrpOptions, IMyDateRangeModel} from 'mydaterangepicker';
import global = require('./../../global.variables')
import {BrowseService} from './../../services/browse.service'
import {WorkflowService} from './../../services/workflowServices/workflow.service'
import {BrowseSharedService} from './../../services/browseEvents.shared.service'
import { Observable, Subscription } from 'rxjs/Rx';
import {RecipientsService} from './../../services/recipients.service'
import {SelectItem} from 'primeng/primeng';

@Component({
  selector: 'header',
  providers: [SearchService,RecipientsService],
  templateUrl: 'app/SharedComponents/headerComponent/header.component.html'
})

export class Header {
	adv_date_empty: boolean;
	date_empty: boolean;
	form_empty: boolean;
	tmpToArray = []
	tmpFromArray = []
	selectedrecepients: any[];
	recepient: any[];
	recipientList: any[];
	errorMessage: string;
  private current_user_name;
  private myDatePickerOptions: IMyOptions = global.date_picker_options_disable;
  private myDateRangePickerOptions: IMyDrpOptions  = global.date_range_option;
  private current_user_login_name;
  private delegateForEmployeeLogin: any;
  taskItems: TaskItems[];
  private documentAdvanceSearchRequestObject = new AdvanceSearchRequestObject();
  private documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
  private workflowSearch = new TaskAdvanceSearch();
  private simpleWorkflowSearch = new TaskSimpleSearch();
  private filter = new LaunchWorkflowSearchFilter();
  public textComparisonOperators: string[] = ["is equal to", "like","contains", "starts with","ends with"];
  public numberComparisonOperators: string[] = ["=", "in", "not in"];
  public dateComparisonOperators: string[] = ["period","=",">", "<", "<=", ">=","between"];
  public equalOperator: string[] = ["="];
  public equalOperatorForDocuemntID: string[] = ["is equal to"];
  private documentSearchForm = new AdvanceSearchForm();
  private activeTab: string = "workflows";
  private simpleSearchRequestObject = new SimpleSearchRequestObject();
  public notification_message: string="";
  public error_message: string="";
  public error_message2: String = "Please provide atleast one search criteria and try again"
  private search_empty: boolean = false;
  public document_from : any[];
  ticks = 0;
  private timer;
  private sub: Subscription;
  private current_user :any
  private departments :any;
  private instructions :  any;
  private userFromSearchUrl : any;
  private userToSearchUrl : any;
  private to : any; 
  private from : any;
  private documentType : any;
  private userSearchUrl;
  private creator : any;

  private subjectExtra= []
  private subjectExtraVal = []
  private refExtra= []
  private refExtraVal = []
  private idExtra= []
  private idExtraVal = []
  private commentsExtra= []
  private commentsExtraVal = []
  private dateFrom =  ''
  private dateTo = ''
  private recieveDate = '';
  private dateCreated = '';
  private  dateObj = new Date();
  private years=this.dateObj.getFullYear();
  private months=this.dateObj.getMonth()+1; 
  private days=this.dateObj.getDate();
  private myDatePickerOptions1: IMyOptions = {
	dateFormat: 'dd/mm/yyyy',
	showTodayBtn: false,
	editableDateField: true,
	markCurrentDay: true,
	showClearDateBtn: false,
	openSelectorOnInputClick: true
  };

  ///  
     private docTypeList:SelectItem[];
   private populatedocumentfrom:SelectItem[];
    private populateCorespondence:SelectItem[];
  /////  
  private newArray:any[];
  private docTypes:any;
  ////  
    private selected: any[];
    private selectedDocumentFrom:any[];
    private selectedDocumentTo:any[];
    private selectedCorespondence:any[];
////
     private showSelectBox: String = "single";
     private DocumentFromSelectBox: String = "single";
     private DocumentToshowSelectBox: String = "single";
     private CorespondenceshowSelectBox: String = "single";
	 empty : any;
  constructor(
	private _router: Router,
	private searchLevels: SearchService,
	private userService: UserService,
	private searchService: SearchService,
	private sharedService: SharedService,
	private browseService: BrowseService,
	private workflowService: WorkflowService,
	private recipientsService: RecipientsService, 
	private browseSharedService: BrowseSharedService
  ) {
	this.dateObj.setDate(this.dateObj.getDate()+1)
	this.years=this.dateObj.getFullYear();
 	this.months=this.dateObj.getMonth()+1; 
	this.days=this.dateObj.getDate();
	this.userFromSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
	this.userToSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
	this.userSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
	this.current_user = this.userService.getCurrentUser();
	this.current_user_name = this.userService.getCurrentUser().employeeName;
	this.current_user_login_name = this.userService.getCurrentUser().employeeLogin;
	this.to = "";
	this.from = "";
		
	var json = this.userService.getCurrentDelegatedForEmployeeLogin();
	if(json =="")
	{
		this.delegateForEmployeeLogin = "";
	}else{
		this.delegateForEmployeeLogin = JSON.parse(json);
	}
	//this.simpleSearchRequestObject.crossDepartment = false;
	this.userService.notifyDelegateForChange.subscribe(data => this.setDelegate(data));
	
	this.workflowService.getLaunchInstructions().subscribe(data => this.instructions = data);
	//this.recipientsService.getDocumentType().subscribe(data=> this.documentType = data);
	this.recipientsService.getDocumentType().subscribe(data=> this.populateDocumentType(data));

	 this.populateCorrespondance();
	 if(localStorage.getItem('searchTerm')){
		 var token = localStorage.getItem('searchTerm');
		 this.simpleWorkflowSearch.subject = decodeURIComponent(token);
	 }else
		 this.simpleWorkflowSearch.subject = "";
		 
	console.log("CrossDepertment checked :: "+localStorage.getItem('isThisForCrossDept'));
	if(localStorage.getItem('isThisForCrossDept')){
		var flag = localStorage.getItem('isThisForCrossDept');
		if(flag == 'true')
			this.simpleSearchRequestObject.crossDepartment = true;
	}
  }

    
   populateDocumentType(data: any ) {
	this.documentType = data;
       this.docTypeList = [];
        for(let i=0;i<data.length;i++){
          this.docTypeList.push({label:data[i].docType,value:data[i].id});
           
        }
    }  
     populatedocument_from(data: any ) {
       this.populatedocumentfrom = [];
        this.document_from = data;
        for(let i=0;i<data.length;i++){
             this.populatedocumentfrom.push({label:data[i].siteName,value:data[i].siteId});
           
        }
    }
    
    populateCorrespondance()
    {
        this.populateCorespondence=[];
        this.populateCorespondence.push({label:"Internal Incoming",value:"1"});
        this.populateCorespondence.push({label:"Internal Outgoing",value:"2"});
        this.populateCorespondence.push({label:"External Incoming",value:"3"});
		this.populateCorespondence.push({label:"External Outgoing",value:"4"});
        
    }
    
  ngOnInit() {
	  console.log("in ng init ()");
	this.browseSharedService.emitchangeEvent$.subscribe(response => this.ShowMessage(response));
	document.getElementById("errortext_search").style.display = "none";
	document.getElementById("displayfade_in").style.display = "none";
	setTimeout(function() {
		if(localStorage.getItem('searchedCount')) {
			let c = parseInt(localStorage.getItem('searchedCount'))
			if(c<1) {
				document.getElementById('errormessage').style.display = "block"
				document.getElementById("errortext_search").style.display = "block";
			} else {
				document.getElementById('errormessage').style.display = "none"
				document.getElementById("errortext_search").style.display = "none";
			}
			localStorage.removeItem('searchedCount')
		}
	},2000)

	 this.initializeSimpleSearchFilter();

	 setTimeout(() => {
		console.log("in ngInInit() :; this.userService.getCurrentDelegatedUserOrCurrentUserLogin() ;:: ",this.userService.getCurrentDelegatedUserOrCurrentUserLogin())
	 }, 1000);
  }


  initializeSimpleSearchFilter() {
	this.workflowService.simpleSearchFilter = localStorage.getItem('searchFilter');
	if(!this.workflowService.simpleSearchFilter) {
		this.workflowService.simpleSearchFilter = 'EmailSubject';
		this.workflowService.placeHolder = 'Subject';
	} else if(this.workflowService.simpleSearchFilter == 'DocumentID') {
		this.workflowService.placeHolder = 'Document ID';
	} else if(this.workflowService.simpleSearchFilter == 'ReferenceNo') {
		this.workflowService.placeHolder = 'Reference Number';
	} else if(this.workflowService.simpleSearchFilter == 'EmailSubject') {
		this.workflowService.placeHolder = 'Subject';
	}  else if(this.workflowService.simpleSearchFilter == 'DocumentDate') {
		this.workflowService.placeHolder = 'Document Date (Please provide date in DD/MM/YYYY format)';
	}  else if(this.workflowService.simpleSearchFilter == 'Content') {
		this.workflowService.placeHolder = 'Content Search';
	} else {
		this.workflowService.simpleSearchFilter = 'EmailSubject';
		this.workflowService.placeHolder = 'Subject';
	}
	
  }
 /* ShowMessage(response:any) {
	if (response.status == 200) {
	  this.notification_message = response._body != null ? response._body : "Action successfull";
	}
	else {
	  this.error_message = response._body != null ? response._body : "Action could not be performed";
	}
  }*/
	 ShowMessage(response:any) {
		if (response.status == 200) {
		  this.notification_message = response._body != null ? response._body : "Action successfully";
			setTimeout(function() { // AMZ change commented out
			$('#hidenotification').fadeOut('fast');
			}, 5000);
			setTimeout(function(){
			$('#hidenotification').fadeIn('slow');
		},1000); 
		}
		else {
		  this.error_message = response._body != null ? response._body : "Action could not be performed";
		}
  }

  	removeMessage() {
			this.notification_message = "";
			this.error_message = "";
			}

	assigndepartmentIDandSearch(data){
		this.getSampleSearch(data.employeeDepartment.departmentCode);
	}

	search() {
		if(this.userService.getCurrentDelegatedForEmployeeLogin()){
			this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assigndepartmentIDandSearch(data));
		}else{
			this.getSampleSearch(this.userService.getCurrentUser().employeeDepartment.departmentCode);
		}

	}

	getSampleSearch(departmentID :any)
	{
		if( this.ticks>0){
			this.sub.unsubscribe();
			
		}
		setTimeout( function() {}, 300 );
		this.filter = new LaunchWorkflowSearchFilter();
		this.filter.filterName = this.workflowService.simpleSearchFilter;
		if(this.filter.filterName =='DocumentDate'){
			var condtion=this.dateValidation(this.simpleWorkflowSearch.subject);
			if(condtion)
			{
				alert(" Date format is invalid (Please provide date in DD/MM/YYYY format)");
			   return false;
			 }  
		}
		if(this.filter.filterName =='ReferenceNo'){
				this.filter.filterCondition = "Like"
			}
		if(this.filter.filterName =='DocumentID'){
			this.filter.filterCondition = "="   
		}
		if(this.filter.filterName =='DocumentDate'){
			this.filter.filterCondition = "="   
		}
		if(this.filter.filterName =='EmailSubject'){
			this.filter.filterCondition = "Like"   
		}
		if (this.filter.filterName == "DocumentDate") {
			this.filter.filterDataType = "Date";
		} else {
			this.filter.filterDataType = "string";
		}
		this.filter.filterValue = encodeURIComponent(this.simpleWorkflowSearch.subject);

		if(this.filter.filterValue && this.dimsTrim(this.filter.filterValue).length < 4)
		return false;

		document.getElementById("searchButton").disabled = true;  
		document.getElementById("displayfade_in").style.display = "block"; 
		document.getElementById("timeelapsedmain").style.display = "block";
		document.getElementById("errormessage").style.display = "block";
		var isThisForCrossDept = false;
		isThisForCrossDept = this.simpleSearchRequestObject.crossDepartment;
		if(isThisForCrossDept){
			document.getElementById("crossdeptextmain").style.display = "block";
			localStorage.setItem('isThisForCrossDept', "true");
		}else{
			localStorage.setItem('isThisForCrossDept', "false");
		}
		this.ticks = 0;
		this.timer = Observable.timer(1000,1000);
		// subscribing to a observable returns a subscription object
		this.sub = this.timer.subscribe(t => this.tickerFunc(t));
		document.getElementById("loader_main_search").style.display = "block";
		document.getElementById("errortext_search").style.display = "none";
		
		if(this.filter.filterName =='Content'){
			this.simpleSearchRequestObject.content =  this.filter.filterValue
		} else {
			if(this.workflowService.simpleSearchFilter === "EmailSubject") { // AMZ change 
				localStorage.setItem('searchTerm', this.filter.filterValue)
			}else if(this.workflowService.simpleSearchFilter === "DocumentDate") { // AMZ change 
				localStorage.setItem('searchTerm', this.filter.filterValue)
			}else if(this.workflowService.simpleSearchFilter === "DocumentID") { // AMZ change 
				localStorage.setItem('searchTerm', this.filter.filterValue)
			}else if(this.workflowService.simpleSearchFilter === "ReferenceNo") { // AMZ change 
				localStorage.setItem('searchTerm', this.filter.filterValue)
			}else if(this.workflowService.simpleSearchFilter === "Content Search") { // AMZ change 
				localStorage.setItem('searchTerm', this.filter.filterValue)
			}else {
				localStorage.removeItem('searchTerm');
			}
			this.simpleSearchRequestObject.filter.push(this.filter);
		}
		this.simpleSearchRequestObject.pageNo = 1;
		this.simpleSearchRequestObject.pageSize = 100;
		this.simpleSearchRequestObject.departmentId = departmentID;
	 	localStorage.setItem('searchedDocuments', JSON.stringify(this.simpleSearchRequestObject));
	
		this.browseService.SearchDocuments(this.simpleSearchRequestObject).subscribe(data => this.doSearch(data),error=>this.checkerror(error));
		this.simpleSearchRequestObject = new SimpleSearchRequestObject();
	  }
	
    doSearch(data) {
      this.simpleWorkflowSearch = new TaskSimpleSearch();
        this.emitSearchedDocuments(data)
    }


	searchPlaceholderSet(placeHolder: any, filter: any) {
		if(placeHolder==='Document Date')
			this.workflowService.placeHolder = placeHolder+" (Please provide date in DD/MM/YYYY format)";
		else
			this.workflowService.placeHolder = placeHolder+"";
			//this.placeHolder = placeHolder+" (Only 2 years documents searched. Use advance search for more)";
		this.workflowService.simpleSearchFilter = filter;
		localStorage.setItem('searchFilter', filter);
	}

  crossDepartmentSelection(event) {
	this.simpleSearchRequestObject.crossDepartment = event.currentTarget.checked;
  }
  tickerFunc(tick){
		this.ticks = tick
		
	}


advanceSearchDocuments(searchModal: any, $event: any) {
	console.log("test test advanceSearchDocuments");
this.showSelectBox = "single";
             this.DocumentFromSelectBox= "single";
             this.CorespondenceshowSelectBox= "single";
             this.DocumentToshowSelectBox= "single";
	let count: number = 0;
	
	this.documentAdvanceSearchRequestObject = new AdvanceSearchRequestObject()
	/*for (let inputField of [].slice.call($event.target.getElementsByTagName('input'))) {
	  if (inputField.getAttribute('type') != "file") {
		this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		for (let selectField of [].slice.call($event.target.getElementsByTagName('select'))){
			if(selectField.id == this.advanceSearchApiValues[count].propertyName){
				if(selectField.options.selectedIndex != -1){
					this.documentAdvanceSearchFilter.filterCondition = selectField.options[selectField.options.selectedIndex].value;
				}
			}
		}
		this.documentAdvanceSearchFilter.filterName = this.advanceSearchApiValues[count].propertyName;
		this.documentAdvanceSearchFilter.filterDataType = this.advanceSearchApiValues[count++].datatype;
		this.documentAdvanceSearchFilter.filterValue = encodeURIComponent(inputField.value);
		this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter)
	  }
	}*/
	
	for (let inputField of [].slice.call($event.target.getElementsByTagName('input'))) {
		if (inputField.getAttribute('type') != "file") {
			this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
			for( let advanceSearchApiValue of this.advanceSearchApiValues){
				if(inputField.name !=undefined && inputField.name == advanceSearchApiValue.propertyName){
					
					if(document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex != -1){
						this.documentAdvanceSearchFilter.filterCondition = document.getElementById(advanceSearchApiValue.propertyName).options[document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex].value;
					}
					this.documentAdvanceSearchFilter.filterValue = encodeURIComponent(inputField.value);
					this.documentAdvanceSearchFilter.filterName = advanceSearchApiValue.propertyName;
					this.documentAdvanceSearchFilter.filterDataType = advanceSearchApiValue.datatype;
					this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
				}
			}
	  	}
	}
	
	let counter: number = 0;
	for (let selectField of [].slice.call($event.target.getElementsByTagName('select'))) {
		this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		
		for( let advanceSearchApiValue of this.advanceSearchApiValues){
			if(selectField.name !=undefined && selectField.name == advanceSearchApiValue.propertyName){
				if(selectField.options.selectedIndex != -1){
					if(document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex != -1){
						this.documentAdvanceSearchFilter.filterCondition = document.getElementById(advanceSearchApiValue.propertyName).options[document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex].value;
					}
					this.documentAdvanceSearchFilter.filterValue = selectField.options[selectField.options.selectedIndex].value;
					this.documentAdvanceSearchFilter.filterName = advanceSearchApiValue.propertyName;
					this.documentAdvanceSearchFilter.filterDataType = advanceSearchApiValue.datatype;
					this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
				}
			}
		}
	}

	
for (let selectField of [].slice.call($event.target.getElementsByTagName('p-multiSelect'))) {
            var value="";
             var ngModule=[];
   
                      console.log("++++++++++++++selected",selectField.id)
                                   console.log("++++++++++++++selected",selectField.id.split("-"))

                      console.log("++++++++++++++selected",selectField.id.split("-")[1])
             var tagaNme=selectField.id.split("-")[1];

            console.log("++++++++++++++selected",tagaNme);


             //alert(selectField.id);
           
          
              console.log("this.selected",this.selected);
                         

             
             
             console.log("advanceSearchApiValues",this.advanceSearchApiValues);
           
          
        this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
        
        for( let advanceSearchApiValue of this.advanceSearchApiValues){
             value="";
                         console.log("advanceSearchApiValue.propertyName--",advanceSearchApiValue.propertyName);

            if(tagaNme== advanceSearchApiValue.propertyName){
               
                if("DocumentType"==advanceSearchApiValue.propertyName)
                      {
                    
                       ngModule=this.selected;
                       }
                     else if("DocumentFrom"==advanceSearchApiValue.propertyName)
                       {
                     
                       ngModule=this.selectedDocumentFrom;
                       }
                     else if("DocumentTo"==advanceSearchApiValue.propertyName)
                     {
                    
                       ngModule=this.selectedDocumentTo;
                       }
                     else if("CorrespondenceType"==advanceSearchApiValue.propertyName)
                      {
                    console.log("this.selectedCorespondence",this.selectedCorespondence);
                 
                       ngModule=this.selectedCorespondence;
                    console.log("ngModule",ngModule);
                       }
                     
                for(let value1 of ngModule)
                {
                 if(value=="")
                 {
                   value=value1; 
                 }
                 else
                     {
                value=value+","+value1;
                     }
                   }
                console.log("---------------",value);
                console.log("---------------",value=="");
              
                    if(value==""){
                            
                }
                else
                        {
                        
                    
                    if(document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex != -1){

                      this.documentAdvanceSearchFilter.filterCondition = document.getElementById(advanceSearchApiValue.propertyName).options[document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex].value;
                    }
                     console.log("valueis",value);
                    
                    this.documentAdvanceSearchFilter.filterValue = value;
                    this.documentAdvanceSearchFilter.filterName = advanceSearchApiValue.propertyName;
                    this.documentAdvanceSearchFilter.filterDataType = advanceSearchApiValue.datatype;
                    this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
                 
                        }
           }
        }


        }
	 
	for (let selectField of [].slice.call($event.target.getElementsByTagName('my-date-picker'))) {
		this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		
		for( let advanceSearchApiValue of this.advanceSearchApiValues){
			if(selectField.id !=undefined && (selectField.id == advanceSearchApiValue.propertyName+"datePicker" || selectField.id == advanceSearchApiValue.propertyName+"dateRange")){
				if(document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex != -1){
					this.documentAdvanceSearchFilter.filterCondition = document.getElementById(advanceSearchApiValue.propertyName).options[document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex].value;
				}
				this.documentAdvanceSearchFilter.filterValue = selectField.childNodes[0].children[0].children[0].value;
				this.documentAdvanceSearchFilter.filterName = advanceSearchApiValue.propertyName;
				this.documentAdvanceSearchFilter.filterDataType = advanceSearchApiValue.datatype;
				this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
			}
		}
	}
	  
	for (let selectField of [].slice.call($event.target.getElementsByTagName('my-date-range-picker'))) {
		this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		
		for( let advanceSearchApiValue of this.advanceSearchApiValues){
			if(selectField.id !=undefined && (selectField.id == advanceSearchApiValue.propertyName+"datePicker" || selectField.id == advanceSearchApiValue.propertyName+"dateRange")){
				if(document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex != -1){
					this.documentAdvanceSearchFilter.filterCondition = document.getElementById(advanceSearchApiValue.propertyName).options[document.getElementById(advanceSearchApiValue.propertyName).options.selectedIndex].value;
				}
				var dateRange = selectField.childNodes[0].children[0].children[0].value.split("-");
				if(dateRange.length>1){
					var appendedRange = dateRange[0].concat("~!",dateRange[1]);
					console.log(appendedRange);
					this.documentAdvanceSearchFilter.filterValue = appendedRange;
				}else{
					this.documentAdvanceSearchFilter.filterValue = "";
				}
				this.documentAdvanceSearchFilter.filterName = advanceSearchApiValue.propertyName;
				this.documentAdvanceSearchFilter.filterDataType = advanceSearchApiValue.datatype;
				this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
			}
		}
	}
	  
	if(document.getElementById("addedByMe").checked==true) {
		this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		this.documentAdvanceSearchFilter.filterCondition = "is equal to";
		console.log("current_user_login_name :: ",this.current_user_login_name);
		this.documentAdvanceSearchFilter.filterValue = this.current_user_login_name;
		this.documentAdvanceSearchFilter.filterName = "Creator";
		this.documentAdvanceSearchFilter.filterDataType = "STRING";
		this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter); 
document.getElementById("addedByMe").checked=false;
	}
	if(document.getElementById("documentCreatorValue").value!=""){
		this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		if(document.getElementById("documentCreatorCondition").options.selectedIndex != -1){
			this.documentAdvanceSearchFilter.filterCondition = document.getElementById("documentCreatorCondition").options[document.getElementById("documentCreatorCondition").options.selectedIndex].value;
		}
		this.documentAdvanceSearchFilter.filterValue = this.creator;
		this.documentAdvanceSearchFilter.filterName = "Creator";
		this.documentAdvanceSearchFilter.filterDataType = "STRING";
		this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
document.getElementById('documentCreatorValue').value = "";
	}
	  
	if(document.getElementById("contentSearch").value!=""){
		/*this.documentAdvanceSearchFilter = new LaunchWorkflowSearchFilter();
		if(document.getElementById("contentSearchCondition").options.selectedIndex != -1){
				this.documentAdvanceSearchFilter.filterCondition = document.getElementById("contentSearchCondition").options[document.getElementById("contentSearchCondition").options.selectedIndex].value;
		}
		this.documentAdvanceSearchFilter.filterValue = document.getElementById("contentSearch").value;
		this.documentAdvanceSearchFilter.filterName = "contentSearch";
		this.documentAdvanceSearchFilter.filterDataType = "STRING";
		this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);*/
		this.documentAdvanceSearchRequestObject.content =  document.getElementById("contentSearch").value;
document.getElementById('contentSearch').value = "";
	}
	  
	var flag = true;
	var chSize = 0;
	for(let filter of this.documentAdvanceSearchRequestObject.filter){
		if(filter.filterValue && this.dimsTrim(filter.filterValue) !="") {
			flag = false;
			if(this.dimsTrim(filter.filterValue).length > chSize) {
				chSize = this.dimsTrim(filter.filterValue).length;
			}
			if((filter.filterName == 'Confidentiality') || (filter.filterName == 'DepartmentID')
				|| (filter.filterName == 'DMail') || (filter.filterName == 'CorrespondenceType') 
				|| (filter.filterName == 'DocumentType') || (filter.filterName == 'IsLaunched')
				|| (filter.filterName == 'DocumentFrom') || (filter.filterName == 'DocumentTo'))
				chSize = 4;
			if(chSize >= 4)
				break;
		 }
	}
	if(this.documentAdvanceSearchRequestObject.content != "") {
		 flag = false;
		 if(chSize < this.dimsTrim(this.documentAdvanceSearchRequestObject.content).length) {
		 	chSize = this.dimsTrim(this.documentAdvanceSearchRequestObject.content).length
		 }
	}
	if(flag){
		  document.getElementById("advFilterMsg").style.display ="block";
		  return;
	}else{
		if(chSize < 4) {
			document.getElementById("advFilterMsg").style.display ="block";
			document.getElementById("advFilterMsg").innerText = "Please provide the search text of atleast 4 characters"
		  	return;
		}
		document.getElementById("advFilterMsg").style.display ="none";
		document.getElementById("advanceSearchButton").disabled = true;
		if( this.ticks>0){
			this.sub.unsubscribe();
		}
		document.getElementById("displayfade_in").style.display = "block";
		document.getElementById("timeelapsedmain").style.display = "block";
		this.ticks = 0;
		this.timer = Observable.timer(1000,1000);
		// subscribing to a observable returns a subscription object
		this.sub = this.timer.subscribe(t => this.tickerFunc(t));
		document.getElementById("loader_main_search").style.display = "block";
	}
	console.log("this.current_user in ADVN ::: this.current_user :: ",this.current_user)
	this.documentAdvanceSearchRequestObject.departmentId = this.current_user.employeeDepartment.departmentCode;
	localStorage.setItem('searchedDocuments', JSON.stringify(this.documentAdvanceSearchRequestObject));
	this.browseService.AdvanceSearchDocuments(this.documentAdvanceSearchRequestObject).subscribe(data => this.emitSearchedDocuments(data),error => this.checkerror(error));
	searchModal.close();
	
	console.log("Last of method");
      this.selected=[];
    this.selectedDocumentFrom=[];
    this.selectedDocumentTo=[];
    this.selectedCorespondence=[];
	//Commented By Rameshwar
	//this._router.navigate(['/browse/document-search'])
}
  
  emitSearchedDocuments(data) {
	this.sharedService.emitDocuments(data)
	//Added by Rameshwar
	this.sharedService.setSearchedDocuments(data);
document.getElementById("crossdeptextmain").style.display = "none";
localStorage.setItem('searchedCount', data.length)
	if(data.length>0){
	  document.getElementById("loader_main_search").style.display = "none";
	  document.getElementById("timeelapsedmain").style.display = "none";
	  document.getElementById("advanceSearchButton").disabled = false;
	  document.getElementById("searchButton").disabled = false; 
	  document.getElementById("displayfade_in").style.display = "none";
	  document.getElementById("errortext_search").style.display = "none";
	  document.getElementById("errormessage").style.display = "none";
	}
	if(data.length==0){
	 document.getElementById("loader_main_search").style.display = "none";  
	 document.getElementById('errormessage').style.display = "block"
	 document.getElementById("errortext_search").style.display = "block";
	 document.getElementById("timeelapsedmain").style.display = "none";
	 document.getElementById("advanceSearchButton").disabled = false;
	 document.getElementById("searchButton").disabled = false; 
	 document.getElementById("displayfade_in").style.display = "none";
   }
	  this.sub.unsubscribe();
	  //this.settimeoutFunction(); AMZ Change
	  
	  //Added By Rameshwar
	//  this._router.navigate(['/browse/document-search']); 
	  this._router.navigateByUrl( '/work-flow/reload' );
      let self: any = this; 
      setTimeout( function() {  
          self._router.navigateByUrl( '/browse/document-search' );
          }, 2000 ); 
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
 /* advanceSearchTask(event, searchModal){
	  console.log("search Model :; ",searchModal)
	if(((this.workflowSearch.subject == undefined)||(this.workflowSearch.subject.trim().length < 4))
					&&((this.workflowSearch.comments == undefined)||(this.workflowSearch.comments.trim().length < 4))
					&&((this.workflowSearch.documentId == undefined)||(this.workflowSearch.documentId.trim().length < 4))
					&&this.tmpFromArray.length < 1 && this.tmpToArray.length < 1 && ((this.workflowSearch.referenceNumber == undefined)
					||(this.workflowSearch.referenceNumber.trim().length < 4))&&document.getElementById("instruction").options.selectedIndex == 0
					&&this.workflowSearch.recieveDate == undefined&&
					this.workflowSearch.dateType == undefined || this.workflowSearch.dateType == "")
		{ 
			this.form_empty = true;
		}
		else{ 
			this.form_empty = false;

			if(this.workflowSearch.dateType == null) {
				this.date_empty = true
			} else {
				this.date_empty = false

				if(this.workflowSearch.dateType == 'specific' || this.workflowSearch.dateType == 'before' || this.workflowSearch.dateType == 'after'){
					if(this.recieveDate == "") {
						this.adv_date_empty = true;
					} else {
						this.adv_date_empty = false;


			if(this.workflowSearch.subject != null) {
				this.workflowSearch.subject = this.workflowSearch.subject.trim()
				if(this.subjectExtraVal.length>0) {
					for(let val of this.subjectExtraVal) {
						this.workflowSearch.subject = this.workflowSearch.subject + '###' + val.trim();
					}
				}
			}
			if(this.workflowSearch.referenceNumber != null) {
				this.workflowSearch.referenceNumber = this.workflowSearch.referenceNumber.trim()
				if(this.refExtraVal.length>0) {
					for(let val of this.refExtraVal) {
						this.workflowSearch.referenceNumber = this.workflowSearch.referenceNumber + '###' + val.trim();
					}
				}
			}
			if(this.workflowSearch.documentId != null) {
				this.workflowSearch.documentId = this.workflowSearch.documentId.trim()
				if(this.idExtraVal.length>0) {
					for(let val of this.idExtraVal) {
						this.workflowSearch.documentId = this.workflowSearch.documentId + '###' + val.trim();
					}
				}
			}
			if(this.workflowSearch.comments != null) {
				this.workflowSearch.comments = this.workflowSearch.comments.trim();
				if(this.commentsExtraVal.length>0) {
					for(let val of this.commentsExtraVal) {
						this.workflowSearch.comments = this.workflowSearch.comments + '###' + val.trim();
					}
				}
			}
			if(this.workflowSearch.workFlowSearchType == undefined) {
				//this.errorMessage = "Please Define Type First"
				//document.getElementById("displayLoader").style.display="none";    			  
			} else {
                localStorage.setItem('workwfSearch', 'WorkWFSearch'); 
                console.log('Search Type before set   :::::::!',this.workflowSearch.workFlowSearchType);
		localStorage.setItem('workflowSearchType', this.workflowSearch.workFlowSearchType)
		
		this.workflowSearch.loginUser = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
		
		if(this.workflowSearch.dateType && this.workflowSearch.dateType.trim() !="") { 
			
			if(this.workflowSearch.dateType == "today" ||
			this.workflowSearch.dateType == "lastWeek" ||
			this.workflowSearch.dateType == "lastMonth" || 
			this.workflowSearch.dateType == "lastYear" ||
			this.workflowSearch.dateType == "all"){
				this.workflowSearch.dateType = this.workflowSearch.dateType.trim() +"~";
			}
			

			if(this.workflowSearch.dateType == "specific" || this.workflowSearch.dateType == "before" || this.workflowSearch.dateType == "after"){
				this.workflowSearch.dateType = this.workflowSearch.dateType.trim() +"~" + this.recieveDate.formatted;
			}

			if(this.workflowSearch.dateType == "range"){
				this.workflowSearch.dateType = this.workflowSearch.dateType.trim() +"~" + this.dateFrom.formatted +'~'+this.dateTo.formatted
			}
			//this.workflowSearch.recieveDate = this.workflowSearch.dateType.trim() +"#####" + this.dateFrom.formatted +'#####'+this.dateTo.formatted
			
		}
		let tmpTo = ''
		this.tmpToArray.forEach((item, index) => {
			if(index == 0) {
				tmpTo = item;
			} else {
				tmpTo = tmpTo + '###' + item
			}
		});
		this.workflowSearch.toUser = tmpTo;
		let tmpFrom = ''
		this.tmpFromArray.forEach((item, index) => {
			if(index == 0) {
				tmpFrom = item;
			} else {
				tmpFrom = tmpFrom + '###' + item
			}
		});
		this.workflowSearch.fromUser = tmpFrom;
		this.workflowSearch.pageSize = 10;
		this.workflowSearch.pageNo = 1;
		if(document.getElementById("instruction").options.selectedIndex != -1){
			this.workflowSearch.instruction = document.getElementById("instruction").options[document.getElementById("instruction").options.selectedIndex].value;
		}
		localStorage.setItem('searchedWorkflowsP', JSON.stringify(this.workflowSearch))	
		this.SearchTaskItems(this.workflowSearch, searchModal)
		event.preventDefault();
		  }
	  }
					}
				}
	}
  }*/

  
advanceSearchTask(event, searchModal){
	//alert(this.workflowSearch.dateType + "start")
  if(((this.workflowSearch.subject == undefined)||(this.workflowSearch.subject.trim().length < 4))
	  &&((this.workflowSearch.comments == undefined)||(this.workflowSearch.comments.trim().length < 4))
	  &&((this.workflowSearch.documentId == undefined)||(this.workflowSearch.documentId.trim().length < 4))
	  &&((this.workflowSearch.filterBy == undefined)||(this.workflowSearch.filterBy.trim().length < 2))
	  &&this.tmpFromArray.length < 1 && this.tmpToArray.length < 1 && ((this.workflowSearch.referenceNumber == undefined)||
	  (this.workflowSearch.referenceNumber.trim().length < 4))&&document.getElementById("instruction").options.selectedIndex == 0
	  &&((this.workflowSearch.dateType==undefined)||(this.workflowSearch.dateType=='')))
	  { 
		  //alert("second")
		  this.empty = "form";
		  
	  }
	  else{
		  if((this.workflowSearch.dateType=="specific"||this.workflowSearch.dateType=="before"||this.workflowSearch.dateType=="after")
		  &&this.workflowSearch.recieveDate==undefined){
			  //alert(this.workflowSearch.dateType + "date first")
			  this.empty = "date";
	  //str
		  }else{
			  //alert("else second")
			  if((this.workflowSearch.dateType=="range"&&this.dateFrom == '')||(this.workflowSearch.dateType=="range"&&this.dateTo == '')){
				  //alert("last")
				  this.empty = "date";
			  }
			  else{
				  //alert("last last")
				  
	  //	setTimeout(function() { // AMZ change commented out
		  //	document.getElementById("displayLoader").style.display="block";    
		  //}, 3000);
		  this.empty = ""
		  if(this.workflowSearch.subject != null) {
			  this.workflowSearch.subject = this.workflowSearch.subject.trim()
			  if(this.subjectExtraVal.length>0) {
				  for(let val of this.subjectExtraVal) {
					  this.workflowSearch.subject = this.workflowSearch.subject + '###' + val.trim();
				  }
			  }
		  }
		  if(this.workflowSearch.referenceNumber != null) {
			  this.workflowSearch.referenceNumber = this.workflowSearch.referenceNumber.trim()
			  if(this.refExtraVal.length>0) {
				  for(let val of this.refExtraVal) {
					  this.workflowSearch.referenceNumber = this.workflowSearch.referenceNumber + '###' + val.trim();
				  }
			  }
		  }
		  if(this.workflowSearch.documentId != null) {
			  this.workflowSearch.documentId = this.workflowSearch.documentId.trim()
			  if(this.idExtraVal.length>0) {
				  for(let val of this.idExtraVal) {
					  this.workflowSearch.documentId = this.workflowSearch.documentId + '###' + val.trim();
				  }
			  }
		  }
		  if(this.workflowSearch.comments != null) {
			  this.workflowSearch.comments = this.workflowSearch.comments.trim();
			  if(this.commentsExtraVal.length>0) {
				  for(let val of this.commentsExtraVal) {
					  this.workflowSearch.comments = this.workflowSearch.comments + '###' + val.trim();
				  }
			  }
		  }
		  if(this.workflowSearch.workFlowSearchType == undefined) {
			  //this.errorMessage = "Please Define Type First"
			  //document.getElementById("displayLoader").style.display="none";    			  
		  } else {
			localStorage.setItem('workwfSearch', 'WorkWFSearch'); 
	  localStorage.setItem('workflowSearchType', this.workflowSearch.workFlowSearchType)
	  
	  this.workflowSearch.loginUser = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
	  
	  if(this.workflowSearch.dateType && this.workflowSearch.dateType.trim() !="") { 
		  
		  if(this.workflowSearch.dateType == "today" ||
		  this.workflowSearch.dateType == "lastWeek" ||
		  this.workflowSearch.dateType == "lastMonth" || 
		  this.workflowSearch.dateType == "lastYear" ||
		  this.workflowSearch.dateType == "all"){
			  this.workflowSearch.dateType = this.workflowSearch.dateType.trim() +"~";
		  }
		  

		  if(this.workflowSearch.dateType == "specific" || this.workflowSearch.dateType == "before" || this.workflowSearch.dateType == "after"){
			  //alert(this.workflowSearch.recieveDate.formatted)
			  this.workflowSearch.dateType = this.workflowSearch.dateType.trim() +"~" + this.workflowSearch.recieveDate.formatted;
		  }

		  if(this.workflowSearch.dateType == "range"){
			  this.workflowSearch.dateType = this.workflowSearch.dateType.trim() +"~" + this.dateFrom.formatted +'~'+this.dateTo.formatted
		  }
		  //this.workflowSearch.recieveDate = this.workflowSearch.dateType.trim() +"#####" + this.dateFrom.formatted +'#####'+this.dateTo.formatted
		  
	  }
	  let tmpTo = ''
	  this.tmpToArray.forEach((item, index) => {
		  if(index == 0) {
			  tmpTo = item;
		  } else {
			  tmpTo = tmpTo + '###' + item
		  }
	  });
	  this.workflowSearch.toUser = tmpTo;
	  let tmpFrom = ''
	  this.tmpFromArray.forEach((item, index) => {
		  if(index == 0) {
			  tmpFrom = item;
		  } else {
			  tmpFrom = tmpFrom + '###' + item
		  }
	  });
	  this.workflowSearch.fromUser = tmpFrom;
	  this.workflowSearch.pageSize = 20;
	  this.workflowSearch.pageNo = 1;
	  if(document.getElementById("instruction").options.selectedIndex != -1){
		  this.workflowSearch.instruction = document.getElementById("instruction").options[document.getElementById("instruction").options.selectedIndex].value;
	  }
	  localStorage.setItem('searchedWorkflowsP', JSON.stringify(this.workflowSearch))	
	  this.SearchTaskItems(this.workflowSearch, searchModal)
	  event.preventDefault();
		}
	  }
	}
}
}


  SearchTaskItems(workflowSearch, searchModal) {
	document.getElementById("displayLoader").style.display="block";
	this.searchService.getSearchTaskItems(workflowSearch).subscribe(data => this.emitSearchedTaskItems(data));
	this.to = "";
	this.from = "";
	this.tmpToArray = []
	this.tmpFromArray = []
	this.workflowSearch.recieveDate = ""
	this.workflowSearch = new TaskAdvanceSearch();
	this.tmpToArray = []
	this.tmpFromArray = []
	this.subjectExtra= []
	this.subjectExtraVal = []
	this.refExtra= []
	this.refExtraVal = []
	this.idExtra= []
	this.idExtraVal = []
	this.commentsExtra= []
	this.commentsExtraVal = []
	searchModal.close();
	this._router.navigateByUrl('/work-flow/task-search')
	// set input fields in model to empty
	document.getElementById("instruction").options.selectedIndex = -1;
	this.workflowSearch.recieveDate = '';
	this.dateFrom = '';
	this.dateTo = '';
  }

  emitSearchedTaskItems(data) {
	this.taskItems = data;
	document.getElementById("displayLoader").style.display="none"; 
	this.sharedService.emitTasks(this.taskItems)
  }

  openSearchModal(searchModal) {
		if(this.userService.getCurrentDelegatedForEmployeeLogin()){
			console.log("this.userService.getCurrentDelegatedForEmployeeLogin() ::",this.userService.getCurrentDelegatedForEmployeeLogin())
			this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assignUserName(data));
		}else{
			console.log("the currect user :: :::  in else Block :: ",this.userService.getCurrentUser());
			this.current_user = this.userService.getCurrentUser();
			this.current_user_login_name = this.current_user.employeeLogin;
			this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.populatedocument_from( data));
			this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.departments = data);
			this.recipientsService.getUsersForFilter('All', this.current_user.employeeLogin).subscribe(data => this.populateFilterUser(data));
			
		}
		this.workflowService.getAdvanceSearchProperties().subscribe(data => this.openAdvanceSearchModel(searchModal, data));
  }

  
assignUserName(data){
	console.log("assignUserName  ::: ",data)
	this.current_user = data;
	this.current_user_login_name = this.userService.getCurrentUser().employeeLogin;
	this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.populatedocument_from( data));
	this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.departments = data);
	this.recipientsService.getUsersForFilter('All', this.current_user.employeeLogin).subscribe(data => this.populateFilterUser(data));
}


  advanceSearchApiValues: any = [];
  openAdvanceSearchModel(searchModal: any, advanceSearchProperties: any) {
	this.activeTab = "documents"
	this.workflowSearch = new TaskAdvanceSearch();
	this.documentSearchForm = new AdvanceSearchForm(advanceSearchProperties);
	this.advanceSearchApiValues = advanceSearchProperties;
	searchModal.open();
	searchModal.modalClass="modal-lg";
  }

  /*getHomePage(){ 
	this._router.navigate(['work-flow/inbox']);
  }*/


  logout() {
	this.userService.removeUserCookies().subscribe(data =>data);
	localStorage.clear();
	this._router.navigate(["my-login"])
  }


  levels: Array<Object> = this.searchLevels.getSearchLevels();

  classes: Array<Object> = this.searchLevels.getSearchClasses();

  checkerror(error){
	if(!(error.status==200)){ 
		document.getElementById("displayfade_in").style.display = "none";
	}
	 document.getElementById("timeelapsedmain").style.display = "none"; 
	 document.getElementById("loader_main_search").style.display = "none";
	 document.getElementById("errortext_search").style.display = "block";
	 document.getElementById("advanceSearchButton").disabled = false;
	 document.getElementById("searchButton").disabled = false; 
	 this.sub.unsubscribe();
	 //this.settimeoutFunction(); AMZ Change
  } 
	
	settimeoutFunction(){
		setTimeout(function() {
			 document.getElementById("errortext_search").style.display = "none";
		 }.bind(this), 2000);
		}
	
	isNumber(evt: any) {
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
			return false;
		}
		return true;
	}
	
	setDelegate(data){
		var json = data;
		if(json =="")
		{
			this.delegateForEmployeeLogin = "";
		}else{
			this.delegateForEmployeeLogin = JSON.parse(json);
		}
	}
	
	setToName(data) {
	if (data && data.employeeLogin) {
	  this.to = data.employeeLogin
	}
  }
	
	setFromName(data) {
	if (data && data.employeeLogin) {
	  this.from = data.employeeLogin
	}
  }
   setCreatorName(data) {
	   this.creator ="";
	if (data && data.employeeLogin) {
	  this.creator = data.employeeLogin
	}
  }
   
	updateFields(elementId : any){
		if(document.getElementById(elementId).value == 'period'){
			document.getElementById(elementId+"datePicker").style.display = "none";
			document.getElementById(elementId+"period").style.display = "block";
			document.getElementById(elementId+"dateRange").style.display = "none";
			
		}
		if(document.getElementById(elementId).value != 'period' && document.getElementById(elementId).value != 'between'){
			document.getElementById(elementId+"datePicker").style.display = "block";
			document.getElementById(elementId+"period").style.display = "none";
			document.getElementById(elementId+"dateRange").style.display = "none";
		}
		if(document.getElementById(elementId).value == 'between'){
			document.getElementById(elementId+"datePicker").style.display = "none";
			document.getElementById(elementId+"period").style.display = "none";
			document.getElementById(elementId+"dateRange").style.display = "block";
		}
	}
	
	addSubjectExtra() {
		if(this.subjectExtra.length<4) {
			this.subjectExtra.push(' ')
		}
	}

	removeSubjectExtra(i) {
		this.subjectExtra.splice(i,1)
		this.subjectExtraVal.splice(i,1)
		
	}

	onValueUpdateSubject(event, i) {
		console.log(event)
		this.subjectExtraVal[i] = event.target.value
	}

	addRefExtra() {
		if(this.refExtra.length<4) {
			this.refExtra.push(' ')
		}
	}

	removeRefExtra(i) {
		this.refExtra.splice(i,1)
		this.refExtraVal.splice(i,1)
		
	}

	onValueUpdateRef(event, i) {
		console.log(event)
		this.refExtraVal[i] = event.target.value
	}

	addIdExtra() {
		if(this.idExtra.length<4) {
			this.idExtra.push(' ')
		}
	}

	removeIdExtra(i) {
		this.idExtra.splice(i,1)
		this.idExtraVal.splice(i,1)
		
	}

	onValueUpdateId(event, i) {
		console.log(event)
		this.idExtraVal[i] = event.target.value
	}

	addCommentsExtra() {
		if(this.commentsExtra.length<4) {
			this.commentsExtra.push(' ')
		}
	}

	removeCommentsExtra(i) {
		this.commentsExtra.splice(i,1)
		this.commentsExtraVal.splice(i,1)
		
	}

	onValueUpdateComments(event, i) {
		console.log(event)
		this.commentsExtraVal[i] = event.target.value
	}

	onDateChanged(event: IMyDateModel) {
        this.myDatePickerOptions1 = {
			disableUntil: event.date,
			disableSince: {year:this.years, month:this.months, day:this.days},
			dateFormat: 'dd/mm/yyyy',
			showTodayBtn: false,
			editableDateField: true,
			markCurrentDay: true,
			showClearDateBtn: false,
			openSelectorOnInputClick: true
		}
    }

	closeTaskSearch(searchModal: any) {
		this.workflowSearch.recieveDate = ""
		this.workflowSearch = new TaskAdvanceSearch();
		this.tmpToArray = []
		this.tmpFromArray = []
		this.subjectExtra= []
		this.subjectExtraVal = []
		this.refExtra= []
		this.refExtraVal = []
		this.idExtra= []
		this.idExtraVal = []
		this.commentsExtra= []
		this.commentsExtraVal = []
		searchModal.close();
	}
	closeModal(searchModal : any){
		
	  this.showSelectBox = "single";
             this.DocumentFromSelectBox= "single";
          this.CorespondenceshowSelectBox= "single";
        this.DocumentToshowSelectBox= "single";
        
    this.selected=[];
    this.selectedDocumentFrom=[];
    this.selectedDocumentTo=[];
    this.selectedCorespondence=[];
        
	this.selected=[];
		if(document.getElementById("advFilterMsg") != undefined){
	document.getElementById("advFilterMsg").style.display ="none";
	}
		if(document.getElementById("documentCreatorValue") != undefined && document.getElementById("documentCreatorValue") !=null){
		document.getElementById('documentCreatorValue').value = "";
		}
		if(document.getElementById("contentSearch") != undefined && document.getElementById("contentSearch") !=null){
		document.getElementById('contentSearch').value = "";
		}
		if(document.getElementById("addedByMe") != undefined && document.getElementById("addedByMe") !=null){
		document.getElementById("addedByMe").checked=false;
		}
		searchModal.close();
	}
	
	dimsTrim(value: string) {
    	if(!value)
        	return "";
    	return decodeURIComponent(value).trim();
	}
	onChange(id,value){
        
        console.log("id---",id);
                console.log("----",value);

        if("DocumentType"==id)
        {
            
        if(value == "="){
            this.showSelectBox = "single";
        }else{
               this.selected=[];
            this.showSelectBox = "multi";
         
        }
            }
        else if("DocumentFrom"==id)
            {
           
            if(value == "="){
            this.DocumentFromSelectBox= "single";
                }
            else
                {
             this.DocumentFromSelectBox= "multi";
                }
            }
        else if("DocumentTo"==id)
        {
            
             if(value == "="){
            this.DocumentToshowSelectBox= "single";
                 }
            else
                 {
             this.DocumentToshowSelectBox= "multi";
                 }
            }
        else if("CorrespondenceType"==id)
        {
          
             if(value == "="){
            this.CorespondenceshowSelectBox= "single";
                 }
            else
                 {
             this.CorespondenceshowSelectBox= "multi";
                 }
        }
        
       
    }
	/*reset(){
		//document.getElementsByName("workflowFilterMsg").style.display = "none"
		this.form_empty = false;
		document.getElementById("instruction").options.selectedIndex = -1;
		this.dateFrom = '';
		this.dateTo = '';
		this.tmpToArray = []
		this.tmpFromArray = []
		this.to = "";
		this.from = "";
		this.workflowSearch = new TaskAdvanceSearch();
		this.subjectExtra= []
		this.subjectExtraVal = []
		this.refExtra= []
		this.refExtraVal = []
		this.idExtra= []
		this.idExtraVal = []
		this.commentsExtra= []
		this.commentsExtraVal = []
		this.workflowSearch.recieveDate = '';
	}*/

	
reset(){
	this.empty = "";
	document.getElementById("instruction").options.selectedIndex = -1;
	this.dateFrom = '';
	this.dateTo = '';
	this.tmpToArray = []
	this.tmpFromArray = []
	this.to = "";
	this.from = "";
	this.workflowSearch = new TaskAdvanceSearch();
	this.subjectExtra= []
	this.subjectExtraVal = []
	this.refExtra= []
	this.refExtraVal = []
	this.idExtra= []
	this.idExtraVal = []
	this.commentsExtra= []
	this.commentsExtraVal = []
	this.workflowSearch.recieveDate = '';
}  


	
	dateValidation(meetingDate)
	{
	console.log("dateValidation",meetingDate);
	var t = meetingDate.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
	console.log("tvaluesiisssss",t);
	if(t===null){
		console.log("falseConditionbegin1");
		return true;
			}
	var d=parseInt(t[1]), m=parseInt(t[2],10),y=parseInt(t[3],10);
	//below should be more acurate algorithm
	console.log("dvalue",d);
	console.log("mvalue",m);
	console.log("yvalue",y);
	if(m>=1 && m<=12 && d>=1 && d<=31){
		console.log("trueconditionbegin1");
			return false;  
	}
	console.log("falseCondition2");
	return true;
	}

	closeTaskSearch(searchModal: any) {
        this.workflowSearch.recieveDate = ""
        searchModal.close();
    }
	
}

import {ViewChild, ElementRef, Component, Input, Output, EventEmitter} from '@angular/core'
import {WorkflowService} from '../services/workflowServices/workflow.service'
import {SimpleSearchForm } from './../model/simpleSearchForm.model'
import {LaunchWorkflow} from './../model/launchWorkflow.model'
import {DocumentResult} from './../model/documentResult.model'

import {AdvanceSearchRequestObject} from './../model/advanceSearchRequestObject.model'
import {SimpleSearchRequestObject} from './../model/simpleSearchRequestObject.model'
import {LaunchWorkflowSearchFilter} from '../model/launchWorkflowSearchFilter.model'
import {AdvanceSearchForm} from './../model/advanceSearchForm.model'
import { Router } from '@angular/router'
import { AccordionModule } from "ng2-accordion";
import {UserService} from '../services/user.service'
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import {LaunchPrimaryDoc} from './../model/launchPrimaryDoc.model'
import { ModalModule } from "ng2-modal"
import {BrowseService} from '../services/browse.service'
import {LaunchService} from '../services/launch.service'
import { Observable, Subscription } from 'rxjs/Rx';
import {RecipientsService} from '../services/recipients.service'
import {Directorate} from '../model/recipients.model'
import {KNPCHierarchy} from '../model/KNPCHierarchyRecipients.model'
import {User} from '../model/userRecipients.model'
import {Department} from '../model/departmentRecipients.model'
import {Division} from '../model/divisionRecipients.model'

import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';
import {IMyDrpOptions, IMyDateRangeModel} from 'mydaterangepicker';
import {SelectItem} from 'primeng/primeng';
import global = require('./../global.variables')
import * as _ from 'lodash';

declare var $: any;
@Component({
	selector: 'advance-search-modal',
	providers: [WorkflowService, BrowseService, LaunchService, RecipientsService],
	templateUrl: 'app/SharedComponents/advanceDocSearchModal.component.html'

})

export class AdvanceDocSearchModal {
	@ViewChild('launchModal') launchModal;
	@ViewChild('userListThing') userListThing;	
	@ViewChild('divisionsThing2') divisionsThing2
	@ViewChild('divisionsThing') divisionsThing
	@ViewChild('crossDptThing') crossDptThing
	@ViewChild('hierarchyThing') hierarchyThing
	@Input() launch_popup_msg: string;
	@Output() selectedDocsList: EventEmitter<any> = new EventEmitter();
	@Output() notifytoSetAttachmentId: EventEmitter<any> = new EventEmitter();
	@Output() selectedTaskDetailDocsList: EventEmitter<any> = new EventEmitter();
	private userSearchUrl;
	private userListSearchUrl;
	private activeTab: string;
	private documentResults: DocumentResult[];
	private selected_docs: any;
	private  userSelected_docs: any[];
	private temp: any;
	private filter = new LaunchWorkflowSearchFilter();
	private simpleSearchForm = new SimpleSearchForm();
	private advanceSearchForm: AdvanceSearchForm;
	private simpleSearchRequestObject = new SimpleSearchRequestObject(); 
	private advanceSearchRequestObject = new AdvanceSearchRequestObject();
	private newTask = new LaunchWorkflow();
	private support_staff: any = [];
	public roles: any;

	private documentSearchForm = new AdvanceSearchForm();
	documentAdvanceSearchRequestObject: AdvanceSearchRequestObject;
	documentAdvanceSearchFilter: LaunchWorkflowSearchFilter;
	ticks = 0;
	private timer;
	private sub: Subscription;
	wradio_to: any = [];
	wradio_cc: any = [];
	wradio_none: any = [];
	public flag : any;
	public searchCrtieria : string = "";
	classes: Array<Object> = [];
	public directorates: any = [];
	public instructions: any = [];
	public priorities : any = ["Normal","Low","High"];
	public textComparisonOperators: string[] = ["is equal to","like", "contains", "starts with","ends with"];
	public numberComparisonOperators: string[] = ["=", "in", "not in"];
	public dateComparisonOperators: string[] = ["period","=",">", "<", "<=", ">=","between"];
	public equalOperator: string[] = ["="];
	public equalOperatorForDocuemntID: string[] = ["is equal to"];
	private myDatePickerOptionsDisabled: IMyOptions = global.date_picker_options_disabledpast  
	private myDatePickerOptions: IMyOptions = global.date_picker_options;
	private myDateRangePickerOptions: IMyDrpOptions  = global.date_range_option;
	launchModalHeading: any = "Launch Workflow";
	private divisions : any = [];
	private departments : any = [];
	private knpcHierarchy : any = [];
	private crossDepartment : any =[];
	private defaultUserList :any = [];
	private userList : any =[];
	private current_user: any;
	private currentDelegateForEmployeeLogin: string = "";
	attachmentFiles: any = [];
	designation:string; 
	private browseLaunchSubscription: any;
	private modalHeight :any;
	private heightFlag : boolean = true;
	private document_from : any
	private departmentsAdv :any;
	private base_url: string;
	private viewer_url : any;
	private os_name:any;
	private selectedAttachmentID: string;
	private documentType : any;
	private creatorSearchUrl;
	private creator : any;  
	private LaunchPrimaryDoc: LaunchPrimaryDoc;
	private totalCount : any;
	private disabled: boolean = false;
	
	private currentTab: any;
    
    private docTypeList:SelectItem[];
     private populatedocumentfrom:SelectItem[];
    private populateCorespondence:SelectItem[];
     

     private newArray:any[];
    private docTypes:any;

    
      private selected: any[];
    private selectedDocumentFrom:any[];
    private selectedDocumentTo:any[];
    private selectedCorespondence:any[];

    
     private showSelectBox: String = "single";
   
     private DocumentFromSelectBox: String = "single";
     private DocumentToshowSelectBox: String = "single";
	 private CorespondenceshowSelectBox: String = "single";
	 
	 public openFromLunchModel :boolean = false;
	 //added by Ravi Boni
	 public showMyUserList: Boolean= false;

	 //added by Ravi Boni 
	 public depts:any;
	 public globalSearchCrtieria: any;
	 public isGlobalKNPCSearch: any;
	 public recpntType: any;

	constructor(private router: Router, private workflowService: WorkflowService, private browseSharedService: BrowseSharedService,
	private browseService: BrowseService, private launchService: LaunchService,
	private recipientsService: RecipientsService, private userService: UserService) {
	let d: Date = new Date();
	//this.userSearchUrl = global.getUserList;
	this.activeTab = 'simpleSearch';
	this.selected_docs = [];
	this.documentResults = []; 
	//this.launchService.getLaunchClasses().subscribe(data => this.classes = data);
	this.recipientsService.getDirectorates().subscribe(data => this.directorates = data);
	this.workflowService.getLaunchInstructions().subscribe(data => this.instructions = data);
	this.base_url = global.base_url;
	this.viewer_url = global.viewer_url;
	this.os_name = global.os_name; 
	this.creatorSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
	this.current_user = this.userService.getCurrentUser();
	this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
	if (this.currentDelegateForEmployeeLogin == "") {
		this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
	}
	this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.departmentsAdv = data);
	this.recipientsService.getDocumentType().subscribe(data=> this.populateDocumentType(data));
    this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.populatedocument_from( data));
	this.populateCorrespondance();
    }
	
	defaultUserListonLoad(data){
		this.userList = data;
		console.log("userList  ::: "+this.userList)
		if(this.userList.length == 0) {
			this.showMyUserList = true;
			this.loadUsersForDivision(this.divisions[0],"");
			this.setAccordionAsClose();
		}
		let index = 0;
		for(let list of this.userList){
			if(index ==0){
				this.loadDeafultUserList(list,'');
			}
			index++;
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
    
    /////////////////onchange///////////
    
    onChange(id,value){
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
	
	assignUserSpecificData(data,msg: any, popupHeading: any){
		this.current_user = data;
		console.log("################################ IF #####&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& :: ",this.current_user.employeeLogin);
		this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
		this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
		this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
		this.openFromLunchModel = false;
		this.workflowService.getAdvanceSearchProperties().subscribe(data => this.openAdvanceSearchModel(msg, data, popupHeading))
	}
    
	openLaunchPopup(msg: any, popupHeading: any) {
		if(this.userService.getCurrentDelegatedForEmployeeLogin()){
			this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assignUserSpecificData(data,msg, popupHeading));
		}else{
			console.log("################################### ELSE ##&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& :: ",this.current_user.employeeLogin);
			this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
			this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
			this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
			this.openFromLunchModel = false;
			this.workflowService.getAdvanceSearchProperties().subscribe(data => this.openAdvanceSearchModel(msg, data, popupHeading))
		}
		
	}

	
	openLaunchPopupInLaunch(msg: any, popupHeading: any) {
		this.openFromLunchModel = true;
		this.workflowService.getAdvanceSearchProperties().subscribe(data => this.openAdvanceSearchModel(msg, data, popupHeading))
	}

	advanceSearchApiValues: any = [];
	openAdvanceSearchModel(msg: any, advanceSearchProperties: any, popupHeading: any) {
		this.activeTab = 'simpleSearch';
		$('.modal-backdrop').show();
		$("body").addClass("modal-open");
		this.simpleSearchForm = new SimpleSearchForm();
		this.documentSearchForm = new AdvanceSearchForm(advanceSearchProperties);
		this.advanceSearchApiValues = advanceSearchProperties;
		this.newTask = new LaunchWorkflow();
		if (popupHeading == "Search") {
			//this.rescale(this.launchModal,"small",this.activeTab);
			this.launchModalHeading = "Search";
			this.launch_popup_msg = msg;
			this.launchModal.modalClass ="";
			this.launchModal.open();
		} else {
			//this.rescale(this.launchModal,"small",this.activeTab);
			this.launchModal.modalClass ="";
			this.launchModal.open();
		}
	}

	addTosList(event,id: any) {
	if (event.employeeLogin != undefined) {
		if (this.wradio_to.indexOf(event.employeeLogin) == -1 && this.wradio_cc.indexOf(event.employeeLogin) == -1) {
		this.wradio_to.push(event.employeeLogin);
		setTimeout(() => {
			$(".launch_action_auto_complete_to").val("");
			document.getElementById("to").value = "";
			document.getElementById("to").focus();
		}, 100);
		}else{
			setTimeout(() => {
				$(".launch_action_auto_complete_to").val("");
				document.getElementById("to").value = "";
				document.getElementById("to").focus();
			}, 100);
		}
	}
	}

	addCcsList(event) {
	if (event.employeeLogin != undefined) {
		if (this.wradio_to.indexOf(event.employeeLogin) == -1 && this.wradio_cc.indexOf(event.employeeLogin) == -1) {
		this.wradio_cc.push(event.employeeLogin);
		setTimeout(() => {
			$(".launch_action_auto_complete_cc").val("");
			document.getElementById("cc").value = "";
			document.getElementById("cc").focus();
		}, 100);
		}else{
			setTimeout(() => {
				$(".launch_action_auto_complete_cc").val("");
				document.getElementById("cc").value = "";
				document.getElementById("cc").focus();
			}, 100);
		}
	}
	}


	removeSelectedDocsFromList(event: any, value: any, listType: any) {
	event.stopPropagation();
	if (listType == "attachment-List") {
		this.attachmentFiles.splice(this.attachmentFiles.indexOf(value), 1);
	}
	if (listType == "Doc-List") {
		if(this.selected_docs.length > 1){
			this.selected_docs.splice(this.selected_docs.indexOf(value), 1);
			this.selected_docs = this.selected_docs.slice();
		}  
	}
	}

	closeModel(){
		if(this.openFromLunchModel)
			this.switchTab(this.launchModal,'launchWorkflow')
		else 
			this.launchModal.close()
	}

	switchTab(launchModal: any, activeTab: any) {
	/*this.designation=this.current_user.employeeJobTitle;
	if(( this.designation=="DCEO"||this.designation=="TL"||this.designation=="MGR" || this.designation=="DIMS")){
		document.getElementById("crossdept").style.display = "block"; 
		document.getElementById("hierarchy").style.display = "block"; 
	
	}else{
		 document.getElementById("crossdept").style.display = "none"; 
	 document.getElementById("hierarchy").style.display = "none"; 
	}*/
	if (activeTab == "task_detail_launch_popup_msg") {
		this.browseSharedService.emitSearchedDocsFromRepository(this.selected_docs);
		launchModal.close();
	} else if (activeTab == "inbox_action_doc_search") {
		this.selectedDocsList.emit(this.selected_docs);
		launchModal.close();
	} else {
		if(this.selected_docs !=0){
			//Commented by Rameshwar and modifed 27102017
			//this.newTask.subject = this.selected_docs[0].symbolicName;
			this.newTask.subject = this.selected_docs[0].subject;
		}
if(localStorage.getItem('isThisFromWILaunch')){
//added for launch wi subject from existing witem
this.newTask.subject = localStorage.getItem('existingWISubject');
//localStorage.removeItem('existingWISubject');
//localStorage.removeItem('isThisFromWILaunch');
}
		if(activeTab == "launchWorkflow"||activeTab == "selectDocuments"){
		 launchModal.modalClass ="modal-lg";
		 //launchModal.contentEl.nativeElement.onload = this.rescale(launchModal,"large",activeTab);
		}else{
		 launchModal.modalClass ="";
		 //launchModal.contentEl.nativeElement.onload = this.rescale(launchModal,"small",activeTab);
		}
		if(activeTab == "advanceSearch") {
            launchModal.modalClass = "modal-lg"
        }
		this.activeTab = activeTab;
	}
	}
	
	rescale(launchModal :any, modalHeight: string,activeTab: any){
	var size = {width: $(window).width() , height: $(window).height() }
	/*CALCULATE SIZE*/
	var offset = 20;
	var offsetBody = 150;
	if(modalHeight == "large"){
		if(this.heightFlag){
			this.modalHeight = $('.modal-body').height();
			this.heightFlag = false;
		}
		
		$(launchModal).css('height', size.height - offset );
		$('.modal-body').css('height', size.height - (offset + offsetBody));
	}
	if(modalHeight == "small"){
		
		$(launchModal).css('height', size.height - offset );
		if(activeTab != "advanceSearch"){
			$('.modal-body').css('height', this.modalHeight);
		}
	}
	
	}
	
	advanceSearchDocuments(searchModal: any, $event: any) {
	
	   this.showSelectBox = "single";
             this.DocumentFromSelectBox= "single";
          this.CorespondenceshowSelectBox= "single";
        this.DocumentToshowSelectBox= "single";
	let count: number = 0;
	this.totalCount=null;
	this.documentAdvanceSearchRequestObject = new AdvanceSearchRequestObject();
		
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
		this.documentAdvanceSearchFilter.filterValue = inputField.value;
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
           
             console.log("++++++++++++++selected",selectField);
               console.log("this.selectedCorespondence",this.selectedCorespondence);
              console.log("this.selected",this.selected);
                          console.log("++++++++++++++selected",selectField.name);

                          console.log("++++++++++++++length",this.selected.length);
             
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
		 console.log("#########################################################################################################",this.current_user.employeeLogin)
		 this.documentAdvanceSearchFilter.filterValue = this.current_user.employeeLogin;
		 this.documentAdvanceSearchFilter.filterName = "Creator";
		 this.documentAdvanceSearchFilter.filterDataType = "STRING";
		 this.documentAdvanceSearchRequestObject.filter.push(this.documentAdvanceSearchFilter);
		 
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
	 }
	 var flag = true;
	var chSize = 0;
	for(let filter of this.documentAdvanceSearchRequestObject.filter){
		if(filter.filterValue && filter.filterValue !="") {
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
			document.getElementById("advFilterMsg").innerText = "Please provide the search text of at least 4 characters"
				return;
		}
			document.getElementById("advFilterMsg").style.display ="none";
			this.ticks = 0;
			this.timer = Observable.timer(1000,1000);
			// subscribing to a observable returns a subscription object
			this.sub = this.timer.subscribe(t => this.tickerFunc(t));  
			this.documentResults = []
		
			if(document.getElementById("loader")!=null){
			 document.getElementById("loader").style.display = "block";
			}
		}
		console.log("#########################################################################################################",this.current_user.employeeLogin)
		console.log("#########################################################################################################",this.current_user.employeeLogin)
	this.documentAdvanceSearchRequestObject.departmentId = this.current_user.employeeDepartment.departmentCode;
	this.browseService.SearchDocuments(this.documentAdvanceSearchRequestObject).subscribe(data => this.checkdata(data),error => this.checkerror(error));
	this.switchTab(searchModal, 'selectDocuments');
        
        this.selected=[];
    this.selectedDocumentFrom=[];
    this.selectedDocumentTo=[];
    this.selectedCorespondence=[];   
	}

simpleSearchDocuments(launchModal : any) {
	if(this.simpleSearchForm.dateCreated != undefined){
		 this.simpleSearchForm.dateCreated = this.simpleSearchForm.dateCreated.formatted;
	}
	this.totalCount=null;

	for (let attribute in this.simpleSearchForm) {
		var value = this.simpleSearchForm[attribute]
		if (value && typeof (this.simpleSearchForm[attribute]) != "function") {
			this.filter = new LaunchWorkflowSearchFilter();
			this.filter.filterName = attribute
			if(attribute =='ReferenceNo'){
			 	this.filter.filterCondition = "Like"
			}
			if(attribute =='DocumentID'){
			 	this.filter.filterCondition = "="   
			}
			if(attribute =='dateCreated'){
			 this.filter.filterCondition = "="  
			 this.filter.filterName = "DocumentDate" 
			}
			if(attribute =='DocumentTitle'){
		 		this.filter.filterCondition = "Like"  
				this.filter.filterName = "EmailSubject" 
			}
			this.filter.filterDataType = this.simpleSearchForm.attributeDataTypeMap()[attribute];
			this.filter.filterValue = encodeURIComponent(value);
		
			this.simpleSearchRequestObject.filter.push(this.filter);
		}
	}
		
	if((this.simpleSearchRequestObject.filter&& this.simpleSearchRequestObject.filter.length == 0)){
		document.getElementById("filterMsg").style.display ="block";
		return;
	}else{
		var chSize = 0;
		if(this.simpleSearchForm.DocumentTitle)
			chSize = this.dimsTrim(this.simpleSearchForm.DocumentTitle).length;
		if(this.simpleSearchForm.DocumentID && this.dimsTrim(this.simpleSearchForm.DocumentID).length > chSize)
			chSize = this.dimsTrim(this.simpleSearchForm.DocumentID).length;
		if(this.simpleSearchForm.ReferenceNo && this.dimsTrim(this.simpleSearchForm.ReferenceNo).length > chSize)
			chSize = this.dimsTrim(this.simpleSearchForm.ReferenceNo).length;
		if(this.simpleSearchForm.dateCreated && this.simpleSearchForm.dateCreated.trim().length > chSize)
			chSize = this.dimsTrim(this.simpleSearchForm.dateCreated).length;
		if(chSize < 4)
		{
			document.getElementById("subjectMsg1").innerText = 'Please provide at least four characters for search and try again';
			document.getElementById("subjectMsg1").style.display ="block";
			return;
		}
		document.getElementById("filterMsg").style.display ="none";

		this.ticks = 0;
	this.timer = Observable.timer(1000,1000);
 // subscribing to a observable returns a subscription object
	this.sub = this.timer.subscribe(t => this.tickerFunc(t));
	this.documentResults = []
	document.getElementById("loader").style.display = "block";
		}
		console.log("#################################################Simple search########################################################",this.current_user.employeeLogin)
		this.simpleSearchRequestObject.departmentId = this.current_user.employeeDepartment.departmentCode;
	this.browseService.SearchDocuments(this.simpleSearchRequestObject).subscribe(data => this.checkdata(data),error => this.checkerror(error));
	this.simpleSearchRequestObject = new SimpleSearchRequestObject();
	this.switchTab(launchModal, 'selectDocuments')
	}
	
	updateSubject(){
		 this.simpleSearchForm.DocumentTitle = document.getElementById("emailsubject").value; 
	}
	tickerFunc(tick){
		this.ticks = tick
	}

	addUsersList(userlist: any, listType: any) {
	if (listType == "to") {
		this.wradio_to = [];
	}
	if (listType == "cc") {
		this.wradio_cc = [];
	}
	for (let user of userlist) {
		if (this.wradio_to.indexOf(user) == -1 || this.wradio_cc.indexOf(user) == -1) {
		if (listType == "to") {
			this.wradio_to.push(user)
		} else {
			this.wradio_cc.push(user)
		}
		}
	}
	this.switchTab(this.launchModal, 'launchWorkflow')
	}

	removeFromList(event: any, value: any, listType: any) {
	if (listType == "wto") {
		
		if(document.getElementsByName(value)[2] !=null){
			document.getElementsByName(value)[2].checked = true;
		 }
		this.wradio_to.splice(this.wradio_to.indexOf(value), 1)
	}
	if (listType == "wcc") {
		if(document.getElementsByName(value)[2] !=null){
			document.getElementsByName(value)[2].checked = true;
		 }
		this.wradio_cc.splice(this.wradio_cc.indexOf(value), 1)
	}
	event.preventDefault();
	event.stopPropagation();
	return false;
	}

	ngOnInit() {
		
	this.browseLaunchSubscription = this.browseSharedService.openDocSearchModel$.subscribe(msg => this.openLaunchPopup(msg, 'Search'));
	this.browseLaunchSubscription = this.browseSharedService.emitBrowseLaunchPopup$.subscribe(docs => this.openLaunchPopupBrowse(docs));
	}


	assignUserSpecificDataForDocs(data,docs: any){
		this.current_user = data;
		console.log("################################ IF #####&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& :: ",this.current_user.employeeLogin);
		this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
		this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
		this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
	}

	openLaunchPopupBrowse(docs: any) {

		if(this.userService.getCurrentDelegatedForEmployeeLogin()){
			this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assignUserSpecificDataForDocs(data, docs));
		}else{
			console.log("################################### ELSE ##&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& :: ",this.current_user.employeeLogin);
			this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
			this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
			this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
		}

		this.newTask = new LaunchWorkflow();
		this.selected_docs =[];
		for (let data of docs) {
			if (data.workflowAttachmentType == "PRIMARY") {
				//commented and added by Rameshwar 27102017
				//this.newTask.subject = data.fileName;
				this.newTask.subject = data.subject;
			}
			this.LaunchPrimaryDoc = new LaunchPrimaryDoc(data);
			if(this.LaunchPrimaryDoc.document_id !=undefined  && this.LaunchPrimaryDoc.symbolicName != undefined){
				this.selected_docs.push(this.LaunchPrimaryDoc);
			}
			
		}
		if(this.selected_docs.length == 0){
			for(var i=0;i<docs.length;i++){
				this.selected_docs.push(docs[i]);
				//commented and added by Rameshwar 27102017
				//this.newTask.subject = docs[0].symbolicName;
				this.newTask.subject = docs[0].subject;
			}
		}
		if(localStorage.getItem('isThisFromWILaunch')){
			//added for launch wi subject from existing witem
			this.newTask.subject = localStorage.getItem('existingWISubject');
			//localStorage.removeItem('existingWISubject');
			//localStorage.removeItem('isThisFromWILaunch');
			}
		
		this.activeTab = 'launchWorkflow';
		this.launchModal.modalClass ="modal-lg";
		this.launchModal.open();
		}

		selectDocLaunch(e: any, doc: any) {
		if (e.currentTarget.checked == true) {
			doc.showOptions = true;
			this.selected_docs.push(doc);
			if(this.selected_docs.length>0){
				var j=0;
				for (var i = 0; i < this.selected_docs.length; i++) { 
				if(this.selected_docs[i].versionSeriesId == doc.versionSeriesId){
					j++;
					if(j>1){
						this.selected_docs.pop(doc);
						break;
					}
				}
			} 
		}
			
		} else if (e.currentTarget.checked == false) {
			doc.showOptions = false;
			if(this.selected_docs.length>0){
				var j=0;
				for (var i = 0; i < this.selected_docs.length; i++) { 
				if(this.selected_docs[i].versionSeriesId == doc.versionSeriesId){
						this.selected_docs.splice(i, 1);
						break;
				}
			} 
			}
		}
			if(this.selected_docs.length>0){
				if(document.getElementById("select_doc_button") !=null){
					document.getElementById("select_doc_button").style.display = "block";   
			}
		}else{
				
			if(document.getElementById("select_doc_button") !=null){
					document.getElementById("select_doc_button").style.display = "none";   
			} 
		}
		
	}

	launch_existingdoc(launchModal: any) {
	var subjectStr = document.getElementById("subject").value;
		subjectStr.trim()
	if(subjectStr.trim() ==""){
		 document.getElementById("subjectMsg").style.display ="block";
			return;
	}else if(subjectStr.trim().length > 400 ){
		//document.getElementById("subjectExceedMsg").style.display ="block";
			return;
	}else{
		document.getElementById("subjectMsg").style.display ="none";
		//document.getElementById("subjectExceedMsg").style.display ="none";
		var e = document.getElementById("instruction");
		var strUser = e.options[e.selectedIndex].text;

		var e = document.getElementById("priority");
		var strPriority = e.options[e.selectedIndex].text;
			
		this.newTask.instruction = strUser;

		if(strPriority == "Normal")
			this.newTask.priority = 0;

		if(strPriority == "Low")
			this.newTask.priority = 1;

		if(strPriority == "High")
			this.newTask.priority = 2;
		
		
		this.newTask.subject = subjectStr.trim();
		console.log("newTask  :: ",this.newTask);
		this.workflowService.launchDocuments(
			this.selected_docs, this.newTask, this.wradio_to, this.wradio_cc,this.attachmentFiles
		).subscribe(data => this.launchCompletionHandler(data,launchModal), error => this.launchCompletionHandler(error,launchModal));
		this.wradio_to = [];
		this.wradio_cc = [];
		this.selected_docs = [];
		this.attachmentFiles =[]; 
		//this.selected_docs =[];
		if(localStorage.getItem('isThisFromWILaunch')){
		localStorage.removeItem('existingWISubject');
		localStorage.removeItem('isThisFromWILaunch');
		}
		launchModal.close();
	}
	
	}

	launchCompletionHandler(response: any, launchModal: any) {
	this.browseSharedService.emitMessageChange(response);
if(localStorage.getItem('isLaunchFromDailyDocs')){
   this.router.navigate(['work-flow/dailydocument'], { queryParams: { random: new Date().getTime() } })
	localStorage.removeItem('isLaunchFromDailyDocs');
	}
	//Comment by malik
	//this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });
	}

	SelectedToRadio(e: any, user: User) {
	if (this.wradio_to.indexOf(user.employeeLogin) == -1) {
		if (this.wradio_cc.indexOf(user.employeeLogin) > -1) {
		this.wradio_cc.splice(this.wradio_cc.indexOf(user.employeeLogin), 1)
		}
		this.wradio_to.push(user.employeeLogin)
	}
	if(this.wradio_none.includes(user.employeeLogin)){
        this.wradio_none.splice( this.wradio_none.indexOf(user.employeeLogin), 1 );
            }
	}
	SelectedNoneRadio(e: any, user: User) {
	 /* if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
		if (this.wradio_cc.indexOf(user.employeeLogin) > -1 || this.wradio_to.indexOf(user.employeeLogin) > -1) {
			this.wradio_cc.splice(this.wradio_cc.indexOf(user.employeeLogin), 1)
			this.wradio_to.splice(this.wradio_to.indexOf(user.employeeLogin), 1)
		}
	}*/
		if(this.wradio_to.includes(user.employeeLogin)){
		this.wradio_to.splice( this.wradio_to.indexOf(user.employeeLogin), 1 );
		 
		}
	
	 if(this.wradio_cc.includes(user.employeeLogin)){
		this.wradio_cc.splice( this.wradio_cc.indexOf(user.employeeLogin), 1 );
			 
		 }
		 if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
			this.wradio_none.push(user.employeeLogin)
		 }
	}
	SelectedCcRadio(e: any, user: User) {
	if (this.wradio_cc.indexOf(user.employeeLogin) == -1) {
		if (this.wradio_to.indexOf(user.employeeLogin) > -1) {
		this.wradio_to.splice(this.wradio_to.indexOf(user.employeeLogin), 1)
		}
		this.wradio_cc.push(user.employeeLogin)
	}
	if(this.wradio_none.includes(user.employeeLogin)){
        this.wradio_none.splice( this.wradio_none.indexOf(user.employeeLogin), 1 );
            }
	}

	loadUsersForDirectorate(directorate: Directorate) {
	this.recipientsService.getUsersForDirectorate(directorate).subscribe();
	}

	loadDepartmentsForDirectorate(directorate: Directorate) {
	this.recipientsService.getDepartmentsForDirectorate(directorate).subscribe();
		
	}

	loadUsersForDepartment(department: Department,searchCrtieria : string) {
	this.disabled = false;
	window.document.getElementById("to").disabled = this.disabled;
	window.document.getElementById("cc").disabled = this.disabled;
	this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
	this.recipientsService.getUsersForDepartment(department,searchCrtieria).subscribe(
		_res =>{
			this.enableUserSelection(_res);
		}
	);
	}

	loadUsersForDepartment_1(department: Department,searchCrtieria : string) {
		this.disabled = false;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
		this.recipientsService.getUsersForDepartment_1(department,searchCrtieria).subscribe(
			_res =>{
				this.enableUserSelection(_res);
			}
		);
		}

	loadDivisionsForDepartment(department: Department) {
	
	this.recipientsService.getDivisionsForDepartment(department).subscribe();
	}

	loadUsersForDivision(division: Division,searchCrtieria : string) {
	this.disabled = false;
	if(window.document.getElementById("to"))
	window.document.getElementById("to").disabled = this.disabled;
	if(window.document.getElementById("cc"))
	window.document.getElementById("cc").disabled = this.disabled;
	//this.userSearchUrl = global.base_url + "/EmployeeService/getDivisionUsers?division_code="+division.divisionCode+"&user_login="+this.current_user.employeeLogin+"&searchCrtieria=:keyword";
	console.log("###################################Before getUsersForDivision() ######################################################################",this.current_user.employeeLogin)
	this.recipientsService.getUsersForDivision(division,this.current_user.employeeLogin,searchCrtieria).subscribe(_res =>{
		this.enableUserSelection(_res);
	});
	 if(document.getElementById("searchCriteria")!= null){
			document.getElementById("searchCriteria").value="";
		} 
	 
	}
	ngOnDestroy() {
	this.browseLaunchSubscription.unsubscribe();
	}
	// getDivisionUsers(){
	//  this.loadUsersForDivision(this.divisions[0]);
	// }
	
	clearRadio(event) {
	event.stopPropagation();
	$(".clear_radio").prop("checked", false);
	this.wradio_to = [];
	this.wradio_cc = [];
	}

	clearAdvanceList() {
	this.selected_docs = [];
	this.attachmentFiles = [];
	this.wradio_to = [];
	this.wradio_cc = [];
        ///multi
         this.showSelectBox = "single";
             this.DocumentFromSelectBox= "single";
          this.CorespondenceshowSelectBox= "single";
        this.DocumentToshowSelectBox= "single";
        
    this.selected=[];
    this.selectedDocumentFrom=[];
    this.selectedDocumentTo=[];
    this.selectedCorespondence=[];
        
	if(window.document.getElementById("to")!=null || window.document.getElementById("cc")!=null){
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
	}  
	if(document.getElementById("advFilterMsg") != undefined){
	document.getElementById("advFilterMsg").style.display ="none";
	}
	if(document.getElementById("subjectMsg") != undefined){
	document.getElementById("subjectMsg").style.display ="none";
	}
	 if(document.getElementById("subjectExceedMsg") != undefined){
	 document.getElementById("subjectExceedMsg").style.display ="none";
	}
		if(document.getElementById("subjectMsg1") != undefined){
	document.getElementById("subjectMsg1").style.display ="none";
	}
	if(document.getElementById("filterMsg")){
		document.getElementById("filterMsg").style.display ="none";
	}
	
	document.getElementById("loader").style.display = "block";
	document.getElementById("errortext").style.display = "none";
	document.getElementById("timeelapsed").style.display = "block";

	this.simpleSearchForm.dateCreated = ''
	this.simpleSearchForm.DocumentTitle = ''
	if(localStorage.getItem('isThisFromWILaunch')){
	localStorage.removeItem('existingWISubject');
	localStorage.removeItem('isThisFromWILaunch');
	}
	if( this.ticks>0){
	 this.sub.unsubscribe();
			 }
		if(document.getElementById("select_doc_button")!=null){
			document.getElementById("select_doc_button").style.display = "none";
		}
	 for (let inputField of [].slice.call(document.getElementsByTagName('input'))) {
			if (inputField.getAttribute('type') == "radio") {
				inputField.checked = false;
				}
		}
		
		$("#my-modal").modal("hide");
		$("#my-modal").hide();
		$('.modal-backdrop').hide();
		$("body").removeClass("modal-open");
		//console.log('$('body')length ::',$('body').length);
		//console.log('$('body')length ::',$('.modal-backdrop').length);
		/**if($('body')){
		console.log('Inside body');
		$('body').removeClass('modal-open');
		}**/
		//document.body.innerHTML
		//document.querySelector('body').classList.remove('modal-open');
	//	$('.modal-backdrop').modal('hide') 

		//if(document.querySelector('modal-backdrop')!=null&&document.querySelector('modal-backdrop').length){
		//$('.modal-backdrop').remove();
		//}
		/**if ($('.modal-backdrop').length > 0){
			console.log('Inside modalbackdrop');
  $('.modal-backdrop').remove();
}**/
	
	
	
	}
 
	switchTab1(){
		
		this.newTask.subject = this.selected_docs[0].subject;
		this.newTask.name = "Correspondence";
		//this.selected_docs = this.selected_docs.slice(); 

		this.userSelected_docs = this.selected_docs.slice();
		this.launchModal.close();
	}
	 
	checkdata(data){
		this.documentResults = data;
		this.totalCount = 0;
		this.totalCount = data.length
		if(data.length>0){
			
		document.getElementById("loader").style.display = "none";
			if(document.getElementById("select_doc_button") !=null){
				document.getElementById("select_doc_button").style.display = "none";   
		 }
		 
		 document.getElementById("backpress").style.marginLeft = "140px";
			document.getElementById("timeelapsed").style.display = "none"; 
		}
		if(data.length==0){
		 document.getElementById("loader").style.display = "none";  
		 if(document.getElementById("select_doc_button") !=null){
				document.getElementById("select_doc_button").style.display = "none";
		 }
		 
		 document.getElementById("errortext").style.display = "block";
		 document.getElementById("backpress").style.marginLeft = "200px"; 
			document.getElementById("timeelapsed").style.display = "none"; 
		 this.Errortext="No Documents Found";
		}
		this.sub.unsubscribe();
	 this.setTimeoutFunction();
	}
 checkerror(error){
	if(!(error.status==200)){ } 
	 document.getElementById("loader").style.display = "none";
	 document.getElementById("errortext").style.display = "block";
	 document.getElementById("select_doc_button").style.display = "none";
	 document.getElementById("backpress").style.marginLeft = "200px"; 
		document.getElementById("timeelapsed").style.display = "none"; 
	 
	 if(error.status==0){
		 this.Errortext="No Internet Connection";  
	 }else{
	 this.Errortext="No Documents Found";
		 }
	 this.sub.unsubscribe();
	 this.setTimeoutFunction();
	}
	setTimeoutFunction(){
		setTimeout(function() {
			if(document.getElementById("errortext")!=null){
				document.getElementById("errortext").style.display = "none";
			}
		 }.bind(this), 2000);
		 }
	getDivisionUserList(division: Division){
		this.searchCrtieria = document.getElementById("searchCriteria").value;
		//alert(this.searchCrtieria);
		this.loadUsersForDivision(division,this.searchCrtieria);
		 
	}
	
	getDepartmentUserList(department: Department){
		this.searchCrtieria = document.getElementById("deptsearchCriteria").value;
		this.loadUsersForDepartment_1(department,this.searchCrtieria);
		if(document.getElementById("deptsearchCriteria")!= null){
			document.getElementById("deptsearchCriteria").value="";
		}
	}  
	
	
	disableToCC(){
		this.userSearchUrl = "";
		this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
		this.disabled = true;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		window.document.getElementById("to").value='';
		window.document.getElementById("cc").value='';
	}

	disableToCC_1(){
		this.userSearchUrl = "";
		this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
		window.document.getElementById("to").value='';
		window.document.getElementById("cc").value='';
	}

	clickinput(){
	
	}
	 backpress(launchModal :any){
		 if(document.getElementById("advFilterMsg") != undefined){
	document.getElementById("advFilterMsg").style.display ="none";
	}
		
	if(document.getElementById("filterMsg")){
		document.getElementById("filterMsg").style.display ="none";
	}
	if(document.getElementById("subjectMsg1") != undefined){
	document.getElementById("subjectMsg1").style.display ="none";
	}
		this.switchTab(launchModal,'simpleSearch');
		 this.simpleSearchForm = new SimpleSearchForm();
		 this.simpleSearchForm.dateCreated ='';
		if(document.getElementById("select_doc_button") !=null){
				document.getElementById("select_doc_button").style.display = "block";
		}
		document.getElementById("timeelapsed").style.display = "block";
		if( this.ticks>0){
		this.sub.unsubscribe();
			 }
	 }
		 
	loadKNPCHierarchy(){
		this.disabled = false;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		//added by Ravi Boni
		this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
		//end
		this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.knpcHierarchy = data);
	}
	
	fileChange(event) {
		for (let attachedFile of event.target.files) {
			this.attachmentFiles.push(attachedFile);
		}
		}
	
	clearFile(event){
		 document.getElementById("choosedFile").value="";
	}
	 
	getUserList(){
		setTimeout(() => {

			if(this.userList.length > 0){
				if(this.userListThing)
				this.userListThing.isOpened = true
				if(this.divisionsThing)
				this.divisionsThing.isOpened = false
			}else{
				if(this.userListThing)
				this.userListThing.isOpened = false
				if(this.divisionsThing)
				this.divisionsThing.isOpened = true
			}
		
		  if(this.divisionsThing2)
			this.divisionsThing2.isOpened = false
		  if(this.crossDptThing)
			this.crossDptThing.isOpened = false
		  if(this.hierarchyThing)
				this.hierarchyThing.isOpened = false

			/*this.userListThing.isOpened = true
			this.divisionsThing2.isOpened = false
			this.divisionsThing.isOpened = false
			this.crossDptThing.isOpened = false
			this.hierarchyThing.isOpened = false*/
		}, 1000);
		

		this.disabled = false;
		this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
	}

	setAccordionAsClose(){
		setTimeout(() => {
			

			if(this.userList.length > 0){
				if(this.userListThing)
				this.userListThing.isOpened = true
				if(this.divisionsThing)
				this.divisionsThing.isOpened = false
			}else{
				if(this.userListThing)
				this.userListThing.isOpened = false
				if(this.divisionsThing)
				this.divisionsThing.isOpened = true
			}


			if(this.divisionsThing)
			this.divisionsThing.isOpened = true
		  if(this.divisionsThing2)
			this.divisionsThing2.isOpened = false
		  if(this.crossDptThing)
			this.crossDptThing.isOpened = false
		  if(this.hierarchyThing)
				this.hierarchyThing.isOpened = false

			/*this.divisionsThing2.isOpened = false
			this.divisionsThing.isOpened = true
			this.crossDptThing.isOpened = false
			this.hierarchyThing.isOpened = false*/
		}, 100);
		
	}



	loadDeafultUserList(list : any ,searchCrtieria :any){
		if(window.document.getElementById("to")!=null){
			this.disabled = false;
			window.document.getElementById("to").disabled = this.disabled;
		}
		if(window.document.getElementById("cc")!=null){
			window.document.getElementById("cc").disabled = this.disabled;
		}
		this.userSearchUrl = global.base_url + "/EmployeeService/loadDeafultUserList?listId="+list.listId+"&searchCrtieria=:keyword";
		this.recipientsService.loadDeafultUserList(searchCrtieria,this.current_user.employeeLogin,list).subscribe(data =>{
			this.defaultUserList = data;
			this.enableUserSelection(data);
		} );
		if(document.getElementById("userList")!= null){
			document.getElementById("userList").value="";
		}
	}
	

	loadCrossDepartmentUsers(searchCrtieria :any){
		this.disabled = false;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		this.userSearchUrl = global.base_url + "/EmployeeService/getCrossDepartmentUsers?searchCrtieria=:keyword";
		this.recipientsService.getCrossDepartmentUsers(searchCrtieria).subscribe(data =>{ 
			this.crossDepartment = data;
			this.enableUserSelection(data);
		});
		if(document.getElementById("crosssearchCriteria")!= null){
			document.getElementById("crosssearchCriteria").value="";
		}
	}
	
	getCrossDepartmentUserList(){
		this.searchCrtieria = document.getElementById("crosssearchCriteria").value;
		
		this.loadCrossDepartmentUsers(this.searchCrtieria);
	}
	
	filterUserList(list : any ){
		 this.searchCrtieria = document.getElementById("userList").value;
		
		 this.loadDeafultUserList(list,this.searchCrtieria);
	 }
	
	loadKNPCHierarchyUsersForDepartment(department: Department,searchCrtieria : string) {
		this.disabled = false;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		/*this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
		this.recipientsService.getKNPCHierarchyUsersForDepartment(department,searchCrtieria).subscribe();
		if(document.getElementById("deptsearchCriteria1")!= null){
			document.getElementById("deptsearchCriteria1").value="";
		}*/
		
		if(this.isGlobalKNPCSearch) {
            //this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?searchCrtieria=:keyword";
            this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
            this.recipientsService.getKNPCHierarchyUsersForDepartment(department,this.globalSearchCrtieria).subscribe(_res=>{
                this.enableUserSelection(_res);
            });
            //this.isGlobalKNPCSearch=false;
        }
        else {
            this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
            this.recipientsService.getKNPCHierarchyUsersForDepartment(department,searchCrtieria).subscribe(_res=>{
                this.enableUserSelection(_res);
            });
        }
        
        if(document.getElementById("deptsearchCriteria1")!= null){
            document.getElementById("deptsearchCriteria1").value="";
        }
        if(document.getElementById("deptsearchCriteria2")!= null){
            document.getElementById("deptsearchCriteria2").value="";
        }
		window.document.getElementById("to").value='';
		window.document.getElementById("cc").value='';
	}

	loadKNPCHierarchyUsersForDepartment_1(searchCrtieria : string) {
		//commented by Ravi Boni
		//this.globalSearchCrtieria=undefined;
		this.isGlobalKNPCSearch = true;
		this.disabled = false;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		//this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?searchCrtieria=:keyword";
		this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
		this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => {    
            this.knpcHierarchy = data;
            this.enableUserSelection(data);
        });
        
        if(document.getElementById("deptsearchCriteria2")!= null){
            document.getElementById("deptsearchCriteria2").value="";
		}
		window.document.getElementById("to").value='';
		window.document.getElementById("cc").value='';
	}

	loadKNPCHierarchyUsersForDepartment_2(searchCrtieria : string) {
		this.globalSearchCrtieria=undefined;
		this.isGlobalKNPCSearch = true;
		this.disabled = false;
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
		//this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?searchCrtieria=:keyword";
		this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
		this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => {    
            this.knpcHierarchy = data;
            this.enableUserSelection(data);
        });
        
        if(document.getElementById("deptsearchCriteria2")!= null){
            document.getElementById("deptsearchCriteria2").value="";
		}
		window.document.getElementById("to").value='';
		window.document.getElementById("cc").value='';
	}
	
	loadKNPCHierarchyUsersList(department: Department){
		this.isGlobalKNPCSearch=false;
		this.searchCrtieria = document.getElementById("deptsearchCriteria1").value;
		this.loadKNPCHierarchyUsersForDepartment(department,this.searchCrtieria);
	} 
	
	loadKNPCHierarchyUsersList_1(){
		this.globalSearchCrtieria = document.getElementById("deptsearchCriteria2").value;
		this.loadKNPCHierarchyUsersForDepartment_1(this.globalSearchCrtieria);
	}
	
	isNumber(evt: any) {
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode > 31 && (charCode < 48 || charCode > 57)) {
			return false;
		}
		return true;
	}
	
	selectAll(userList : any, recpType : any, suffix : any){
		 for (var i = 0; i < userList.length; i++) { 
			 if(recpType=="to"){
				
				 this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"to"));
				
				 if (this.wradio_to.indexOf(userList[i].employeeLogin) == -1) {
					 this.wradio_to.push(userList[i].employeeLogin);
				 }
				 if(this.wradio_cc.includes(userList[i].employeeLogin)) {
					 this.wradio_cc.splice( this.wradio_cc.indexOf(userList[i].employeeLogin), 1 );
				 }
				 if(this.wradio_none.includes(userList[i].employeeLogin)) {
					this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
				}
			 }else if(recpType=="cc"){
				
				
				this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"cc"));
				 if (this.wradio_cc.indexOf(userList[i].employeeLogin) == -1) {
					this.wradio_cc.push(userList[i].employeeLogin);
				 }
				 if(this.wradio_to.includes(userList[i].employeeLogin)) {
					 this.wradio_to.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
				 }
				 if(this.wradio_none.includes(userList[i].employeeLogin)) {
					this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
				}
			 }else if(recpType=="none"){
				if (this.wradio_none.indexOf(userList[i].employeeLogin) == -1) {
					this.wradio_none.push(userList[i].employeeLogin);
				}
				this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"none"));
				 if(this.wradio_to.includes(userList[i].employeeLogin)) {
					 this.wradio_to.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
				 }
				 if(this.wradio_cc.includes(userList[i].employeeLogin)) {
					 this.wradio_cc.splice( this.wradio_cc.indexOf(userList[i].employeeLogin), 1 );
				 }
			 }
		}
	}

	globalSelectAll(recpType : any){
        var dep_codes = '';
        if(this.knpcHierarchy && this.knpcHierarchy.length>0){
            for(var i = 0; i < this.knpcHierarchy.length; i++) {
                //dep_codes.push(this.knpcHierarchy[i].departmentCode);
                dep_codes = dep_codes + this.knpcHierarchy[i].departmentCode + "~";
            }
        }       
        this.recpntType = recpType;
        this.recipientsService.getKNPCHierarchyUsersForGlobalDepartment(dep_codes,this.globalSearchCrtieria).subscribe((data) => this.selectAllToCcNone(data));
    }

    selectAllToCcNone(data: any) {
         var userList = [];
         userList = data;
         for (var i = 0; i < userList.length; i++) { 
			if(this.recpntType=="to"){
			   
				this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"to"));
			   
				if (this.wradio_to.indexOf(userList[i].employeeLogin) == -1) {
					this.wradio_to.push(userList[i].employeeLogin);
				}
				if(this.wradio_cc.includes(userList[i].employeeLogin)) {
					this.wradio_cc.splice( this.wradio_cc.indexOf(userList[i].employeeLogin), 1 );
				}
				if(this.wradio_none.includes(userList[i].employeeLogin)) {
				   this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
			   }
			}else if(this.recpntType=="cc"){
			   
			   
			   this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"cc"));
				if (this.wradio_cc.indexOf(userList[i].employeeLogin) == -1) {
				   this.wradio_cc.push(userList[i].employeeLogin);
				}
				if(this.wradio_to.includes(userList[i].employeeLogin)) {
					this.wradio_to.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
				}
				if(this.wradio_none.includes(userList[i].employeeLogin)) {
				   this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
			   }
			}else if(this.recpntType=="none"){
			   if (this.wradio_none.indexOf(userList[i].employeeLogin) == -1) {
				   this.wradio_none.push(userList[i].employeeLogin);
			   }
			   this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"none"));
				if(this.wradio_to.includes(userList[i].employeeLogin)) {
					this.wradio_to.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
				}
				if(this.wradio_cc.includes(userList[i].employeeLogin)) {
					this.wradio_cc.splice( this.wradio_cc.indexOf(userList[i].employeeLogin), 1 );
				}
			}
	   }

     }

	enableUserSelection(userArr:any){
		var _ref = this;
		setTimeout(function() {
			for(var i=0;i<userArr.length ; i++){
				if(_ref.wradio_to.indexOf(userArr[i].employeeLogin) >= 0){
					_ref.checkedAllWithSameClassName(document.getElementsByClassName(userArr[i].employeeLogin+"to"));
				}else if(_ref.wradio_cc.indexOf(userArr[i].employeeLogin)>=0){
					_ref.checkedAllWithSameClassName(document.getElementsByClassName(userArr[i].employeeLogin+"cc"));
				}else if(_ref.wradio_none.indexOf(userArr[i].employeeLogin)>=0){
					_ref.checkedAllWithSameClassName(document.getElementsByClassName(userArr[i].employeeLogin+"none"));
				}
			}
		},200);
		
	}
	checkedAllWithSameClassName(domArr:any){
		for(var i=0;i<domArr.length ; i++){
			domArr[i].checked = true;
		}
	}
	enterKeyPressed(evt : any,list) {
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode == 13) {
			 this.filterUserList(list);
		}
	}    
	searchDivision(evt: any, division:any ){
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode == 13) {
			 this.getDivisionUserList(division);
		}
	}
	
	searchDepartment(evt :any ,department: any ){
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode == 13) {
			 this.getDepartmentUserList(department);
		}
	}
	
	searchCrossDepartment(evt:any){
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode == 13) {
			 this.getCrossDepartmentUserList();
		}
	}
	
	searchKNPCDepartment(evt:any ,department :any){
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode == 13) {
			 this.loadKNPCHierarchyUsersList(department);
		}
	}

	searchKNPCDepartment_1(evt:any){
		evt = (evt) ? evt : window.event;
		var charCode = (evt.which) ? evt.which : evt.keyCode;
		if (charCode == 13) {
			 this.loadKNPCHierarchyUsersList_1();
		}
	}

	viewDocument(fileToView: string){
		window.open(fileToView, '_blank', 'fullscreen=no');
	} 

	setAttachmentDocHist(doc) {
		this.currentTab = 'History'
		this.selectedAttachmentID = doc.id +'~~'+ new Date();		
	}
	
	setAttachmentID(id) {
		this.currentTab = 'Properties'		
		this.selectedAttachmentID = id +'~~'+ new Date();
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
	
	notifyToDisplayProperties(docId : any) { 
		this.notifytoSetAttachmentId.emit(docId);
	}
	
	dimsTrim(value: string) {
    	if(!value)
        	return "";
    	return decodeURIComponent(value).trim();
	  }
	

	disable(){
		window.document.getElementById("to").disabled = this.disabled;
		window.document.getElementById("cc").disabled = this.disabled;
	}
	handleDocumentChange(doc){
        if(this.documentResults && this.documentResults.length > 0){
            for(var i = 0;i<this.documentResults.length; i++){
            
                if(this.documentResults[i].id == doc.id ){
                    
                        this.documentResults[i] = doc;
                        this.documentResults = this.documentResults.slice();
            
                }
            
            
            }
            
        }
	}
	checkUserAndDelegateJobTitle(){
		if(localStorage.getItem("isDelegate") == "true"){
			return JSON.parse(JSON.parse(localStorage.getItem("selectedDelegateUser"))).EmpJobTitle;
		  }else{
			return this.userService.getCurrentUser().employeeJobTitle;
		  }
		}
		checkJobTitle(){
			return this.checkUserAndDelegateJobTitle() == 'CRDEP' || this.checkUserAndDelegateJobTitle() == 'DCEO' || this.checkUserAndDelegateJobTitle() == 'TL' || this.checkUserAndDelegateJobTitle() == 'MGR' || this.checkUserAndDelegateJobTitle() == 'DIMS';
		}
	}
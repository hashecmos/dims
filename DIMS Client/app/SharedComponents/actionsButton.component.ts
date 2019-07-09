import {ViewChild, Component, Input, Output, OnChanges , EventEmitter } from '@angular/core'
import { InboxReply } from './../model/inboxReply';
import { Observable, Subscription } from 'rxjs/Rx';
import { Router, ActivatedRoute } from '@angular/router'
import {WorkflowService} from '../services/workflowServices/workflow.service'
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import {RecipientsService} from '../services/recipients.service'
import {UserService} from '../services/user.service'
import {SimpleSearchForm } from './../model/simpleSearchForm.model'
import {LaunchWorkflow} from './../model/launchWorkflow.model'
import {DocumentResult} from './../model/documentResult.model'
import {AdvanceSearchRequestObject} from './../model/advanceSearchRequestObject.model'
import {SimpleSearchRequestObject} from './../model/simpleSearchRequestObject.model'
import {LaunchWorkflowSearchFilter} from './../model/launchWorkflowSearchFilter.model'
import {TaskDetail} from './../model/taskDetail.model';
import {AdvanceSearchForm} from './../model/advanceSearchForm.model'
import {LaunchPrimaryDoc} from './../model/launchPrimaryDoc.model'
import {BrowseService} from '../services/browse.service'
import {ForwardTask} from './../model/forwardInbox.model';
import {AddUserTask} from './../model/addUserInbox.model';
import {DoneTask} from './../model/inboxAction.model';
import {ReassignTask} from './../model/reassigninbox.model';
import {WorkflowRecipientList} from './../model/workflowRecipientList.model';
import {Directorate} from './../model/recipients.model'
import {User} from './../model/userRecipients.model'
import {Department} from './../model/departmentRecipients.model'
import {Division} from './../model/divisionRecipients.model'
import {EmployeeDetails} from './../model/employeeDetails.model'
import {WorkflowAttachments} from './../model/workflowAttachments.model'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';
import {IMyDrpOptions, IMyDateRangeModel} from 'mydaterangepicker';
import {SelectItem} from 'primeng/primeng';
import global = require('./../global.variables')

declare var $: any;


@Component({
  selector: 'actions-btn',
  providers: [RecipientsService, BrowseService],
  templateUrl: 'app/SharedComponents/actionsButton.component.html',
})
export class ActionsButton implements OnChanges   {
  @Input() is_archive_available: boolean;
  @Input() is_for_buttons: boolean;
  @Input() is_cc_item: boolean;
  @Input() is_done_by_sub_item:boolean;
  @Input() is_done_available : boolean;
  @Input() is_complete_available :boolean;
  @Input() selectedTasks: any ;
  @Input() is_all_selected :boolean;
  @Input() is_launch_available : boolean;
  @Input() tasks : any;
  @Output() notifyParenttoarchive: EventEmitter<any> = new EventEmitter();
  @Output() notifyParentToComplete: EventEmitter<any> = new EventEmitter();
  @Output() launch: EventEmitter<any> = new EventEmitter();
  @Input() action_list : any;
  @Output() selectedListEmpty: EventEmitter<any> = new EventEmitter();
  @ViewChild('actionModal') launchModal;
  @ViewChild('userListThing') userListThing;
  @ViewChild('divisionsThing') divisionsThing;
  @ViewChild('deptThing') deptThing;
  @ViewChild('hierThing') hierThing;
  @ViewChild('crossThing') crossThing;
  private userSearchUrl;
  workflowAttachments: WorkflowAttachments;
  private simpleSearchForm = new SimpleSearchForm();
  private advanceSearchForm = new AdvanceSearchForm();
  private documentResults: DocumentResult;
  private filter = new LaunchWorkflowSearchFilter();
  private selected_docs: any = [];
  private docIds: any = [];
  private simpleSearchRequestObject = new SimpleSearchRequestObject();
  private advanceSearchRequestObject = new AdvanceSearchRequestObject();
  private documentAdvanceSearchRequestObject: AdvanceSearchRequestObject;
  private documentAdvanceSearchFilter: LaunchWorkflowSearchFilter;
  enableLoader:boolean = false;
  filtertype: any;
  actionOptions: string[];
  replyObject: InboxReply;
  selectedTosList: any = [];
  selectedCcsList: any = [];
  activeTab: string = 'default';
  inboxActionObject: any;
  workflowRecipientList: any;
  formData: FormData;
  public priorities : any = ["Normal","Low","High"];
  attachmentFiles: any = [];
  existingAttachmentFiles: any = [];
  public directorates: any = [];
  public divisions: any = [];
  public departments: any = [];
  public crossDepartment: any = [];
  public defaultUserList:any = [];

  public currentTask: TaskDetail = new TaskDetail();
  
  private LaunchPrimaryDoc: LaunchPrimaryDoc;
  wradio_to: any = [];
  wradio_cc: any = [];
  wradio_none: any = [];

  private selectedAttachmentID: string;
  selectedDocument: any;
  private currentTab: any;
    
  public searchCrtieria : string = "";
  private accordionMenu: any;
  private current_user: EmployeeDetails;
  private currentDelegateForEmployeeLogin: string = "";
  private documentSearchForm = new AdvanceSearchForm();
  private knpcHierarchy : any = [];
  toSelectionListAvailability: any;
  advanceSearchApiValues: any = [];
  snapshot: any;
  designation:string; 
  ticks = 0;
  private timer;
  private sub: Subscription;
  public textComparisonOperators: string[] = ["is equal to", "like","contains", "starts with","ends with"];
  public numberComparisonOperators: string[] = ["=", "in", "not in"];
  public dateComparisonOperators: string[] = ["period","=",">", "<", "<=", ">=","between"];
  public equalOperator: string[] = ["="];
  public equalOperatorForDocuemntID: string[] = ["is equal to"];
  private flag :boolean =false;
  private myDatePickerOptions: IMyOptions = global.date_picker_options;
  private myDateRangePickerOptions: IMyDrpOptions  = global.date_range_option;
  public instructions: any = [];
  private selectedRecipient : any;
  private recipientList : any;
  private userList : any =[];
  private document_from : any;
  private delegateLogin : any;
  private departmentsAdv : any;  
  private base_url : any ;
  private viewer_url : any;
  private os_name : any;
  private documentType : any;
  private creatorSearchUrl;
  private creator : any;
  private removedExistingDocument : any;
  private parentType: string = "NONE";

    //multi
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
    /////
  private totalCount : any;
  public is_add_user : Boolean = false;
  public recipientSelected : Boolean = false;
  public depts:any;
  public globalSearchCrtieria: any;
  public isGlobalKNPCSearch: any;
  public recpntType: any;

  public showMyUserList: Boolean= false;
  public isRecipentSelected : Boolean = false;

  constructor(private router: Router,
    private route: ActivatedRoute,
    private workflowService: WorkflowService,
    private browseService: BrowseService,
    public recipientsService: RecipientsService,
    private userService: UserService,
    private browseSharedService: BrowseSharedService
  ) {
      this.removedExistingDocument =[];
    this.base_url = global.base_url;
    this.viewer_url = global.viewer_url;
    this.os_name = global.os_name;
    this.userSearchUrl = global.getUserList;
    this.creatorSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
    this.snapshot = route.snapshot;
    this.accordionMenu = this.workflowService.getAccordionMenu();
    this.current_user = this.userService.getCurrentUser();
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    this.delegateLogin = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }
    this.userService.notifyDelegateForChange.subscribe(data => this.currentDelegateForEmployeeLogin = data);
    this.replyObject = new InboxReply();
    this.recipientsService.getDirectorates().subscribe(data => this.directorates = data);
    this.workflowService.getLaunchInstructions().subscribe(data => this.instructions = data);
    
    this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
    this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
    this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));

    this.recipientsService.getCrossDepartmentUsers(this.current_user.employeeLogin).subscribe(data => this.crossDepartment = data);
    this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.departmentsAdv = data);
    this.recipientsService.getDocumentType().subscribe(data=> this.populateDocumentType(data));
     this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.populatedocument_from( data));
     this.populateCorrespondance();
  }
    ////MethodsMulti
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

defaultUserListonLoad(data){
    this.userList = data;
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

  dimsTrim(value: string) {
    if(!value)
        return "";
    return decodeURIComponent(value).trim();
  }


  ngOnChanges() { 
      this.getActions(this.action_list);
  }

  getActions(action_list : any){
       this.is_complete_available = false;
       this.is_archive_available =false;
       this.actionOptions =[];
       if(action_list != undefined){
           for(let action of action_list){

          if(action =='Complete'){
              this.is_complete_available = true;
          }else if(action =='Archive'){
            this.is_archive_available =true;
          }else{
              this.actionOptions.push(action);
          }
        }
       }
 }  
   checkReassign(data){
      
     if(this.is_all_selected){
          this.actionOptions = [];
         if(this.selectedTasks[0].workflowWorkItemType != "Reply" && this.selectedTasks[0].workflowWorkItemType != "cc"){
             this.is_complete_available = true;
         }
         if(this.selectedTasks[0].workflowWorkItemType == "to" || this.selectedTasks[0].workflowWorkItemType == "cc"){
             this.actionOptions = ['Add Users'];
         }
          
      }else{
          /*if(this.selectedTasks.length >0){
              if(this.selectedTasks[0].workflowItemRootSender == this.current_user.employeeLogin || (this.selectedTasks[0].workflowItemRootSender == this.delegateLogin && this.selectedTasks[0].workflowWorkItemType!='Reply')){
                  this.is_complete_available = true;
              }else{
                  this.is_complete_available = false;
              }
          }*/
          //this.is_complete_available = true;
          if(this.is_cc_item) {
                this.actionOptions = ['Add Users']
                /*if(this.selectedTasks[0].workflowRecipientList != undefined){
                   if(this.selectedTasks[0].workflowRecipientList[0].workflowRecipient == this.current_user.employeeLogin){
                        this.is_archive_available = true;    
                    }else{
                        this.is_archive_available = false;
                    } 
                }
               
                if(this.selectedTasks[0].workflowItemRootSender == this.current_user.employeeLogin){
                    this.is_archive_available = false;
                }else{
                    this.is_archive_available = true; 
                }*/
          }else{
                var isReassign = false;
                  for(let recp of data){
                      if(recp.workflowWorkItemStatus !='Forward' && recp.workflowWorkItemStatus != 'Done'){
                          isReassign = true;
                          break;
                      }
                  }
                  
                  if(isReassign){
                      this.actionOptions = ['Reassign', 'Add Users'];
                  }else{
                      this.actionOptions = ['Add Users'];
                  }
                //this.actionOptions = ['Reassign', 'Add Users']
          }
          
          if(this.is_done_by_sub_item){
              this.actionOptions = []
          }
      }  
       
     
      
  }
    
  addTosList(event) {
    if (event.employeeLogin != undefined) {
      if (this.selectedTosList.indexOf(event.employeeLogin) == -1 && this.selectedCcsList.indexOf(event.employeeLogin) == -1) {
        this.selectedTosList.push(event.employeeLogin);
        setTimeout(() => {
          $(".action_auto_complete_to").val("");
            document.getElementById("f_to").value = "";
            document.getElementById("f_to").focus();
        }, 100);
      }else{
        setTimeout(() => {
            $(".action_auto_complete_to").val("");
              document.getElementById("f_to").value = "";
              document.getElementById("f_to").focus();
          }, 100);
      }
    }
  }

  addCcsList(event) {
    if (event.employeeLogin != undefined) {
      if (this.selectedTosList.indexOf(event.employeeLogin) == -1 && this.selectedCcsList.indexOf(event.employeeLogin) == -1) {
        this.selectedCcsList.push(event.employeeLogin);
        setTimeout(() => {
          $(".action_auto_complete_cc").val("");
            document.getElementById("f_cc").value = "";
            document.getElementById("f_cc").focus();
        }, 100);
      }else{
        setTimeout(() => {
            $(".action_auto_complete_cc").val("");
              document.getElementById("f_cc").value = "";
              document.getElementById("f_cc").focus();
          }, 100);
      }
    }
  }

  setParentType(data) {
      this.parentType = data._body;
  }

  
	assignUserSpecificData(data,action: any, actionModal: any){
		this.current_user = data;
		console.log("################################ IF #####&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& :: ",this.current_user.employeeLogin);
		this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
		this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
		this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
        this.performWorkitemAction(action, actionModal);
    }
    
  performAction(action: any, actionModal: any) {
    
    if(this.userService.getCurrentDelegatedForEmployeeLogin()){
        this.userService.getUserDetails(this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(data => this.assignUserSpecificData(data,action, actionModal));
    }else{
        console.log("################################### ELSE ##&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& :: ",this.current_user.employeeLogin);
        this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
        this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
        this.recipientsService.getUserList(this.current_user.employeeLogin).subscribe(data => this.defaultUserListonLoad(data));
        this.performWorkitemAction(action, actionModal);
    }
    
   
  }

  performWorkitemAction(action: any, actionModal: any)
  {

    $('.modal-backdrop').show();
	$("body").addClass("modal-open");
    if (action == 'Launch') {
        localStorage.setItem('isThisFromWILaunch',true);
        localStorage.setItem('existingWISubject',this.selectedTasks[0].workflowItemSubject);   
      this.workflowService.getTask(this.selectedTasks[0].workflowWorkItemID).subscribe(data => this.launchPrimaryPopup(data));
    }else if(this.is_all_selected && action =="complete"){
        actionModal.modalClass="";
        actionModal.open();
    }else if(action == "Reassign"){
        this.workflowService.getSentItemRecipients(this.selectedTasks[0].workflowWorkItemID).subscribe(data => this.launchReassign(data,action,actionModal));
    }else {
      this.workflowService.getWorkitemParentType(this.selectedTasks[0].workflowWorkItemID).subscribe(data => this.setParentType(data));
      this.replyObject.reply_type = action;
      this.replyObject.subject = this.selectedTasks[0].workflowItemSubject;
      this.replyObject.name = this.selectedTasks[0].workflowName;
      this.attachmentFiles = [];
      if(action=="Forward" && this.selectedTasks.length == 1 && this.selectedTasks[0].workflowAttachments == undefined){
          this.workflowService.getTask(this.selectedTasks[0].workflowWorkItemID).subscribe(data => this.updateAttachment(data));
      }else {
          this.existingAttachmentFiles = this.selectedTasks[0].workflowAttachments;
      }
      
      if(action != "Done" && action != "complete"){
          actionModal.modalClass="modal-lg";
           
      }else{
          actionModal.modalClass="";
      }
      actionModal.open();
    }
  }

    updateAttachment(data){
        this.currentTask.assignAttribiteValuesFromData(data);
        var existingAttachmentFiles = [];
        for (let attachment of this.currentTask.workflowAttachments) {
          existingAttachmentFiles.push(attachment.workflowDocumentId)
        }
        this.browseService.loadDocumentsFileProperties(existingAttachmentFiles).subscribe(data => this.updateExistingDocId(data))
    }
    
    updateExistingDocId(data){
        this.currentTask.assignMimeTypeDataToAttachments(data)
        this.existingAttachmentFiles = [];
        this.existingAttachmentFiles = this.currentTask.workflowAttachments;
    }
  setDetails(action: any){
      this.replyObject.reply_type = action;
      this.replyObject.subject = this.selectedTasks[0].workflowItemSubject;
      this.replyObject.name = this.selectedTasks[0].workflowName;
      this.attachmentFiles = [];
      this.existingAttachmentFiles = this.selectedTasks[0].workflowAttachments;
  }
  launchPrimaryPopup(data) {
    
    this.currentTask.assignAttribiteValuesFromData(data);
    var attachmentIds = [];
    for (let attachment of this.currentTask.workflowAttachments) {
      attachmentIds.push(attachment.workflowDocumentId)
    }
    //this.existingAttachmentFiles = this.selectedTasks[0].workflowAttachments;
    this.browseService.loadDocumentsFileProperties(attachmentIds).subscribe(data => this.launchPrimaryPopupModelOpen(data))
  }

 
  launchPrimaryPopupModelOpen(data) {
      $('.modal-backdrop').show();
	$("body").addClass("modal-open");
    this.currentTask.assignMimeTypeDataToAttachments(data)
    if(this.currentTask.workflowAttachments && this.currentTask.workflowAttachments.length > 0) {
        this.LaunchPrimaryDoc = new LaunchPrimaryDoc(this.currentTask.workflowAttachments[0]);
        this.browseSharedService.emitBrowseLaunchPopupOpen(this.currentTask.workflowAttachments);
    }
    /*for (let data of this.currentTask.workflowAttachments) {
      if (data.workflowAttachmentType == "PRIMARY") {
        this.LaunchPrimaryDoc = new LaunchPrimaryDoc(data);
        this.browseSharedService.emitBrowseLaunchPopupOpen(this.currentTask.workflowAttachments);
          //this.browseSharedService.emitBrowseLaunchPopupOpen(this.LaunchPrimaryDoc)
     }
    } */
  }

  archive_task() {
    for (let task of this.selectedTasks) {
      this.workflowService.archiveItem(task.workflowWorkItemID).subscribe(data => this.refreshAfterArchive(data));
    }
  }

  completeTask(action: any, actionModal: any) {
    //actionModal.open();
    if (action == "complete") {
      this.notifyParentToComplete.emit(action);
      actionModal.close();
    } else if (action == "notComplete") {
       actionModal.close();
    } else {
       actionModal.close();
    }
    document.getElementById("displayfade_in").style.display = "none";
    document.getElementById("loader_main_search").style.display = "none"; 
    $('.modal-backdrop').hide();
    $("body").removeClass("modal-open");
    if(this.router.url.indexOf('inbox') >= 0) {         
        this.router.navigate(['work-flow/inbox'] , { queryParams: { random: new Date().getTime() } });       
    }else if(this.router.url.indexOf('sent-item') >= 0) {
        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        
    }else if(this.router.url.indexOf('archive') >= 0) {        
        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        
    } else {            
        this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        
    } 
        
  }

  removeFromList(event: any, value: any, listType: any) {
    event.stopPropagation();
    if (listType == "wto") {
      if(document.getElementsByName(value)[2] !=null){
            document.getElementsByName(value)[2].checked = true;
       }
      this.selectedTosList.splice(this.selectedTosList.indexOf(value), 1);
    }
    if (listType == "wcc") {
       if(this.is_cc_item){
           if(document.getElementsByName(value)[1] !=null){
            document.getElementsByName(value)[1].checked = true;
            }
       }else {
           
           if(document.getElementsByName(value)[2] !=null){
            document.getElementsByName(value)[2].checked = true;
            }
       }
      this.selectedCcsList.splice(this.selectedCcsList.indexOf(value), 1);
    }
  }


  addUsersList(userlist: any, listType: any) {
    for (let user of userlist) {
      if (this.selectedTosList.indexOf(user) == -1 && this.selectedCcsList.indexOf(user) == -1) {
        if (listType == "to") {
          this.selectedTosList.push(user)
        } else {
          this.selectedCcsList.push(user)
        }
      }
    }
    this.switchTab('default')
  }


  addTosAndCcsList(value: any, listType: any) {
    if (listType == "selectedTosList") {
      if (this.selectedTosList.indexOf(value) < 0) {
        if (this.selectedCcsList.indexOf(value) > -1) {
          this.selectedCcsList.splice(this.selectedCcsList.indexOf(value), 1)
        }
        this.selectedTosList.push(value);
      }
    }
    if (listType == "selectedCcsList") {
      if (this.selectedCcsList.indexOf(value) < 0) {
        if (this.selectedTosList.indexOf(value) > -1) {
          this.selectedTosList.splice(this.selectedTosList.indexOf(value), 1)
        }
        this.selectedCcsList.push(value);
      }
    }
  }

  clearList() {

    if(document.getElementById("errorForMultiUsers"))
        document.getElementById("errorForMultiUsers").style.display = "none";  

    this.showSelectBox = "single";
    this.DocumentFromSelectBox= "single";
    this.CorespondenceshowSelectBox= "single";
    this.DocumentToshowSelectBox= "single";
    this.selected=[];
    this.selectedDocumentFrom=[];
    this.selectedDocumentTo=[];
    this.selectedCorespondence=[];
    this.docIds = [];
    this.attachmentFiles = [];
    this.existingAttachmentFiles = [];
    this.selected_docs = [];
    this.selectedTosList = [];
    this.selectedCcsList = [];
    this.recipientSelected = false;
    // if(window.document.getElementById("f_to")!=null ){
    //   //  window.document.getElementById("f_to").disabled = true;
        
    // }
    // if(window.document.getElementById("f_cc")!=null){
    //  //   window.document.getElementById("f_cc").disabled = true;
    // }
    
    if(document.getElementById("advFilterMsg") != undefined){
    document.getElementById("advFilterMsg").style.display ="none";
    }
      
    if(document.getElementById("filterMsg")){
        document.getElementById("filterMsg").style.display ="none";
    }
    
      
    $('.selectListRadio,.searchResultRadio').prop('checked', false);
    this.activeTab = 'default';
      
      for (let inputField of [].slice.call(document.getElementsByTagName('input'))) {
          if (inputField.getAttribute('type') == "radio") {
                inputField.checked = false;
              }
      }
      if(this.selectedTasks.length>0){
          if(this.removedExistingDocument.length > 0){
              for(let exstDoc of this.removedExistingDocument){
                  if(this.selectedTasks[0].workflowAttachments.indexOf(exstDoc) == -1){
                    this.selectedTasks[0].workflowAttachments.push(exstDoc);
                  }
              }
          }
      }
    document.getElementById("displayfade_in").style.display = "none";
    document.getElementById("loader_main_search").style.display = "none"; 
    $('.modal-backdrop').hide();
    $("body").removeClass("modal-open");
  }

  sendReply(actionModal: any) {
    this.enableLoader = true;
    document.getElementById("displayfade_in").style.display = "block";
    document.getElementById("loader_main_search").style.display = "none";
   console.log("displayfade_in  ::  DONE Method()");
    this.replyObject.to = this.selectedTosList;
    this.replyObject.cc = this.selectedCcsList;
    let counter = 0;
    
    if(this.replyObject.reply_type != 'Done'){
        console.log("inif !=")
        document.getElementById("actionButton").disabled = true;  
    }
    if(this.replyObject.reply_type == 'Done'){
        console.log("inif ==")
        document.getElementById("doneActionButton").disabled = true;  
    }
      document.getElementById("cancelButton").disabled = true;       
    for (let task of this.selectedTasks) {
      document.getElementById("spinner").className="fa fa-refresh fa-spin";
      counter++;
      this.workflowService.getTask(task.workflowWorkItemID).subscribe(data => this.performSendAction(data, actionModal,counter));
    }

   
  } 

  closePopup(actionModal: any){
      if(document.getElementById("errorForMultiUsers"))
            document.getElementById("errorForMultiUsers").style.display = "none";  

    document.getElementById("displayfade_in").style.display = "none";
    document.getElementById("loader_main_search").style.display = "none"; 
    $('.modal-backdrop').hide();
    actionModal.close();
    
}


  performSendAction(task: any, actionModal: any, counter : any) {
      this.enableLoader = true;
      document.getElementById("displayfade_in").style.display = "block";
      document.getElementById("loader_main_search").style.display = "none";
    if (this.replyObject.reply_type == "Reassign") {
      this.inboxActionObject = new ReassignTask();
      (<any>Object).assign(this.inboxActionObject, task);
      //these are extra parameters which are currently not present in taskdetails nor user enters them.
      this.inboxActionObject.workflowInstruction = task.workflowInstruction;
      //this.inboxActionObject.workflowItemRootSender = task.workflowItemRootSender;
      this.inboxActionObject.workflowSender = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      this.inboxActionObject.workflowItemAction = "Reassign";
      this.inboxActionObject.workflowItemActionBy = this.current_user.employeeLogin
      this.inboxActionObject.workflowItemActionOnBehalf = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      this.inboxActionObject.workflowItemActionComment = encodeURIComponent(this.replyObject.message);
      this.inboxActionObject.workflowRecipientList = [];
      if(this.selectedTosList && this.selectedTosList.length > 1){
          this.enableLoader=false;
          document.getElementById("errorForMultiUsers").style.display = "block";  
          document.getElementById("spinner").className="";
          document.getElementById("actionButton").disabled = false;  
          document.getElementById("cancelButton").disabled = false; 
          //document.getElementById("displayfade_in").style.display = "none";
          document.getElementById("loader_main_search").style.display = "none";
          return false;
      }
      for (let task of this.selectedTosList) {
        this.workflowRecipientList = new WorkflowRecipientList();
        this.workflowRecipientList.workflowRecipient = task;
        this.workflowRecipientList.workflowWorkItemType = "to";
        this.inboxActionObject.workflowRecipientList.push(this.workflowRecipientList);
      }
      
      this.workflowService.reAssignWorkItem(this.inboxActionObject,this.selectedRecipient).subscribe(data =>  this.refreshSentItem(data,actionModal), error => {
          this.browseSharedService.emitMessageChange(error);
           this.enableLoader=false;
           document.getElementById("displayfade_in").style.display = "none";
           document.getElementById("loader_main_search").style.display = "none"; 
           $('.modal-backdrop').hide();
           if(this.router.url.indexOf('inbox') >= 0) {         
        this.router.navigate(['work-flow/inbox'] , { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        } 
        });
      //actionModal.close();
      //this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });
      /* if(this.router.url.indexOf('inbox') >= 0) {         
        this.router.navigate(['work-flow/inbox'] , { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        }
   */ } else if (this.replyObject.reply_type == "Forward") {
        
  
      
      this.inboxActionObject = new ForwardTask();
      (<any>Object).assign(this.inboxActionObject, task);

    
      
      if(this.existingAttachmentFiles != undefined){
          this.inboxActionObject.workflowAttachments = [];
      }
      
      for (let docId of this.selected_docs) {
        this.docIds.push(docId);
      }
      
      for (let task of this.docIds) {
        this.workflowAttachments = new WorkflowAttachments();
        this.workflowAttachments.workflowDocumentId = task.document_id;
        this.workflowAttachments.workflowAttachmentType = "Attachment";
        this.inboxActionObject.workflowAttachments.push(this.workflowAttachments);
      }
       
      if(this.existingAttachmentFiles != undefined){
          //this.inboxActionObject.workflowAttachments = [];
          for (i = 0; i < this.existingAttachmentFiles.length; i++) { 
            this.workflowAttachments = new WorkflowAttachments();
            this.workflowAttachments.workflowDocumentId = this.existingAttachmentFiles[i].workflowDocumentId;
            this.workflowAttachments.workflowAttachmentType = "Attachment";
            this.inboxActionObject.workflowAttachments.push(this.workflowAttachments);
         } 
      }
      
      
      this.inboxActionObject.workflowItemActionComment = encodeURIComponent(this.replyObject.message);
      this.inboxActionObject.workflowRecipientList = [];
      for (let task of this.selectedTosList) {
        this.workflowRecipientList = new WorkflowRecipientList();
        this.workflowRecipientList.workflowRecipient = task;
        this.workflowRecipientList.workflowWorkItemType = "to";
        this.inboxActionObject.workflowRecipientList.push(this.workflowRecipientList);
      }
      for (let task of this.selectedCcsList) {
        this.workflowRecipientList = new WorkflowRecipientList();
        this.workflowRecipientList.workflowRecipient = task;
        this.workflowRecipientList.workflowWorkItemType = "cc";
        this.inboxActionObject.workflowRecipientList.push(this.workflowRecipientList);
      }
      
      var e = document.getElementById("instruction");
      var strUser = "";
      if(e.options[e.selectedIndex] != undefined){
           strUser = e.options[e.selectedIndex].text;
          this.inboxActionObject.workflowInstruction = strUser;  
      }

      
      var e = document.getElementById("priority");
      var strPriority = e.options[e.selectedIndex].text;
          
      if(strPriority == "Normal")
          this.inboxActionObject.workflowItemPriority = 0;
  
      if(strPriority == "Low")
          this.inboxActionObject.workflowItemPriority = 1; 
  
      if(strPriority == "High")
          this.inboxActionObject.workflowItemPriority = 2;

      this.inboxActionObject.workflowItemAction = "Forward";
      this.inboxActionObject.workflowSender = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      this.inboxActionObject.workflowItemActionBy = this.current_user.employeeLogin;
      this.inboxActionObject.workflowItemActionOnBehalf = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      if (this.inboxActionObject.deadline) {
        this.inboxActionObject.deadline = this.inboxActionObject.deadline.formatted
      }
      this.formData = new FormData();
      this.formData.append('workflowJSONString', JSON.stringify({ workItemDetails: this.inboxActionObject }));

      this.formData.append('documentJSONString', JSON.stringify({
        properties: [
          { propertyName: "Subject", propertyValue: "Forward Subject" },
          { propertyName: "DocumentTitle", propertyValue: this.inboxActionObject.workflowItemSubject }
        ]
      }))
      this.formData.append('DocumentClass', 'Correspondence')
      var i = 1;
      for (let attachedFile of this.attachmentFiles) {
         const name = encodeURIComponent(attachedFile.name);
        //attachedFile = new File([attachedFile], name, { type: attachedFile.type });
        this.formData.append('file' + i, attachedFile,name);
        i++;
      }
      this.workflowService.forwardTaskItem(this.formData).subscribe(data => this.refreshSentItem(data,actionModal), error =>{
        this.browseSharedService.emitMessageChange(error);
        this.enableLoader=false;
        document.getElementById("displayfade_in").style.display = "none";
        document.getElementById("loader_main_search").style.display = "none"; 
        $('.modal-backdrop').hide();
        if(this.router.url.indexOf('inbox') >= 0) {         
     this.router.navigate(['work-flow/inbox'] , { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        } 
      });
      this.docIds = [];
      //actionModal.close();
     
    } else if (this.replyObject.reply_type == "Done") {
      this.inboxActionObject = new DoneTask();
      this.formData = new FormData();
      (<any>Object).assign(this.inboxActionObject, task);
      //this.inboxActionObject.workflowRecipientList = [];
      //this.inboxActionObject.workflowAttachments = [];
      for (let docId of this.selected_docs) {
        this.docIds.push(docId);
      }
      for (let task of this.docIds) {
        this.workflowAttachments = new WorkflowAttachments();
        this.workflowAttachments.workflowDocumentId = task.document_id;
        this.workflowAttachments.workflowAttachmentType = "Attachment";
        this.inboxActionObject.workflowAttachments.push(this.workflowAttachments);
      }
      this.inboxActionObject.workflowItemActionComment = encodeURIComponent(this.replyObject.message);
      this.inboxActionObject.workflowSender = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      this.inboxActionObject.workflowItemActionOnBehalf = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      this.inboxActionObject.workflowItemActionBy = this.current_user.employeeLogin;
      this.formData.append('workflowJSONString', JSON.stringify({ workItemDetails: this.inboxActionObject }));
      this.formData.append('documentJSONString', JSON.stringify({
        properties: [
          { propertyName: "EmailSubject", propertyValue: this.inboxActionObject.workflowItemSubject }, //SK 23-May subject to Email Subject
          { propertyName: "DocumentTitle", propertyValue: this.inboxActionObject.workflowItemSubject }

        ]
      }))
      this.formData.append('DocumentClass', 'Correspondence');
      var i = 1;
      for (let attachedFile of this.attachmentFiles) {
         const name = encodeURIComponent(attachedFile.name);
        //attachedFile = new File([attachedFile], name, { type: attachedFile.type });
        this.formData.append('file' + i, attachedFile,name);
        i++;
      }
      this.workflowService.doneTaskRequest(this.formData).subscribe(data => this.refreshInboxItem(data,counter,actionModal), error => {
        this.browseSharedService.emitMessageChange(error)
        this.enableLoader=false;
        document.getElementById("displayfade_in").style.display = "none";
        document.getElementById("loader_main_search").style.display = "none"; 
        $('.modal-backdrop').hide();
        if(this.router.url.indexOf('inbox') >= 0) {         
        this.router.navigate(['work-flow/inbox'] , { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        } 
     
      }
      
    );
     // this.inboxActionObject = new DoneTask();
     // this.enableLoader=false;
    //  document.getElementById("displayfade_in").style.display = "none";
     // document.getElementById("loader_main_search").style.display = "none";  
     // $('.modal-backdrop').hide();
      //actionModal.close();
      //this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime()} });
       /*
       if(this.router.url.indexOf('inbox') >= 0) {       
             this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });  
                 } else if(this.router.url.indexOf('sent-item') >= 0) {                
                         this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });  
                              }        else if(this.router.url.indexOf('archive') >= 0) {  
                                        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });     
                                       } else {           
                                            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });    
                                            }
      */
    } else if (this.replyObject.reply_type == "Add Users") {
      this.inboxActionObject = new AddUserTask();
      (<any>Object).assign(this.inboxActionObject, task);
      var e = document.getElementById("instruction");
      var strUser = "";
      if(e.options[e.selectedIndex] != undefined){
           strUser = e.options[e.selectedIndex].text;
          this.inboxActionObject.workflowInstruction = strUser;  
      }
      //this.inboxActionObject.workflowItemRootSender = task.workflowItemRootSender;
       this.inboxActionObject.workflowItemActionComment = encodeURIComponent(this.replyObject.message);
      this.inboxActionObject.workflowSender = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
        this.inboxActionObject.workflowItemActionOnBehalf = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
        this.inboxActionObject.workflowItemActionBy = this.current_user.employeeLogin;
      this.inboxActionObject.workflowRecipientList = [];
      for (let task of this.selectedTosList) {
        this.workflowRecipientList = new WorkflowRecipientList();
        this.workflowRecipientList.workflowRecipient = task; 
        this.workflowRecipientList.workflowWorkItemType = "to";
        this.inboxActionObject.workflowRecipientList.push(this.workflowRecipientList);
      }
      for (let task of this.selectedCcsList) {
        this.workflowRecipientList = new WorkflowRecipientList();
        this.workflowRecipientList.workflowRecipient = task;
        this.workflowRecipientList.workflowWorkItemType = "cc";
        this.inboxActionObject.workflowRecipientList.push(this.workflowRecipientList);
      }
      this.enableLoader=false;
      document.getElementById("displayfade_in").style.display = "none";
      document.getElementById("loader_main_search").style.display = "none";  
      $('.modal-backdrop').hide();
      this.workflowService.addUserTaskItem(this.inboxActionObject).subscribe(data =>  this.refreshAfterDone(data,actionModal), error => this.browseSharedService.emitMessageChange(error));
      //actionModal.close();
      //this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });
       if(this.router.url.indexOf('inbox') >= 0) {         this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        }
    }
  }
  fileChange(event) {
    for (let attachedFile of event.target.files) {
      this.attachmentFiles.push(attachedFile);
    }
  }

  clearFile(event){
     document.getElementById("choosedFile1").value="";
  }
    
  clearFile2(event){
     document.getElementById("choosedFile2").value="";
  } 
  removeAttachmentFromList(event: any, value: any, listType: any) {
    event.stopPropagation();
      
    if (listType == "alreadyAttached") {
        
        if(this.existingAttachmentFiles.length > 1){
            this.removedExistingDocument.push(value);
            this.existingAttachmentFiles.splice(this.existingAttachmentFiles.indexOf(value), 1);
            this.existingAttachmentFiles = this.existingAttachmentFiles.slice();
            this.selected_docs.splice(this.existingAttachmentFiles.indexOf(value), 1);
            this.selected_docs = this.selected_docs.slice();
        }
      //this.existingAttachmentFiles.splice(this.existingAttachmentFiles.indexOf(value), 1);
    } 
    if (listType == "attachment-List") {
      this.attachmentFiles.splice(this.attachmentFiles.indexOf(value), 1);
        this.attachmentFiles = this.attachmentFiles.slice()
    }
    if (listType == "Doc-List") {
      this.selected_docs.splice(this.selected_docs.indexOf(value), 1);
        this.selected_docs = this.selected_docs.slice();
    }
  }

  searchUser(terms: string, status: any) {
    this.filtertype = { name: terms }
    this.activeTab = status;
  }

  isToEnabled() {
    if(this.replyObject.reply_type === 'Reassign') {
        return true
    } else {
        if(this.is_cc_item) {
            if(this.replyObject.reply_type === 'Add Users') {
                if(this.parentType === 'CC')
                    return false;
                else
                    return true;
            }else {
                return false;
            }
         }  else {
                return true;
         }
    }
  }


  isCCEnabled() {
      if(this.replyObject.reply_type == 'Reassign')
        return false;
    /*if(this.is_cc_item) {
        if(this.replyObject.reply_type === 'Add Users') {
            if(this.parentType === 'CC')
                return false;
        }
    } */
    return true;
  }

  loadUsersForDirectorate(directorate: Directorate) {
    this.recipientsService.getUsersForDirectorate(directorate).subscribe();
  }

  loadDepartmentsForDirectorate(directorate: Directorate) {
    this.recipientsService.getDepartmentsForDirectorate(directorate).subscribe();
  }

  loadUsersForDepartment(department: Department,searchCrtieria : string) {
    if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
             window.document.getElementById("f_cc").disabled = false;
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             window.document.getElementById("f_to").disabled = false;
         }
         
         if((this.replyObject.reply_type == 'Add Users') || (!this.replyObject.reply_type)){
            this.is_add_user = true;
         } else
            this.is_add_user = false;
    this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
    this.recipientsService.getUsersForDepartment(department,searchCrtieria).subscribe(_res =>{
		this.enableUserSelection(_res);
	});
  }

  //added by Ravi Boni on 14-12-2017
  loadUsersForDepartment_1(department: Department,searchCrtieria : string) {
    if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
             window.document.getElementById("f_cc").disabled = false;
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             window.document.getElementById("f_to").disabled = false;
         }
         
         if((this.replyObject.reply_type == 'Add Users') || (!this.replyObject.reply_type)){
            this.is_add_user = true;
         } else
            this.is_add_user = false;
    this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
    this.recipientsService.getUsersForDepartment_1(department,searchCrtieria).subscribe(_res =>{
		this.enableUserSelection(_res);
	});
  }

  loadDivisionsForDepartment(department: Department) {
    this.recipientsService.getDivisionsForDepartment(department).subscribe();
  }

  loadUsersForDivision(division: Division,searchCrtieria : string) {
    if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
            if(window.document.getElementById("f_cc"))
             window.document.getElementById("f_cc").disabled = false;
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             if(window.document.getElementById("f_to"))
             window.document.getElementById("f_to").disabled = false;
         }
         if(this.replyObject.reply_type == 'Add Users'){
            this.is_add_user = true;
         } else
            this.is_add_user = false;
    this.userSearchUrl = global.base_url + "/EmployeeService/getDivisionUsers?division_code="+division.divisionCode+"&user_login="+this.current_user.employeeLogin+"&searchCrtieria=:keyword";
    this.recipientsService.getUsersForDivision(division,this.current_user.employeeLogin,searchCrtieria).subscribe(
        _res =>{
            this.enableUserSelection(_res);
        }
    );
     if(document.getElementById("searchCriteria")!= null){
            document.getElementById("searchCriteria").value="";
        } 
  }

  openLaunchPopup(launchModal: any,actions: any) {
    //$('#actions').modal('hide');
    this.workflowService.getAdvanceSearchProperties().subscribe(data => this.openAdvanceSearchModel(launchModal, data))
  }

  /*openAdvanceSearchModel(searchModal: any, advanceSearchProperties: any) {
    this.activeTab = 'simpleSearch';
      if(this.activeTab=='simpleSearch'){
          searchModal.modalClass="";
          }
    this.simpleSearchForm = new SimpleSearchForm();
    this.simpleSearchForm.dateCreated = '';
    this.documentSearchForm = new AdvanceSearchForm(advanceSearchProperties);
    this.advanceSearchApiValues = advanceSearchProperties;
    searchModal.open();
  }*/

  openAdvanceSearchModel(searchModal: any, advanceSearchProperties: any) {
    this.activeTab = 'simpleSearch';
    $('.modal-backdrop').show();
	$("body").addClass("modal-open");
      if(this.activeTab=='simpleSearch'){
          searchModal.modalClass="";
          } else {
              searchModal.modalClass="modal-lg"
          }
    this.simpleSearchForm = new SimpleSearchForm();
    this.simpleSearchForm.dateCreated = '';
    this.documentSearchForm = new AdvanceSearchForm(advanceSearchProperties);
    this.advanceSearchApiValues = advanceSearchProperties;
    searchModal.open();
  }

  simpleSearchDocuments() {
  if(this.simpleSearchForm.dateCreated != undefined){
       this.simpleSearchForm.dateCreated = this.simpleSearchForm.dateCreated.formatted;
    }
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
        }
        if(attribute =='DocumentTitle'){
           this.filter.filterCondition = "Like"   
        }
        this.filter.filterDataType = this.simpleSearchForm.attributeDataTypeMap()[attribute];
        this.filter.filterValue = encodeURIComponent(value)
        
        this.simpleSearchRequestObject.filter.push(this.filter)
      }
    }
      
    if(this.simpleSearchRequestObject.filter.length == 0){
          document.getElementById("filterMsg").style.display ="block";
          return;
      }else{
           document.getElementById("filterMsg").style.display ="none";
          this.ticks = 0;
  this.timer = Observable.timer(1000,1000);
 // subscribing to a observable returns a subscription object
  this.sub = this.timer.subscribe(t => this.tickerFunc(t));
    this.documentResults = []
    document.getElementById("loader").style.display = "block";
      }
    this.simpleSearchRequestObject.departmentId = this.current_user.employeeDepartment.departmentCode;
    this.browseService.SearchDocuments(this.simpleSearchRequestObject).subscribe(data => this.checkdata(data),error => this.checkerror(error));
    this.simpleSearchRequestObject = new SimpleSearchRequestObject();
    this.switchTab('selectDocuments')
  }
  
    tickerFunc(tick){
        this.ticks = tick
    }
  
  /*switchTab(activeTab) {
      
      if(activeTab=="default"){
         this.launchModal.modalClass="modal-lg";
        }
      else if(activeTab=="simpleSearch"){
         this.launchModal.modalClass="";  
      }
      else if(activeTab=="selectDocuments"){
           this.launchModal.modalClass="modal-lg";
          }
      if(activeTab=="default" && this.replyObject.reply_type == "Done"){
          this.launchModal.modalClass="";
      }
      
    this.activeTab = activeTab;
  }*/

  switchTab(activeTab) {
    
     if(activeTab=="default"){
        this.launchModal.modalClass="modal-lg";
       }
     else if(activeTab=="simpleSearch"){
        this.launchModal.modalClass=""; 
     }
     else if(activeTab=="selectDocuments"){
          this.launchModal.modalClass="modal-lg";
         } else if(activeTab=="advanceSearch") {
           this.launchModal.modalClass="modal-lg";           
         }
     if(activeTab=="default" && this.replyObject.reply_type == "Done"){
         this.launchModal.modalClass="";
     }
    
   this.activeTab = activeTab;
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
                
                    //this.selected_docs.pop(doc);
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
      
      
      
      /*if (e.currentTarget.checked == true) {
      this.selected_docs.push(doc);
    } else if (e.currentTarget.checked == false) {
      this.selected_docs.pop(doc);
      this.selected_docs.splice(this.selected_docs.indexOf(doc), 1);
    }*/
  }

  handleDocumentChange(doc){
      console.log("doc  :: ",doc)
    if(this.documentResults && this.documentResults.length > 0){
        for(var i = 0;i<this.documentResults.length; i++){
        
            if(this.documentResults[i].id == doc.id ){
                
                    this.documentResults[i] = doc;
                    this.documentResults = this.documentResults.slice();
        
            }
        
        
        }
        
    }
}

  advanceSearchDocuments(searchModal: any, $event: any) {
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
                   this.selected=[];
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
          this.ticks = 0;
          this.timer = Observable.timer(1000,1000);
          // subscribing to a observable returns a subscription object
          this.sub = this.timer.subscribe(t => this.tickerFunc(t));  
          this.documentResults = []
      
          if(document.getElementById("loader")!=null){
             document.getElementById("loader").style.display = "block";
          }
      }
    this.documentAdvanceSearchRequestObject.departmentId = this.current_user.employeeDepartment.departmentCode;  
    this.browseService.SearchDocuments(this.documentAdvanceSearchRequestObject).subscribe(data => this.checkdata(data),error => this.checkerror(error));
    this.switchTab('selectDocuments')
      ///multi
              this.selected=[];
    this.selectedDocumentFrom=[];
    this.selectedDocumentTo=[];
    this.selectedCorespondence=[];
  }

  actionToccClear(event) {
    event.stopPropagation();
    this.selectedTosList = []
    this.selectedCcsList = []
  }
    

   
  SelectedToRadio(e: any, user: User) {
    if (this.selectedTosList.indexOf(user.employeeLogin) == -1) {
      if (this.selectedCcsList.indexOf(user.employeeLogin) > -1) {
        this.selectedCcsList.splice(this.selectedCcsList.indexOf(user.employeeLogin), 1)
      }
      this.selectedTosList.push(user.employeeLogin)
    }
    if(this.wradio_none.includes(user.employeeLogin)){
        this.wradio_none.splice( this.wradio_none.indexOf(user.employeeLogin), 1 );
            }
  }
  SelectedNoneRadio(e: any, user: User) {
     /* if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
      if (this.selectedCcsList.indexOf(user.employeeLogin) > -1 || this.selectedTosList.indexOf(user.employeeLogin) > -1) {
          this.selectedCcsList.splice(this.selectedCcsList.indexOf(user.employeeLogin), 1)
          this.selectedTosList.splice(this.selectedTosList.indexOf(user.employeeLogin), 1)
      }
    }*/
      if(this.selectedTosList.includes(user.employeeLogin)){
      this.selectedTosList.splice( this.selectedTosList.indexOf(user.employeeLogin), 1 );
          
          }
    if(this.selectedCcsList.includes(user.employeeLogin)){
      this.selectedCcsList.splice( this.selectedCcsList.indexOf(user.employeeLogin), 1 );
          }
          if (this.selectedCcsList.indexOf(user.employeeLogin) == -1) {
            this.wradio_none.push(user.employeeLogin)
          }
  }
  SelectedCcRadio(e: any, user: User) {
    if (this.selectedCcsList.indexOf(user.employeeLogin) == -1) {
      if (this.selectedTosList.indexOf(user.employeeLogin) > -1) {
        this.selectedTosList.splice(this.selectedTosList.indexOf(user.employeeLogin), 1)
      }
      this.selectedCcsList.push(user.employeeLogin)
    }
    if(this.wradio_none.includes(user.employeeLogin)){
        this.wradio_none.splice( this.wradio_none.indexOf(user.employeeLogin), 1 );
            }
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
        if(this.is_cc_item){
            window.document.getElementById("f_cc").disabled = true;
            
        }else{
           window.document.getElementById("f_to").disabled = true; 
            if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
                window.document.getElementById("f_cc").disabled = true;
            }
        }
        if(this.replyObject.reply_type == 'Add Users'){
            this.is_add_user = true;
            //commented by Ravi Boni on 18-12-2017 for making 'To' text box is disabled.
            //window.document.getElementById("f_to").disabled = false; 
        } else
            this.is_add_user = false;
        window.document.getElementById("f_to").value='';
        window.document.getElementById("f_cc").value='';
        }

        disableToCC_1(){
            this.userSearchUrl = "";
            this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
            window.document.getElementById("f_to").value='';
            window.document.getElementById("f_cc").value='';
        }
        
    userSelected_docs: any[];
    openForward(){
        //$('#actions').modal('show');
        this.replyObject.subject = this.selected_docs[0].symbolicName;
        this.replyObject.name = "Correspondence";
        this.selected_docs = this.selected_docs.slice(); 
        //this.userSelected_docs =[];
        //this.userSelected_docs = this.selected_docs.slice();
        this.launchModal.open();
   }
    
    loadKNPCHierarchy(){
        console.log("this.departmentsAdv   :",this.departmentsAdv);
        this.isGlobalKNPCSearch=false;
        if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
            window.document.getElementById("f_cc").disabled = false;
        }
        if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
            window.document.getElementById("f_to").disabled = false;
        }
        
        if((this.replyObject.reply_type == 'Add Users') || (!this.replyObject.reply_type)){
           this.is_add_user = true;
        } else
           this.is_add_user = false;
   //this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
   //this.recipientsService.getUsersForDepartment(department,searchCrtieria).subscribe();

        this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
        this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.knpcHierarchy = data);
    }
    
    backpress(){
      this.switchTab('simpleSearch');
       this.simpleSearchForm = new SimpleSearchForm();
        if(document.getElementById("select_doc_button") !=null){
              document.getElementById("select_doc_button").style.display = "block";
        }
   }
    
     loadDeafultUserList(list : any,searchCrtieria :any){
         if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
            if(window.document.getElementById("f_cc")!=null){
                window.document.getElementById("f_cc").disabled = false;
            }
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             if(window.document.getElementById("f_to")!=null){
                window.document.getElementById("f_to").disabled = false;
             }
             
         }
         if(this.replyObject.reply_type == 'Add Users'){
            this.is_add_user = true;
         } else
            this.is_add_user = false;

        this.userSearchUrl = global.base_url + "/EmployeeService/loadDeafultUserList?listId="+list.listId+"&searchCrtieria=:keyword";
        this.recipientsService.loadDeafultUserList(searchCrtieria,this.current_user.employeeLogin,list).subscribe(data => {
            this.defaultUserList = data;
            this.enableUserSelection(data);
        });
        if(document.getElementById("userList")!= null){
            document.getElementById("userList").value="";
        }
    }
    
    filterUserList(list){
         this.searchCrtieria = document.getElementById("userList").value;
        
         this.loadDeafultUserList(list,this.searchCrtieria);
   }
    
    loadCrossDepartmentUsers(searchCrtieria :any){
        if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
             window.document.getElementById("f_cc").disabled = false;
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             window.document.getElementById("f_to").disabled = false;
         }
         if(this.replyObject.reply_type == 'Add Users'){
            this.is_add_user = true;
         } else
            this.is_add_user = false;
        this.userSearchUrl = global.base_url + "/EmployeeService/getCrossDepartmentUsers?searchCrtieria=:keyword";
        this.recipientsService.getCrossDepartmentUsers(searchCrtieria).subscribe(data => {
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
    
    loadKNPCHierarchyClear() {
        this.globalSearchCrtieria=undefined;
        document.getElementById("deptsearchCriteria2").value=''; 
        this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.knpcHierarchy = data);
    }

    loadKNPCHierarchyUsersForDepartment(department: Department,searchCrtieria : string) {
        if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
             window.document.getElementById("f_cc").disabled = false;
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             window.document.getElementById("f_to").disabled = false;
         }
         if(this.replyObject.reply_type == 'Add Users'){
            this.is_add_user = true;
         } else
            this.is_add_user = false;
        
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
        window.document.getElementById("f_to").value='';
        window.document.getElementById("f_cc").value='';
    }

    loadKNPCHierarchyUsersForDepartment_1(searchCrtieria : string) {
        this.isGlobalKNPCSearch = true;
        if(this.replyObject.reply_type == 'Forward' || this.replyObject.reply_type == 'Add Users'){
             window.document.getElementById("f_cc").disabled = false;
         }
         if(this.replyObject.reply_type != 'Done' && this.is_cc_item == false ){
             window.document.getElementById("f_to").disabled = false;
         }
         if(this.replyObject.reply_type == 'Add Users'){
            this.is_add_user = true;
         } else
            this.is_add_user = false;
        //this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment_1?searchCrtieria=:keyword";
        this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
        //this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => this.arrangeRequiredDept(data));
        this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => {    
            this.knpcHierarchy = data;
            this.enableUserSelection(data);
        });
        
        if(document.getElementById("deptsearchCriteria2")!= null){
            document.getElementById("deptsearchCriteria2").value="";
        }
        window.document.getElementById("f_to").value='';
        window.document.getElementById("f_cc").value='';
    }
    arrangeRequiredDept(data){
        this.knpcHierarchy = [];
        this.knpcHierarchy = data;
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
    
    checkerror(error){
        if(!(error.status==200)){ } 
         document.getElementById("loader").style.display = "none";
         document.getElementById("errortext").style.display = "block";
         if(document.getElementById("select_doc_button") != null){
            document.getElementById("select_doc_button").style.display = "none";
         }
         document.getElementById("backpress").style.marginLeft = "200px"; 
          document.getElementById("timeelapsed").style.display = "none"; 
         
         if(error.status==0){
           this.Errortext="No Internet Connection";  
         }else{
         this.Errortext="No Documents Found";
             }
         this.sub.unsubscribe();
         this.setTimeoutFunction(); //AMZ change
      }
    setTimeoutFunction(){
        setTimeout(function() {
            if(document.getElementById("errortext") != undefined){
                document.getElementById("errortext").style.display = "none";
            }
         }.bind(this), 2000);
       }

       
	setAttachmentDocHist(doc) {
		this.currentTab = 'History'
		this.selectedAttachmentID = doc.id +'~~'+ new Date();		
	}
	
    setAttachmentDoc(doc){
        this.currentTab = "Properties";
        console.log("selected Docuemnt ::  ",doc)
          this.selectedAttachmentID = doc.id;
          this.selectedDocument = doc;
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
     this.setTimeoutFunction(); //AMZ Change
    }
    
   SelectedRadio(e: any, taskRecipient: any){
       this.selectedRecipient = taskRecipient;
       this.recipientSelected = true;
   }
   
   refreshAfterDone(data : any ,actionModal : any){
    this.browseSharedService.emitMessageChange(data);
       actionModal.close();
       document.getElementById("cancelButton").disabled = false;
       document.getElementById("spinner").className="";
       this.browseSharedService.emitMessageChange(data);
       //this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });
        if(this.router.url.indexOf('inbox') >= 0) {         this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        }
   }
    
   refreshSentItem(data,actionModal){
       this.enableLoader = false;
       this.browseSharedService.emitMessageChange(data);
       document.getElementById("displayfade_in").style.display = "none";
       document.getElementById("loader_main_search").style.display = "none";
       $('.modal-backdrop').hide();
       actionModal.close();
       this.selectedListEmpty.emit();
       if(document.getElementById("cancelButton") !=undefined){
           document.getElementById("cancelButton").disabled = false;
       }
       if(document.getElementById("spinner") != undefined){
           document.getElementById("spinner").className="";
       }
       this.browseSharedService.emitMessageChange(data);
       //this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });
        if(this.router.url.indexOf('inbox') >= 0) {         this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        }
   }
  refreshAfterArchive(data){
      //this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });
       if(this.router.url.indexOf('inbox') >= 0) {         this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        }
  }
  refreshInboxItem(data : any,counter : any ,actionModal : any){
      //if(this.replyObject.reply_type == 'Done'){
        
        this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime()} });
       /* if(this.router.url.indexOf('inbox') >= 0) {
          this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });
        } else if(this.router.url.indexOf('sent-item') >= 0) {
         this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });
         }
         else if(this.router.url.indexOf('archive') >= 0) {
         this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });
         } else {
              if(this.router.url.indexOf('inbox') >= 0) {    
                       this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });  
             } else if(this.router.url.indexOf('sent-item') >= 0) {     
                    this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });  
             } else if(this.router.url.indexOf('archive') >= 0) {        
                 this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        
                } else {        
                        this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });    
                        }
         }*/
         this.enableLoader = false;
         this.browseSharedService.emitMessageChange(data);
         document.getElementById("displayfade_in").style.display = "none";
         document.getElementById("loader_main_search").style.display = "none";
         $('.modal-backdrop').hide();
         //this.browseSharedService.emitMessageChange(data);
         document.getElementById("doneActionButton").disabled = false;  
         actionModal.close();
    //}
       document.getElementById("cancelButton").disabled = false;
       document.getElementById("spinner").className="";
        this.browseSharedService.emitMessageChange(data);
        if(this.is_cc_item) {
        this.actionOptions = ['Forward'];
        this.is_archive_available = true;
      }
      if(counter == this.selectedTasks.length){
          if (this.replyObject.reply_type == "Done"){
              
              actionModal.close();
              //this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime()} });
               if(this.router.url.indexOf('inbox') >= 0) {         this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });       } else if(this.router.url.indexOf('sent-item') >= 0) {        this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });        }        else if(this.router.url.indexOf('archive') >= 0) {        this.router.navigate(['work-flow/archive'], { queryParams: { random: new Date().getTime() } });        } else {            this.router.navigate([this.router.url], { queryParams: { random: new Date().getTime() } });        }
          }
      }
        
    }
    
  launchReassign(data,action,actionModal){
      this.recipientList = [];
      for(let usr of data){
          if(this.selectedTasks[0].workflowItemRootSender == this.current_user.employeeLogin){
              this.recipientList.push(usr);
          }else if(usr.workflowRecipient == this.current_user.employeeLogin){
              this.recipientList.push(usr);
          }
      }
      if(this.recipientList.length == 0){
          this.recipientList = data;
      }
      if(this.router.url.indexOf('inbox') >= 0){
        this.isRecipentSelected = true;
        this.selectedRecipient = this.recipientList[0];
        this.recipientSelected = true;
    }
      this.replyObject.reply_type = action;
      actionModal.modalClass="modal-lg";
      actionModal.open();
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
       
		  if(this.deptThing)
			this.deptThing.isOpened = false
		  if(this.hierThing)
			this.hierThing.isOpened = false
		  if(this.crossThing)
				this.crossThing.isOpened = false
        /*this.userListThing.isOpened = true
         this.divisionsThing.isOpened = false
         this.deptThing.isOpened = false
         this.hierThing.isOpened = false
         this.crossThing.isOpened = false*/
         //added by Ravi Boni on 24-12-2017
         if(window.document.getElementById("f_to")!=null ){
            document.getElementById("f_to").value = '';
              
          }
          if(window.document.getElementById("f_cc")!=null){
            document.getElementById("f_cc").value = '';
          }
          //end
        }, 1000);
        
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
		  if(this.deptThing)
			this.deptThing.isOpened = false
		  if(this.hierThing)
			this.hierThing.isOpened = false
		  if(this.crossThing)
                this.crossThing.isOpened = false
                

            /*this.divisionsThing.isOpened = true
            this.deptThing.isOpened = false
            this.hierThing.isOpened = false
            this.crossThing.isOpened = false*/

            if(window.document.getElementById("f_to")!=null ){
               document.getElementById("f_to").value = '';
                 
             }
             if(window.document.getElementById("f_cc")!=null){
               document.getElementById("f_cc").value = '';
             }
             //end
           }, 100);
		
	}

    
   selectAll(userList : any, recpType : any){
       for (var i = 0; i < userList.length; i++) { 
           if(recpType=="to"){
               console.log('userList[i].employeeLogin   :', userList[i].employeeLogin);
                this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"to"));
               //document.getElementsByName(userList[i].employeeLogin+suffix).checked = true;
               if (this.selectedTosList.indexOf(userList[i].employeeLogin) == -1) {
                   this.selectedTosList.push(userList[i].employeeLogin);
               }
               if(this.selectedCcsList.includes(userList[i].employeeLogin)) {
                   this.selectedCcsList.splice( this.selectedCcsList.indexOf(userList[i].employeeLogin), 1 );
               }
               if(this.wradio_none.includes(userList[i].employeeLogin)) {
                this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
            }
           }else if(recpType=="cc"){
            this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"cc"));
               if (this.selectedCcsList.indexOf(userList[i].employeeLogin) == -1) {
                    this.selectedCcsList.push(userList[i].employeeLogin);
               }
               if(this.selectedTosList.includes(userList[i].employeeLogin)) {
                   this.selectedTosList.splice( this.selectedTosList.indexOf(userList[i].employeeLogin), 1 );
               }
               if(this.wradio_none.includes(userList[i].employeeLogin)) {
                this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
            }
           }else if(recpType=="none"){
                this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"none"));
                if (this.wradio_none.indexOf(userList[i].employeeLogin) == -1) {
                    this.wradio_none.push(userList[i].employeeLogin);
                }
               if(this.selectedTosList.includes(userList[i].employeeLogin)) {
                   this.selectedTosList.splice( this.selectedTosList.indexOf(userList[i].employeeLogin), 1 );
               }
               if(this.selectedCcsList.includes(userList[i].employeeLogin)) {
                   this.selectedCcsList.splice( this.selectedCcsList.indexOf(userList[i].employeeLogin), 1 );
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
                console.log('userList[i].employeeLogin   :', userList[i].employeeLogin);
                 this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"to"));
                //document.getElementsByName(userList[i].employeeLogin+suffix).checked = true;
                if (this.selectedTosList.indexOf(userList[i].employeeLogin) == -1) {
                    this.selectedTosList.push(userList[i].employeeLogin);
                }
                if(this.selectedCcsList.includes(userList[i].employeeLogin)) {
                    this.selectedCcsList.splice( this.selectedCcsList.indexOf(userList[i].employeeLogin), 1 );
                }
                if(this.wradio_none.includes(userList[i].employeeLogin)) {
                 this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
             }
            }else if(this.recpntType=="cc"){
             this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"cc"));
                if (this.selectedCcsList.indexOf(userList[i].employeeLogin) == -1) {
                     this.selectedCcsList.push(userList[i].employeeLogin);
                }
                if(this.selectedTosList.includes(userList[i].employeeLogin)) {
                    this.selectedTosList.splice( this.selectedTosList.indexOf(userList[i].employeeLogin), 1 );
                }
                if(this.wradio_none.includes(userList[i].employeeLogin)) {
                 this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
             }
            }else if(this.recpntType=="none"){
                 this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"none"));
                 if (this.wradio_none.indexOf(userList[i].employeeLogin) == -1) {
                     this.wradio_none.push(userList[i].employeeLogin);
                 }
                if(this.selectedTosList.includes(userList[i].employeeLogin)) {
                    this.selectedTosList.splice( this.selectedTosList.indexOf(userList[i].employeeLogin), 1 );
                }
                if(this.selectedCcsList.includes(userList[i].employeeLogin)) {
                    this.selectedCcsList.splice( this.selectedCcsList.indexOf(userList[i].employeeLogin), 1 );
                }
            }
        }

     }

    enableUserSelection(userArr:any){
		var _ref = this;
		setTimeout(function() {
			for(var i=0;i<userArr.length ; i++){
				if(_ref.selectedTosList.indexOf(userArr[i].employeeLogin) >= 0){
					_ref.checkedAllWithSameClassName(document.getElementsByClassName(userArr[i].employeeLogin+"to"));
				}else if(_ref.selectedCcsList.indexOf(userArr[i].employeeLogin)>=0){
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
    
    backpress(launchModal :any){
        if(document.getElementById("advFilterMsg") != undefined){
    document.getElementById("advFilterMsg").style.display ="none";
    }
      
    if(document.getElementById("filterMsg")){
        document.getElementById("filterMsg").style.display ="none";
    }
      this.switchTab('simpleSearch');
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
    
    addExistingDocument(data){
       this.existingAttachmentFiles = data.workflowAttachments;
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
    checkUserAndDelegateJobTitle(){
		if(localStorage.getItem("isDelegate") == "true"){
			return JSON.parse(JSON.parse(localStorage.getItem("selectedDelegateUser"))).EmpJobTitle;
		  }else{
			return this.userService.getCurrentUser().employeeJobTitle;
		  }
		}
		checkJobTitle(){
			return this.checkUserAndDelegateJobTitle() == 'CRDEP' || this.checkUserAndDelegateJobTitle() == 'DCEO' || this.checkUserAndDelegateJobTitle() == 'CEO' || this.checkUserAndDelegateJobTitle() == 'TL' || this.checkUserAndDelegateJobTitle() == 'MGR' || this.checkUserAndDelegateJobTitle() == 'DIMS';
        }
        checkActionOption(option:any){
            return (this.selectedTasks.length > 1 && option !='Reassign');
        }
        haveOneSelectedTask(){
            return (this.selectedTasks.length == 1);
        }
        showActions(){
            return (this.is_for_buttons != true && (this.actionOptions.length >0 || this.is_archive_available==true || this.is_complete_available==true) );
        }
}

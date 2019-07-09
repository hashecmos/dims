//Libraries
import {Http, RequestOptions, Headers} from '@angular/http';
import {Component, ViewChild} from '@angular/core'
import {PopoverModule} from "ng2-popover";
import {Router, ActivatedRoute} from '@angular/router'
import {Location} from '@angular/common';
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';
//Serivces
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {BrowseService} from '../../services/browse.service'
import {RecipientsService} from '../../services/recipients.service'
import {UserService} from '../../services/user.service'
import {BrowseSharedService} from '../../services/browseEvents.shared.service'
//Models
import {LaunchPrimaryDoc} from '../../model/launchPrimaryDoc.model'
import {TaskDetail} from '../../model/taskDetail.model';
import {ForwardTask} from '../../model/forwardInbox.model';
import {AddUserTask} from '../../model/addUserInbox.model';
import {DoneTask} from '../../model/inboxAction.model';
import {ReassignTask} from '../../model/reassigninbox.model';
import {WorkflowRecipientList} from '../../model/workflowRecipientList.model';
import {Directorate} from '../../model/recipients.model'
import {User} from '../../model/userRecipients.model'
import {Department} from '../../model/departmentRecipients.model'
import {Division} from '../../model/divisionRecipients.model'
import {WorkflowAttachments} from '../../model/workflowAttachments.model'
import {DocumentInfo} from '../../model/documentInfo.model'
//Ravi Boni Added
import {SearchService} from '../../services/search.service'
import {SharedService} from '../../services/advanceSearchTasks.service'
//end
import global = require('./../../global.variables')
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'task-details',
  providers: [WorkflowService, RecipientsService,SearchService],
  templateUrl: 'app/WorkFlowComponent/Inbox/taskDetails.component.html'
})
export class TaskDetails {
  @ViewChild('userlistModal') public userListModal;
  private LaunchPrimaryDoc: LaunchPrimaryDoc;
  private activeTab: string
  private actionSubmissionParams
  private attachmentFiles: any = [];
  private base_url: string;
  private os_name: string;
  private directorates: any = [];
  private availableActions: string[]
  private attachedDocs: any = [];
  public currentTask: TaskDetail = new TaskDetail();
  private radio_to: any = [];
  private radio_cc: any = [];
  private callingTab: string
  private historyDetails: any = [];
  selectedDocument: any;
  private selectedAttachmentID: string
  private current_user: any;
  private currentDelegateForEmployeeLogin: string = "";
  private fromPageName: string = "";

  private is_archive_available: boolean = false;
  private is_cc_item: boolean = false;
  private is_done_by_sub_item: boolean = false;
  private is_complete_available :boolean;
  private is_launch_available : boolean;
  private selectedTasks: any=[];
  private snapshot : any;
  private viewer_url : any;
  private delegateLogin: any;
  private actionList: any=[];
  private queue: string ;
  currentTab: string;
  private subscription: ISubscription[] = [];
  private taskSubs: ISubscription;

  constructor(private route:ActivatedRoute,private http: Http, private router: Router, private workflowService: WorkflowService,
    private searchService: SearchService,private sharedService: SharedService,
    private recipientsService: RecipientsService, private userService: UserService,
    private browseService: BrowseService, private browseSharedService: BrowseSharedService) {
    this.base_url = global.base_url;
    this.os_name = global.os_name;
    this.viewer_url = global.viewer_url;
    this.snapshot = route.snapshot;
    this.current_user = this.userService.getCurrentUser();
    this.delegateLogin = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }
    this.subscription.push(this.userService.notifyDelegateForChange.subscribe(data => this.backClicked()));
    this.actionSubmissionParams = new ForwardTask()
  }

  ngOnInit() {
    this.actionList = [];
    this.subscription.push(this.browseSharedService.emitSearchedDocsFromRepo$.subscribe(docs => this.setDocsList(docs)));
    let subscription = this.router.events.subscribe((val) => {
      this.fromPageName = val.url.split('/')[val.url.split('/').indexOf("task-details") - 1]
      this.subscription.push(this.workflowService.getTask(val.url.slice(val.url.indexOf('task-details/') + 13)).subscribe(data => this.assignCurrentTaskVal(data)));
      subscription.unsubscribe();
    });
  }

  ngOnDestroy() {
    for(let subs of this.subscription) {
      subs.unsubscribe();
    }
  }

  assignCurrentTaskVal(data: any) {
    this.currentTask.assignAttribiteValuesFromData(data);
    var attachmentIds = [];
    for (let attachment of this.currentTask.workflowAttachments) {
      attachmentIds.push(attachment.workflowDocumentId)
    }
    this.subscription.push(this.browseService.loadDocumentsFileProperties(attachmentIds).subscribe(data => this.currentTask.assignMimeTypeDataToAttachments(data)));
    this.subscription.push(this.workflowService.getItemsHistory(this.currentTask.workflowWorkItemID, null).subscribe(h => this.historyDetails = h));
    this.selectedTasks.push(this.currentTask);
    console.log("selectedTasks  ::: ",this.selectedTasks);
    if (this.snapshot.routeConfig.path.includes("inbox")) {
      this.queue = "INBOX";
    }else if (this.snapshot.routeConfig.path.includes("sent")) {
      this.queue = "SENT";
    }else if (this.snapshot.routeConfig.path.includes("archive")) {
      this.queue = "ARCHIVE";
    }

    if(this.snapshot.routeConfig.path.includes("sent") || this.snapshot.routeConfig.path.includes("inbox")){
        this.workflowService.getWorkitemActions(this.currentTask.workflowWorkItemID, this.queue, this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(alist => this.actionList = alist);
    }else{
        this.actionList =['Forward','Launch'];
    }
    
    if(this.selectedTasks[0].workflowWorkItemType == 'cc') {
       this.is_cc_item = true;
    }
    
    // this.is_launch_available = true;
    // if(this.selectedTasks[0].workflowWorkItemType == 'cc') {
    //   this.is_cc_item = true;
    //   if(this.selectedTasks[0].workflowItemStatus =="Archive"){
    //     this.is_archive_available = false;
    //   }else{
    //     this.is_archive_available = true;
    //   }
    //   this.is_complete_available = false;
    // }
    // if(this.selectedTasks[0].workflowWorkItemType == 'Reply') {
    //   this.is_done_by_sub_item = true;
    // }
    // if(this.current_user.employeeLogin == this.delegateLogin && this.selectedTasks[0].workflowItemRootSender == this.current_user.employeeLogin) {
    //     this.is_complete_available = true;
    // }else if(this.current_user.employeeLogin != this.delegateLogin && (this.selectedTasks[0].workflowItemRootSender == this.delegateLogin && this.selectedTasks[0].workflowWorkItemType!='Reply')){
    //     this.is_complete_available = true;
    // }else{
    //     if(this.selectedTasks[0].workflowWorkItemType == 'Reply') {
    //         if(this.current_user.employeeLogin != this.delegateLogin && (this.selectedTasks[0].workflowItemRootSender == this.delegateLogin)){
    //             this.is_complete_available = true;
    //         }
    //     }else{
    //         this.is_complete_available = false;
    //     }
        
    // }
    // if(this.is_complete_available)
    //   this.is_archive_available = false;
  }



  //************ view helpers *************

  openDocSearchModel() {
    this.browseSharedService.openDocSearchModelPopup("task_detail_launch_popup_msg");
  }

  setDocsList(selectedDocs: any) {
    this.attachedDocs = selectedDocs;
  }

  setAttachmentID(id) {
    this.selectedAttachmentID = id +'~~'+ new Date();
  }

  backClicked() {
      //this.router.navigate(['work-flow/'+this.fromPageName]);
      
      if(localStorage.getItem('workwfSearch') === 'WorkWFSearch') {
            this.router.navigateByUrl('/work-flow/task-search');   
            //localStorage.removeItem('workwfSearch'); 
            let searchedWfs = JSON.parse(localStorage.getItem('searchedWorkflowsP'));
            this.SearchTaskItems(searchedWfs);
        }
        else {
            this.router.navigateByUrl('/work-flow/'+this.fromPageName);
        }
      
  }
    
    
    //Added by RaviBoni
    SearchTaskItems(workflowSearch) {
    document.getElementById("displayLoader").style.display="block";
    //this.subscription.push(this.searchService.getSearchTaskItems(workflowSearch).subscribe(data => this.emitSearchedTaskItems(data)));
    this.taskSubs = this.searchService.getSearchTaskItems(workflowSearch).subscribe(data => this.emitSearchedTaskItems(data));

   // searchModal.close(); archive
        
    // set input fields in model to empty
    //document.getElementById("instruction").options.selectedIndex = -1;
    
  }

  emitSearchedTaskItems(data) {
    //this.taskItems = data;
    document.getElementById("displayLoader").style.display="none"; 
    this.sharedService.emitTasks(data)
    this.taskSubs.unsubscribe();
  }
    
  //end  

  openDocumentActionPopup(document: any, actionPopup: any) {
    var docIds = [];
    var docObj: DocumentInfo = new DocumentInfo();
    docObj.document_id = document.workflowDocumentId;
    docObj.subject = document.fileName;
    docIds.push(docObj);
    this.setAttachmentID(document.id);
    actionPopup.openPopup(docIds);
  }

  sendMail(documentId: any, docName: any) { 
    this.subscription.push(this.browseService.getOutlookAttachmentDocument(documentId, this.os_name).subscribe(data => {
      var theApp = new ActiveXObject("Outlook.Application");
      var theMailItem = theApp.CreateItem(0);
      console.log(data);
      console.log(data.text());
      theMailItem.Body = "This is an Automated message sent to you using DIMS";
      //theMailItem.BodyFormat = 2;
      //theMailItem.HTMLBody = "<HTML><BODY>This is an Automated message sent to you using DIMS </BODY></HTML>";
      theMailItem.Subject = docName;
      theMailItem.CC = this.current_user.employeeEmail;
      theMailItem.Attachments.Add(data.text());

      theMailItem.display();
    }));
  } 

  viewDocument(fileToView: string){
        window.open(fileToView, '_blank', 'fullscreen=no');
       
  }
    
  downloadDocument(downloadDocument: string){
        window.location.assign(downloadDocument);
  }
    
  completeTask(event: any) {
    if (event == "complete") {
      this.subscription.push(this.workflowService.completeTask(this.selectedTasks[0].workflowWorkItemID).subscribe(data => this.afterComplete(data), error => this.browseSharedService.emitMessageChange(error)));
    }
  }
  afterComplete(data: any){
    
    this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } });
    this.browseSharedService.emitMessageChange(data);
  }
  
  fetchHistory(){
    this.subscription.push(this.workflowService.getItemsHistory(this.currentTask.workflowWorkItemID, null).subscribe(h => this.updateHistoryValue(h))); 
  }
  
  getWorkflowHistroyByDept(){
    this.subscription.push(this.workflowService.getItemsHistoryByDept(this.currentTask.workflowWorkItemID, null).subscribe(h => this.updateHistoryValue(h))); 
  }
  updateHistoryValue(data){
    this.historyDetails = [];
    this.historyDetails = data;
  
  }
  handleDocumentChange(doc){
    if(this.currentTask.workflowAttachments && this.currentTask.workflowAttachments.length > 0){
        for(var i = 0;i<this.currentTask.workflowAttachments.length; i++){
        
            if(this.currentTask.workflowAttachments[i].id == doc.id ){
                
                    this.currentTask.workflowAttachments[i].fileName = doc.subject;
                    this.currentTask.workflowAttachments = this.currentTask.workflowAttachments.slice();
        
            }
        
        
        }
        
    }
}
setAttachmentDOC(doc){
  this.currentTab ="Properties";
  console.log("docu ::::  ",doc)
  this.setAttachmentID(doc.workflowDocumentId);
  doc.subject = ' ';
  this.selectedDocument = doc;
}
}

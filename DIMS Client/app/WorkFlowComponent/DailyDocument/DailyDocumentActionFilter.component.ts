//libraries
import {Component, Input, Output, EventEmitter} from '@angular/core'
import {Router, ActivatedRoute} from '@angular/router'
//models
import {LaunchWorkflow} from '../model/launchWorkflow.model'
import {DocumentResult} from '../model/documentResult.model'
//services
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {BrowseService} from '../../services/browse.service'
import {UserService} from '../../services/user.service'
import {BrowseSharedService} from '../../services/browseEvents.shared.service'

import global = require('./../../global.variables')
 
@Component({
  selector: 'action-btn-daily-document',
  templateUrl: 'app/WorkFlowComponent/DailyDocument/DailyDocumentActionFilter.component.html'
})
export class ActionBtnDailyDocument {
  private base_url: string;
  private os_name: string;
  private removeDocumentId : string;
  private itemIds : any; 
  private multiDocIds : any;
  private multiSubject : any;
  private currentUser : any;
  private snapshot : any;
  @Input() selectedDocuments: any
  @Input() selectedFolders: any
  @Output() notifytoSetAttachmentId: EventEmitter<any> = new EventEmitter();

  constructor(private browseService: BrowseService, private browseSharedService: BrowseSharedService,
    private router: Router,private route: ActivatedRoute, private workflowService: WorkflowService,
    private userService: UserService) {
    this.selectedDocuments = []
    this.selectedFolders = []
    this.itemIds = []
    this.multiDocIds ="";
    this.snapshot = route.snapshot;
    this.base_url = global.base_url
    this.os_name = global.os_name;
    this.currentUser = this.userService.getCurrentUser();
  }
  //************** view helpers *************

  isSingleDocSelected() {
    let result = this.selectedDocuments.length > 1 ? true : false
    return result
  }

  ifFolderSelected() {
    return (this.selectedFolders.length > 0)
  }


  openLaunchModel() {
      $('.modal-backdrop').show();
	$("body").addClass("modal-open");
    let documents = this.selectedDocuments.slice(0) // slice is create a copy of the array and pass-by-value
    this.browseSharedService.emitBrowseLaunchPopupOpen(documents);
localStorage.setItem('isLaunchFromDailyDocs',true);
  }

  notifyToDisplayProperties() {
    if (this.selectedDocuments.length == 1) {
      this.notifytoSetAttachmentId.emit(this.selectedDocuments[0].id)
    }
  }

  openDocumentActionPopup(documentId: string, actionPopup: any) {
    var docs = []
    console.log("documentId :: ",documentId)
    for(var i =0; i<documentId.length ; i++)
        docs.push(documentId[i]);

    actionPopup.openPopup(docs);
  }
    
  openRemoveModal(selectedDocuments: any,completeActionModal: any){
      completeActionModal.open();
  }
  
  removeDocument(selectedDocuments:any ,completeActionModal:any){
      /* for (var i = 0; i < selectedDocuments.length; i++) {
           this.browseService.removeDailyDocument(selectedDocuments[i].id).subscribe(data =>data);
       }*/
      
      for (var i = 0; i < selectedDocuments.length; i++) {
          this.itemIds.push(selectedDocuments[i].id);
      }
      
      this.browseService.removeDailyDocument(this.itemIds).subscribe(data =>this.refresh(data));
  }
    
  closeAction(completeActionModal){
      completeActionModal.close();
  }
  
  downloadDocuments(selectedDocuments){
      for (var i = 0; i < selectedDocuments.length; i++) {
          window.location = this.base_url+'/FilenetService/downloadDocument?docId='+selectedDocuments[i].id+'&osName='+this.os_name
      }
  }
  
  refresh(data){
        this.router.navigate(['work-flow/dailydocument'], { queryParams: { random: new Date().getTime() } })
      }
    
  viewDocument(fileToView: string){
        window.open(fileToView, '_blank', 'fullscreen=no');
  } 
    
  downloadMultiDocument(selectedDocuments :any){
      //[attr.href]="base_url+'/FilenetService/downloadDocument?docId='+selectedDocuments[0]?.id+'&osName='+os_name"
      this.multiDocIds ="";
      /*for (var i = 0; i < selectedDocuments.length; i++) {
          this.multiDocIds.push(selectedDocuments[i].id);
      }
      
      this.browseService.downloadMultiDocument(this.multiDocIds).subscribe(data =>this.refresh(data));*/
      for (var i = 0; i < selectedDocuments.length; i++) {
          this.multiDocIds = this.multiDocIds+selectedDocuments[i].id+",";
      }
       window.location.assign(this.base_url+'/FilenetService/downloadMultiDocuments?docIds='+this.multiDocIds+'&osName='+this.os_name)
  }

  sendMail(selectedDocuments: any) { 
      this.multiDocIds ="";
      this.multiSubject = "";
      for (var i = 0; i < selectedDocuments.length; i++) {
          this.multiDocIds = this.multiDocIds+selectedDocuments[i].id+",";
      }
      this.multiSubject = 'Multi-documents';
      if(selectedDocuments.length == 1)
        this.multiSubject = selectedDocuments[0].subject;

    this.browseService.getOutlookAttachmentDocument(this.multiDocIds, this.os_name).subscribe(data => {
      var theApp = new ActiveXObject("Outlook.Application");
      var theMailItem = theApp.CreateItem(0);
      console.log(data);
      console.log(data.text());
      theMailItem.Body = "This is an Automated message sent to you using DIMS";
      //theMailItem.BodyFormat = 2;
      //theMailItem.HTMLBody = "<HTML><BODY>This is an Automated message sent to you using DIMS </BODY></HTML>";
      theMailItem.Subject = this.multiSubject;
      theMailItem.CC = this.currentUser.employeeEmail;
      theMailItem.Attachments.Add(data.text());

      theMailItem.display();
    });
  } 
}

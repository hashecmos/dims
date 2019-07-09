//libraries
import {Component, Input, Output, EventEmitter} from '@angular/core'
import {Router, ActivatedRoute} from '@angular/router'
//models
import {LaunchWorkflow} from '../model/launchWorkflow.model'
import {DocumentResult} from '../model/documentResult.model'
//services
import {WorkflowService} from '../services/workflowServices/workflow.service'
import {BrowseService} from '../services/browse.service'
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import {UserService} from '../services/user.service'

import global = require('./../global.variables')

@Component({
  selector: 'action-btn-document',
  templateUrl: 'app/BrowseComponent/DocumentActionFilter.component.html'
})
export class ActionBtnDocument {
  private base_url: string;
  private os_name: string;
  private newFolderOptions: boolean = true;
     private multiDocIds : any;
     private multiSubject : any;
     private currentUser : any;
  @Input() selectedDocuments: any
  @Input() selectedFolders: any
  @Output() notifyParenttocheckout: EventEmitter<any> = new EventEmitter();
  @Output() notifyParentToOpenDocumentLink: EventEmitter<any> = new EventEmitter();
  @Output() notifyParentToCancelCheckout: EventEmitter<any> = new EventEmitter();
  @Output() notifytoSetAttachmentId: EventEmitter<any> = new EventEmitter();

  constructor(private browseService: BrowseService, private browseSharedService: BrowseSharedService,
    private router: Router, private workflowService: WorkflowService,route : ActivatedRoute,
    private userService: UserService) {
      this.multiDocIds ="";
     var snapshot = route.snapshot;
    switch (snapshot.routeConfig.path) {
    case "browse/document-search":
          this.newFolderOptions = false;
    }
    this.selectedDocuments = []
    this.selectedFolders = []
    this.base_url = global.base_url
    this.os_name = global.os_name;
    this.currentUser = this.userService.getCurrentUser();
  }
  //************** view helpers *************

  isSingleDocSelected() {
    let result = this.selectedDocuments.length > 1 ? true : false
    return result
  }
isMoveToEnabled(){
  var result=false;
for(var index =0;index<this.selectedDocuments.length;index++){
//console.log('this.selectedDocuments[index].createdBy :',this.selectedDocuments[index].createdBy);
//console.log('this.currentUser.employeeLogin :',this.currentUser.employeeLogin);
	  if(this.selectedDocuments[index].createdBy.toUpperCase()===this.currentUser.employeeLogin.toUpperCase()){
	 // console.log('Inside if ::value false ');
	  result =false;
	  }else{
	// console.log('Inside else  ::value true ');
	  return true;
	  }
	  }
	 //  console.log('Returned result',result);
	   return result;
	  }
  ifFolderSelected() {
    return false;
  }

  CheckInActionText() {
    let result = this.selectedDocuments[0].reserved ? "CheckIn" : "Checkout"
    return result
  }

  checkoutDoc() {
    this.notifyParenttocheckout.emit();
  }

  cancelCheckout() {
    this.notifyParentToCancelCheckout.emit();
  }

  openLaunchModel() {
    $('.modal-backdrop').show();
	  $("body").addClass("modal-open");
    let documents = this.selectedDocuments.slice(0) // slice is create a copy of the array and pass-by-value
    this.browseSharedService.emitBrowseLaunchPopupOpen(documents);
  }

  notifyToDisplayProperties() {
    if (this.selectedDocuments.length == 1) {
      this.notifytoSetAttachmentId.emit(this.selectedDocuments[0].id)
    }
  }

  openDocumentLink() {
    this.notifytoSetAttachmentId.emit(this.selectedDocuments[0].id)
    this.notifyParentToOpenDocumentLink.emit();
  }
  
  downloadMultiDocument(selectedDocuments :any){
      this.multiDocIds ="";
      for (var i = 0; i < selectedDocuments.length; i++) {
          this.multiDocIds = this.multiDocIds+selectedDocuments[i].id+",";
      }
       window.location.assign(this.base_url+'/FilenetService/downloadMultiDocuments?docIds='+this.multiDocIds+'&osName='+this.os_name)
  }
    
  openDocumentActionPopup(documentId: string, actionPopup: any) {
    actionPopup.openPopup(documentId);
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

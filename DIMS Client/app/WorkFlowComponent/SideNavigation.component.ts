import {Component,Output, EventEmitter} from '@angular/core'
import {Router} from '@angular/router';
import {WorkflowService} from './../services/workflowServices/workflow.service'
import {BrowseSharedService} from './../services/browseEvents.shared.service'
import {UserService} from '../services/user.service'
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'side-navigation',
  templateUrl: 'app/WorkFlowComponent/SideNavigation.component.html'
})

export class SideNavigation{
  private inboxFolders : any;
  private sentItemsFolders : any;
  private archiveFolders : any;
  private activeFolderFilter
  private current_user: any; 
  private isSecretary : boolean = false;
  private selectedFolder : any;
  private selectedInboxFolder : any;
  private showSearchResults = true;
  private selectedArchiveFolder : any;
  private delegateLogin: any;   
  private selectTab: string;
  private subscription: ISubscription[] = [];

  constructor(private router: Router,private workflowService: WorkflowService, private sharedService: BrowseSharedService, private userService: UserService){
    this.current_user = this.userService.getCurrentUser();
    this.delegateLogin = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
    
    this.inboxFolders = []
    this.sentItemsFolders = []
    this.archiveFolders = []
    this.subscription.push(this.workflowService.getDelegateUsers1(this.current_user.employeeLogin).subscribe(data => this.setDelegatedSecretary(data)));
    //this.subscription.push(this.workflowService.isSecretary().subscribe(data => this.setIsSecretary(data)));
    this.subscription.push(this.userService.notifyDelegateForChange.subscribe(data => {console.log('Notifying on delegateChange');
    this.getInboxFolders('inbox');
    this.getSentItemsFolders('sent');
    this.getArchiveFolders('archive');
    }));
  }

  
  public delegatedUsers1: any[];
  public delUserLogin: string;
  setDelegatedSecretary(data){
    console.log("Swithching USER   :",data);
    var isHaveInbox = localStorage.getItem("isCurrentUserHaveInbox");
    var isHaveReports = localStorage.getItem("isCurrentUserHaveReports");
   let delegateUserData = [];
    for (var i = 0; i < data.length; i++) {
        var folderDetailArray = data[i].split("!~");
        var folderDetail = {
            "userLogin": folderDetailArray[0],
            "userFullName":folderDetailArray[1],
            "EmpJobTitle":folderDetailArray[2].trim(),
            "EmpIsHaveReports":folderDetailArray[3]+"".trim(),
            "DelegateduserHaveAccessForReports":folderDetailArray[4]+"".trim(),
        }
        delegateUserData[i] = folderDetail;
    } 
    console.log("delegateUserData   :",delegateUserData)
      this.delegatedUsers1 = delegateUserData;  
      if(isHaveInbox != "1"){
          if( this.delegatedUsers1 && this.delegatedUsers1.length>0 ){ 
              this.delUserLogin = this.delegatedUsers1[0].userLogin;
              console.log("DELEGATE USER   :",this.delUserLogin);
          }
      }
      this.subscription.push(this.workflowService.isSecretary1(this.delUserLogin).subscribe(data => this.setIsSecretary(data)));
  }


  ngOnInit(){
    this.activeFolderFilter = ""
    this.getInboxFolders('inbox')
    this.getSentItemsFolders('sent')
    this.getArchiveFolders('archive')
    
    // if(this.router.url.indexOf('sent-item') >= -1) {
    //   this.showSearchResults = false;
    // }
    this.subscription.push(this.sharedService.emitSideNavReload$.subscribe(data => this.refreshFolders(data)))
  }

  ngOnDestroy() {
    for(let subs of this.subscription) {
      subs.unsubscribe();
    }
    this.subscription = [];
  }

  removeFolderFilter(sideTab){
    this.activeFolderFilter = ""
    this.emitFolderFilter(sideTab) 
  }

  applyFolderFilter(folderFilter,sideTab){
    this.activeFolderFilter = folderFilter
    this.emitFolderFilter(sideTab)
  }

  emitFolderFilter(sideTab){
    switch (sideTab) {
    case "inbox":
      this.sharedService.emitFolderFilterForInboxItems(this.activeFolderFilter)
      break;
    case "sent":
      this.sharedService.emitFolderFilterForSentItems(this.activeFolderFilter)
      break;
    case "archive":
      this.sharedService.emitFolderFilterForArchiveItems(this.activeFolderFilter)
      break;
    case "dailydocument":
      this.sharedService.emitFolderFilterForDailyDocumentItems(this.activeFolderFilter)
      break;
    }
  }

  refreshFolders(folderFor){
    switch (folderFor) {
    case "inbox":
      this.getInboxFolders('inbox')
      break;
    case "sent":
      this.getSentItemsFolders('sent')
      break;
    case "archive":
      this.getArchiveFolders('archive')
      break;      
    }
  }

  getInboxFolders(folder){
      localStorage.removeItem('workwfSearch');
      this.subscription.push(this.workflowService.getworkflowFolder(folder).subscribe(data => this.addFolders(data,"inbox")));
  }

  getSentItemsFolders(folder){
      localStorage.removeItem('workwfSearch');
      this.subscription.push(this.workflowService.getworkflowFolder(folder).subscribe(data => this.addFolders(data,"sent")));
  }

  getArchiveFolders(folder){
      localStorage.removeItem('workwfSearch');
      this.subscription.push(this.workflowService.getworkflowFolder(folder).subscribe(data => this.addFolders(data,"archive"))); 
  }
    
  setIsSecretary(data: any){
      if(data){
          this.isSecretary = true;
      }
  }
  
  addFolders(data,folderFor){
      let folderData = [];
      for (var i = 0; i < data.length; i++) {
          var folderDetailArray = data[i].split("!~");
          var folderDetail = {
              "folderId": folderDetailArray[0],
              "folderType": folderDetailArray[1],
              "folderName": folderDetailArray[2],
              "folderFullName":folderDetailArray[3]
          }
          folderData[i] = folderDetail;
      }
      if(folderFor == "inbox"){
        this.inboxFolders = folderData;
      }
      if(folderFor == "sent"){
        this.sentItemsFolders = folderData;
      }
      if(folderFor == "archive"){
        this.archiveFolders = folderData;
      }
  }
    
    
  deleteFolder(sentItemFolder:any ,detelesentfolder:any){
      this.subscription.push(this.workflowService.deleteFolder(sentItemFolder.folderId).subscribe(data => this.refreshList(data,sentItemFolder,detelesentfolder)));
  }
  
  refreshList(data,sentItemFolder,detelesentfolder){
      if(data = "true"){
          this.getSentItemsFolders('sent');
          detelesentfolder.close();
      }
  }
    
  confirmDelete(sentItemFolder:any ,detelesentfolder :any,selectTab:any){
      this.selectedFolder = sentItemFolder;
      detelesentfolder.open();
  }
    
  confirmInboxDelete(inboxFolder: any,deteleinboxfolder: any){
      this.selectedInboxFolder = inboxFolder;
      deteleinboxfolder.open()
  }
  
  deleteinboxFolder(selectedInboxFolder : any ,deteleinboxfolder: any){
      this.subscription.push(this.workflowService.deleteFolder(selectedInboxFolder.folderId).subscribe(data => this.refreshInboxFolderList(data,selectedInboxFolder,deteleinboxfolder)));
  }
    
  refreshInboxFolderList(data,selectedInboxFolder,deteleinboxfolder){
      if(data = "true"){
          this.getInboxFolders('inbox');
          deteleinboxfolder.close();
      }
  }
  
  confirmArchiveDelete(archiveFolder:any,detelearchivefolder:any){
       this.selectedArchiveFolder = archiveFolder;
       detelearchivefolder.open();
  }
  
  deletearchiveFolder(selectedArchiveFolder,detelearchivefolder){
      this.subscription.push(this.workflowService.deleteFolder(selectedArchiveFolder.folderId).subscribe(data => this.refreshArchiveFolderList(data,selectedArchiveFolder,detelearchivefolder)));
  }
    
    refreshArchiveFolderList(data,selectedArchiveFolder,detelearchivefolder){
         if(data = "true"){
          this.getArchiveFolders('archive');
          selectedArchiveFolder.close();
            }
        }
        getItemsLengthGreaterZero(folderFor){
          switch (folderFor) {
            case "inbox":
              return this.inboxFolders.length > 0;
            case "sent":
              return this.sentItemsFolders.length > 0;
             
            case "archive":
              return this.archiveFolders.length > 0;      
            }
        }
        getItemsLengthEqualZero(folderFor){
          switch (folderFor) {
            case "inbox":
              return this.inboxFolders.length == 0;
            case "sent":
              return this.sentItemsFolders.length == 0;
             
            case "archive":
              return this.archiveFolders.length == 0;      
            }
        }
        
}

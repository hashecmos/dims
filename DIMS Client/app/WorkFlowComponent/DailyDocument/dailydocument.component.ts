import {Component, Input} from '@angular/core'
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {Router, ActivatedRoute} from '@angular/router'
import {UserService} from '../../services/user.service'
import {BrowseService} from '../../services/browse.service'
import { BrowseSharedService} from './../../services/browseEvents.shared.service'
import {DocCheckIn} from './../../model/docCheckIn.model'
import {DocumentResult} from './../../model/documentResult.model'
import global = require('./../../global.variables')

@Component({
  selector: 'archive-layout',
  providers: [WorkflowService],
  templateUrl: 'app/WorkFlowComponent/DailyDocument/dailydocument.component.html',

})

export class DailyDocumentLayoutComponent {
  public tasks: any; is_all_selected: boolean;
  private emitFolderfilterForArchiveSubscription: any;
  private perPage: number = 20;
  private currentPage: any = 1;
  private totalItemsCount: number = 0;
  totalPages: any = 1;
  currentTab: string;
  public foldersuccessmessage: boolean = false;
  private secretary : string = "false";
  selectedDocuments: any;  
  selectedDocument: any;  
  private currentFolderFilter;
  private selectedAttachmentID: string
  private documentResults: DocumentResult[];
  private current_user: any;
  private currentDelegateForEmployeeLogin: string = "";
  private base_url: string;
  private viewer_url : any;
  private os_name:any;
  private paramsSubscription: any;
  private colPreference: any;
  @Input() private is_item_selected: boolean;
    
    
    
  constructor(private workflowService: WorkflowService, public _router: ActivatedRoute,
    private userService: UserService, private sharedService: BrowseSharedService,
    private browseService: BrowseService) {
    this.is_all_selected = false
    this.is_item_selected = true
    this.currentFolderFilter = ""
    this.selectedDocuments =[];
    this.base_url = global.base_url;
    this.viewer_url = global.viewer_url;
    this.os_name = global.os_name; 
    this._router = _router; 
    this.userService.notifyDelegateForChange.subscribe(data => this.refresh());
    this.current_user = this.userService.getCurrentUser();
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }
  }

  getDailyDocumentItems(page) {
    console.log("getting documents")
    this.workflowService.getDailyDocumentItems(this.currentFolderFilter, page, this.perPage,this.secretary).subscribe(data => this.assignDailyDocumentValues(data));
    
  }

  performAction: any = "";
  /*selectCheckbox(e: any, currentSelection: any) {
      if (e.target.checked) {
        currentSelection.showOptions = true;
        this.selectedDocuments.push(currentSelection);
      } else {
        this.selectedDocuments = [];
        this.selectedDocuments.splice(this.selectedDocuments.indexOf(currentSelection), 1);
        currentSelection.showOptions = false
      }
  }*/

  selectCheckbox(e: any, currentSelection: any) {
 var _currenttasks=[];
    _currenttasks = this.tasks;
    if (e.target.checked) {
      currentSelection.showOptions = true;
      this.selectedDocuments.push(currentSelection);
    } else {
     // this.selectedDocuments = [];
      this.selectedDocuments.splice(this.selectedDocuments.indexOf(currentSelection), 1);
      currentSelection.showOptions = false
    }
    let test = true
    for(let task of _currenttasks) {
      if(!task.is_checked) {
        test = false;
      }
   }
    if(test) {
      this.is_all_selected = true;
    } else {
      this.is_all_selected = false;
    }
}

  isItemSelected() {
    let result = this.selectedDocuments.length > 0 ? true : false
    return result
  }
    
    
    
  assignDailyDocumentValues(data) {
    this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.colPreference = colData );
    console.log("this.colPreference DATA ::  ",this.colPreference);
    this.documentResults = data;
    this.tasks = data.resultMapList;
    this.totalItemsCount = 0;
    this.totalPages = 1;
    if (this.tasks.length > 0) {
      this.totalItemsCount = this.tasks.length;
      if (this.totalItemsCount % this.perPage == 0) {
        this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString());
      } else {
        this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString()) + 1;
      }
    }
  }


  ngOnInit() {
    //this.workflowService.isSecretary().subscribe(data => this.setIsSecretary(data));
    var context: any = this;
    this.paramsSubscription = this._router.queryParams.subscribe(
      data => {
        context.refresh();
      })
    /*this.emitFolderfilterForArchiveSubscription = this.sharedService.emitFolderfilterForArchive$.subscribe(folderFilter => {
      this.setCurrentFilters(folderFilter)
      this.getDailyDocumentItems(1);
    })
    this.sharedService.emitchangeEvent$.subscribe(showmessage => this.ShowMessage(showmessage));*/

  }

  ShowMessage(event: any) {
    if (event.status == 200) {
      this.foldersuccessmessage = true;
    }
  }

  removeFolderMessage() {
    this.foldersuccessmessage = false;
  }

  ngOnDestroy() {
    //this.emitFolderfilterForArchiveSubscription.unsubscribe();
  }

  setCurrentFilters(folderFilter) {
    this.currentFolderFilter = folderFilter
  }

  toggleSelectAll(event: any) {
    this.is_all_selected = event.currentTarget.checked
    this.selectedDocuments = [];
    
    if(this.tasks){
      for (let doc of this.tasks) {
        doc.is_checked = event.currentTarget.checked;
        doc.showOptions = event.currentTarget.checked;
        this.selectedDocuments.push(doc);
      }
    }

  //Added if cond by Rameshwar
	if (!event.currentTarget.checked) {
      this.selectedDocuments = [];
      this.deSelectall();
    }
  }

  deSelectall() {
  for (let doc of this.tasks) {
    doc.is_checked = false
  }
  this.is_all_selected = false
}
  refresh() {
    console.log("refresh  :::  ",this.currentPage)
        this.is_all_selected = false
        this.selectedDocuments = [];
    this.getDailyDocumentItems(this.currentPage);
  }
  getPage(page: number) {
    this.currentPage = page;
    this.getDailyDocumentItems(page);
    return page;
  }
    
  setIsSecretary(data: any){
      if(data){
          this.secretary = "true";
      }
      this.getDailyDocumentItems(1);
  }
    
  setAttachmentID(id) {
    this.selectedAttachmentID = id;
  }


  //Added  by Rameshwar 27102017
  setEmailAttachmentDocumentsID(document) {
    var selectedDocuments = [];
    selectedDocuments.push(document);
    return selectedDocuments;
  }

sendMail(documentId: any, docName: any) { 
    this.browseService.getOutlookAttachmentDocument(documentId, this.os_name).subscribe(data => {
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
    });
  } 
  
  openDocumentActionPopup(document: string, actionPopup: any) {
      //Commented by Rameshwar 27102017
    //this.setAttachmentID(documentId);
    actionPopup.openPopup(this.setEmailAttachmentDocumentsID(document));
    //actionPopup.openPopup(this.selectedAttachmentID);
  }
  
  viewDocument(fileToView: string){
        window.open(fileToView, '_blank', 'fullscreen=no');
       
  } 
    applyFilter(filters) {
    //this.deSelectall()
    this.setcurrentFilters(filters)
    this.getDailyDocumentItems(1);
  }

  setcurrentFilters(filters) {
    //this.currentFilter = filters.primaryFilter
    this.currentFolderFilter = filters
    //this.filterFlag = filters.flagFilter;
  }
   
     leftClicked(p : any){
        if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value==''){
            p.previous();
        }else{
            let selectedPage = document.getElementById("currentPageSelected").value;
           if(selectedPage == p.service.instances.DEFAULT_PAGINATION_ID.currentPage){
                p.previous();
            }else{
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
            }
            
        }
        
    }
    
    rightClicked(p : any){
        if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value==''){
            p.next();
        }else{
            let selectedPage = document.getElementById("currentPageSelected").value;
            
            if(selectedPage == p.service.instances.DEFAULT_PAGINATION_ID.currentPage){
                p.next();
            }else{
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
            }
            
            
            
        }
        
    }
    
     onEnterKey(evt: any, p : any ){
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode == 13) {
           if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value!=''){
               let selectedPage = document.getElementById("currentPageSelected").value;
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
           }
        }
    }

    handleDocumentChange(doc){
      console.log("task :: ",doc);
      if(this.tasks && this.tasks.length > 0){
          for(var i = 0;i<this.tasks.length; i++){
          
              if(this.tasks[i].id == doc.id ){
                  
                      this.tasks[i] = doc;
                      this.tasks = this.tasks.slice();
          
              }
          
          
          }
          
      }
  }
  setAttachmentDoc(doc){
    this.currentTab = "Properties";
    this.setAttachmentID(doc.id);
    this.selectedDocument = doc;
}
}

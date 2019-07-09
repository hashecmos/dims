//libraries
import {Component, Input, Output, EventEmitter, ViewChild} from '@angular/core'
import {Router, ActivatedRoute} from '@angular/router'
//import {TreeComponent} from 'angular2-tree-component';
//models
import {LaunchWorkflow} from '../model/launchWorkflow.model'

import {SubFolder} from '../model/subFolderContainee.model'
import {FolderFiledIn} from '../model/folderFileContainee.model'
import {DocumentAttachment} from '../model/docAttachmentContainee.model'
import {DocumentVersion} from '../model/docVersionContainee.model'
import {DocumentInFolder} from '../model/docFolderContainee.model'
import {DocCheckIn} from '../model/docCheckIn.model'
import {Department} from '../model/departmentRecipients.model'
import {Division} from '../model/divisionRecipients.model'
import {User} from '../model/userRecipients.model'
//services

//Added By Rameshwar
import {SharedService} from '../services/advanceSearchTasks.service'
import {HighlightPipe} from '../pipes/highlight.pipe'

import {BrowseService} from '../services/browse.service'
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import {WorkflowService} from '../services/workflowServices/workflow.service'
import {RecipientsService} from '../services/recipients.service'
import {UserService} from '../services/user.service'
import {TreeModule,TreeNode} from 'primeng/primeng';
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker,IMyInputFieldChanged} from 'mydatepicker';
import global = require('./../global.variables')
declare var $: any;

@Component({
  selector: 'documents',
  providers: [RecipientsService],
  templateUrl: 'app/BrowseComponent/documents.component.html'
})

export class Documents {
  countTimes =0
  goingPage = true;
  currentTab: string;

  public documents_items: any; selectedDocuments: any; selectedFolders: any;
  @ViewChild('p') p;
  @Input() nodes: Node[];
  @Input() selectedNode : TreeNode;
  @Input() containees: any[];
  @Output() onRefresh: EventEmitter<any> = new EventEmitter();
  @Output() onNodeChange: EventEmitter<any> = new EventEmitter();
  private moveNodes: TreeNode[];  
  nodesList = [];
  private parentFolderId: string;
  private sub_folders: any;
  private is_all_selected: boolean;
  public message_success: boolean = false;
  private applyFilterType: any;
  private selectedFolder: any;
  //@ViewChild(TreeComponent)
  //private tree: TreeComponent;
  private selectedAttachmentID: string
  selectedDocument: any;
  private base_url: string;
  private viewer_url : any;
  private currentCheckInDoc: DocCheckIn;
  private isReserved: any;
  private currentDoc: any;
  private attachmentFile: any;
  private docProperty: any = [];
  private docVersions: any = [];
  private docFolders: any = [];
  private docHistory: any = [];
  private newTask = new LaunchWorkflow();
    
  private divisions : any = [];
  private departments : any = [];
  private current_user: any;
  private currentDelegateForEmployeeLogin: string = "";
  public instructions: any = [];
  public colPrefs :any;
  wradio_to: any = [];
  wradio_cc: any = [];
  wradio_none: any = [];
  private userSearchUrl;
  private knpcHierarchy : any = [];
  private os_name:any;
  private checkin: boolean = false;
  private creatorBy:any;
  private loginUser:any; 
  private isReservedAction:any;
  private preventNextPage: boolean = true;
  //private totalItemsCount : number =0;
  private searchTerm:any; // For AMZ changes
  private myDatePickerOptions: IMyOptions ={
    editableDateField:false
  };

  private currentPage: any = 1;
  //pagination variables
  private currentPageStartCount: number = 1
  private perPage: number = 100
  private currentPageEndCount: number = this.perPage
  private totalItemsCount: number
  totalPages: any = 100;
  public filterOptions = ['Subject','Reference No','Document ID','Document Date','Department','Created By','Date Created'];
  public filterButtonText = "Sort";
  public isSortButtonEnabled: boolean = false;
  private selectedindex : any;
  private selected :any;

  public isDocDate : boolean = false;
  public isDepartment : boolean = false;
  public isCreatedBy : boolean = false;
  public isDateCreated : boolean = false;
  
  constructor(public router: Router, public browseService: BrowseService,
    private browseSharedService: BrowseSharedService,private recipientsService: RecipientsService,
    private workflowService: WorkflowService, private userService: UserService,private sharedService: SharedService) {
    this.nodes = this.browseService.getNodes()
    this.base_url = global.base_url;
    this.viewer_url = global.viewer_url;
    this.selectedDocuments = [];
    this.selectedFolders = [];
    this.is_all_selected = false;
    this.isReserved = false;
    this.userSearchUrl = global.getUserList; 
    this.os_name = global.os_name;
    this.workflowService.getLaunchInstructions().subscribe(data => this.instructions = data);
    
    this.current_user = this.userService.getCurrentUser();
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }
      
    this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
    this.recipientsService.getUserDepartment(this.current_user.employeeDepartment.departmentCode,this.current_user.employeeLogin).subscribe(data => this.departments = data);
    this.browseService.getfolderTree('').subscribe(data => this.nodesList= this.getTreeNodes(data,false));
     this.getColumnPreferences();
  }



  getPage(page: number) {
    this.currentPage = page;
    if(localStorage.getItem('searchedDocuments')) {
    document.getElementById("displayLoader").style.display="block";
      let v = JSON.parse(localStorage.getItem('searchedDocuments'));
      v.pageNo = page;
      this.browseService.SearchDocuments(v).subscribe(data => {
        this.sharedService.emitDocuments(data)
        this.sharedService.setSearchedDocuments(data);
        this.documents_items = data;
        
        this.enableNextPage(this.documents_items.length);
         document.getElementById("displayLoader").style.display= "none";
      });
     
      return page;
    } else if(this.selectedNode.data){
    document.getElementById("displayLoader").style.display="block";
   console.log("=======current folder id=="+this.selectedNode.data);
   this.browseService.getContainees(this.selectedNode.data,page,this.perPage).subscribe(data => {
         // this.containees = data;
         console.log('::after getpage::',data);
         this.sharedService.emitDocuments(data)
        this.sharedService.setSearchedDocuments(data);
       console.log('data.documents.length ::',data.documents.length);
      this.enableNextPage(data.documents.length);
        // data;
       this.refreshGrid(data);
      document.getElementById("displayLoader").style.display= "none";
        // this.refreshNodes();

        } ,error=>this.checkerror(error));
      return page;
    }else{
      return false
    }
   
  }
  enableNextPage(docCount:number){
  console.log('docCount ::',docCount);
   if(docCount==100){
   console.log('Inside if');
       this.preventNextPage=true;
       this.totalItemsCount = docCount;
       }else{
        console.log('Inside else');
        this.totalItemsCount = docCount;
       this.preventNextPage=false;
       }
  }
  leftClicked(p : any){
    console.log("PAge in left click ::; ",p)
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
    this.goingPage= false;    
    console.log("PAge in right click ::; ",p)
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
 /**
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
     this.getPage(1);
  }
**/
 onEnterKey(evt: any, p : any ){
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode == 13) {
      var _event =evt;
      var _p =p;
         if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value!=''){
             let selectedPage = document.getElementById("currentPageSelected").value;
              p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
              p.next();
         }
      }
   //  this.getPage(1);
  }
//Added By Rameshwar for sorting -- START
applyFilterOnSort(option, action, index){
  console.log("option, action, index  :: ",option, action, index);
  //Date Created Descending 6
  this.selected = action; 
  this.selectedindex = index;
    //option,'Ascending',i
    function sortByKeyAsending(array, key) {
      return array.sort(
       function(a,b){
        var x = '',y ='';
        if(a[key])
            x = a[key].trim().toLowerCase();
        
         if(b[key])
           y = b[key].trim().toLowerCase();

        if( x > y){
            return 1;
        }else if( x < y ){
            return -1;
        }
        return 0;
      }
    )
      
    }

    function sortByKeyDesending(array, key) {
      return array.sort(
        function(a,b){
         
         var x = '',y ='';
         if(a[key])
             x = a[key].trim().toLowerCase();
         
          if(b[key])
            y = b[key].trim().toLowerCase();
        
         if( y > x){
             return 1;
         }else if( y < x ){
             return -1;
         }
         return 0;
       }
     )
    }

    function sortByKeyAsendingDate(array, key) {
      return array.sort(function(a, b) {
       
        var aa = a[key].split(/-|\//); 
        var bb = b[key].split(/-|\//); 
          var x = new Date(aa[1]+"/"+aa[0]+"/"+aa[2]).getTime(); 
          var y = new Date(bb[1]+"/"+bb[0]+"/"+bb[2]).getTime();
          //console.log("x AS:: ",x)
          //console.log("y AS:: ",y)
          if( !isFinite(x) && !isFinite(y) ) {
            return 0;
          }
          if( !isFinite(x) ) {
            return 1;
          }
          if( !isFinite(y) ) {
            return -1;
          }
          return (x - y);
         
      });
    }

    function sortByKeyDesendingDate(array, key) {
      return array.sort(function(a, b) {
       
        var aa = a[key].split(/-|\//); 
        var bb = b[key].split(/-|\//); 
          var x = new Date(aa[1]+"/"+aa[0]+"/"+aa[2]).getTime(); 
          var y = new Date(bb[1]+"/"+bb[0]+"/"+bb[2]).getTime();
          //console.log("x :: ",x)
          //console.log("y :: ",y)
          if( !isFinite(x) && !isFinite(y) ) {
            return 0;
          }
          if( !isFinite(x) ) {
            return 1;
          }
          if( !isFinite(y) ) {
            return -1;
          }
          return (y - x);
          
      });
    }
    if(this.documents_items && this.documents_items.length > 0){
      if(action == "Ascending"){
        if(option == 'Subject'){
          this.documents_items = sortByKeyAsending(this.documents_items, 'subject');
        }else if(option == 'Reference No'){
          this.documents_items = sortByKeyAsending(this.documents_items, 'referenceNo');
        }else if(option == 'Document ID'){
          this.documents_items = sortByKeyAsending(this.documents_items, 'documentID');
        }else if(option == 'Date Created'){
          this.documents_items = sortByKeyAsendingDate(this.documents_items, 'dateCreated');
        }else if(option == 'Created By'){
          this.documents_items = sortByKeyAsending(this.documents_items, 'createdBy');
        }else if(option == 'Department'){
          this.documents_items = sortByKeyAsending(this.documents_items, 'department');
        }else if(option == 'Document Date'){
          this.documents_items = sortByKeyAsendingDate(this.documents_items, 'documentDate');
        }
      }
      if(action == "Descending"){
        if(option == 'Subject'){
          this.documents_items = sortByKeyDesending(this.documents_items, 'subject');
        }else if(option == 'Reference No'){
          this.documents_items = sortByKeyDesending(this.documents_items, 'referenceNo');
        }else if(option == 'Document ID'){
          this.documents_items = sortByKeyDesending(this.documents_items, 'documentID');
        }else if(option == 'Date Created'){
          this.documents_items = sortByKeyDesendingDate(this.documents_items, 'dateCreated');
        }else if(option == 'Created By'){
          this.documents_items = sortByKeyDesending(this.documents_items, 'createdBy');
        }else if(option == 'Department'){
          this.documents_items = sortByKeyDesending(this.documents_items, 'department');
        }else if(option == 'Document Date'){
          this.documents_items = sortByKeyDesendingDate(this.documents_items, 'documentDate');
        }
        
      } 
    }
   
}

isActiveAscending(Ascending : any ,index: any,option:any) {
  
  if(this.selectedindex==index){
      return this.selected === Ascending;
  }
 
}
isActiveDescending(Descending :any,index: any,option:any){
  if(this.selectedindex==index){
  return this.selected === Descending;
}
}

//Added By Rameshwar for sorting  -- END

getColumnPreferences(){
  this.userService.getColumnPreference(this.currentDelegateForEmployeeLogin).subscribe( colData=> this.assignColPreferences(colData) );
  this.refreshNodes();
}

assignColPreferences(colData)
{
  this.colPrefs = colData;
  
  this.isDocDate = false;
  this.isDepartment = false;
  this.isCreatedBy = false;
  this.isDateCreated = false;

 for(var i=0; i<this.colPrefs.length ; i++){
    if(this.colPrefs[i].itemType == 'browseDocumentDate' && this.colPrefs[i].coloumnEnabled == 'true' && this.colPrefs[i].columnName == 'documentDocumentDate'){
      this.isDocDate = true;
    } else if(this.colPrefs[i].itemType == 'browseDepartment' && this.colPrefs[i].coloumnEnabled == 'true' && this.colPrefs[i].columnName == 'documentDepartment'){
      this.isDepartment = true;
    } else if(this.colPrefs[i].itemType == 'browseCreatedBy' && this.colPrefs[i].coloumnEnabled == 'true' && this.colPrefs[i].columnName == 'documentCreatedBy'){
      this.isCreatedBy = true;
    } else if(this.colPrefs[i].itemType == 'browseDateCreated' && this.colPrefs[i].coloumnEnabled == 'true' && this.colPrefs[i].columnName == 'documentDateCreated'){
      this.isDateCreated = true;
    }
  }
}

  refreshNodes() {
    if(localStorage.getItem('searchedDocuments')){
      let asd = JSON.parse(localStorage.getItem('searchedDocuments'));
      this.browseService.SearchDocuments(asd).subscribe(data => {
        this.documents_items = data;
        this.selectedDocuments =[];
      });
    }else if(this.selectedNode){
      this.onRefresh.emit();
    }
    //window.location.refresh();
      //setTimeout(() => {
          this.onRefresh.emit();
             /*let asd = JSON.parse(localStorage.getItem('searchedDocuments'));
            this.browseService.SearchDocuments(asd).subscribe(data => {
                        this.emitSearchedDocuments(data)
            });*/
   
      //}, 100);
      
  }
  
  emitSearchedDocuments(data) {
    this.sharedService.emitDocuments(data)
    this.sharedService.setSearchedDocuments(data);
          }
		
  deSelectall() {
      if(!(this.sub_folders == undefined)){
           for (let folder of this.sub_folders) {
              folder.is_checked = false
            }
      }
   
    for (let doc of this.documents_items) {
      doc.is_checked = false
    }
    this.is_all_selected = false
  }
 SelectedToRadio(e: any, user: User) {
    if (this.wradio_to.indexOf(user.employeeLogin) == -1) {
      if (this.wradio_cc.indexOf(user.employeeLogin) > -1) {
        this.wradio_cc.splice(this.wradio_cc.indexOf(user.employeeLogin), 1)
      }
      this.wradio_to.push(user.employeeLogin)
    }
  }
  SelectedNoneRadio(e: any, user: User) {
      if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
      if (this.wradio_cc.indexOf(user.employeeLogin) > -1 || this.wradio_to.indexOf(user.employeeLogin) > -1) {
          this.wradio_cc.splice(this.wradio_cc.indexOf(user.employeeLogin), 1)
          this.wradio_to.splice(this.wradio_to.indexOf(user.employeeLogin), 1)
      }
    }
  }
  SelectedCcRadio(e: any, user: User) {
    if (this.wradio_cc.indexOf(user.employeeLogin) == -1) {
      if (this.wradio_to.indexOf(user.employeeLogin) > -1) {
        this.wradio_to.splice(this.wradio_to.indexOf(user.employeeLogin), 1)
      }
      this.wradio_cc.push(user.employeeLogin)
    }
  }
  isItemSelected() {
    let result = this.selectedDocuments.length > 0 ? true : false || this.selectedFolders.length > 0 ? true : false
    return result
  }

  selectDoc(e: any, currentSelection: any) {
    //set the login user, creator by values
    this.loginUser = this.current_user.employeeLogin;
    this.creatorBy = currentSelection.createdBy;
    console.log('currentSelection   :',currentSelection);
    console.log('this.loginUser   :',this.loginUser);
    console.log('this.creatorBy   :',this.creatorBy);
    if (currentSelection.objectType == "Document") {
      if (e.target.checked) {
        currentSelection.showOptions = true;
        this.selectedDocuments.push(currentSelection);
      } else {
        this.selectedDocuments.splice(this.selectedDocuments.indexOf(currentSelection), 1);
        currentSelection.showOptions = false
      }
    } else {
      if (e.target.checked) {
        currentSelection.is_checked = true
        currentSelection.visible = true
        this.selectedFolders.push(currentSelection)
      } else {
        this.selectedFolders.splice(this.selectedFolders.indexOf(currentSelection), 1);
        this.is_all_selected = false
      }
    }
      /*let allFoldersSelected = false;
      if(!(this.sub_folders == undefined)){
          allFoldersSelected = this.selectedFolders.length == this.sub_folders.length ? true : false
      }
    
    let allDocumentsSelected = this.selectedDocuments.length == this.documents_items.length ? true : false
    this.is_all_selected = allFoldersSelected && allDocumentsSelected*/
	//Added By Rameshwar
	 if(!(this.sub_folders == undefined)){
        this.is_all_selected  = this.selectedFolders.length == this.sub_folders.length ? true : false
      }
    
      if(!(this.documents_items == undefined)){
        this.is_all_selected  = this.selectedDocuments.length == this.documents_items.length ? true : false
      }
  }

  checkoutDoc(doc: any, docCheckInModal: any, docCannotDoCheckInOutModel: any) {
   
    //this.workflowService.getDocumentProperties(doc.id).subscribe(data => this.openCheckoutModel(data, docCheckInModal))
    this.currentDoc = doc;
    console.log('doc   :',doc);
    console.log('doc reserved   :',doc.reserved);
    console.log("Created By   :",this.currentDoc.createdBy);
    console.log("Current User   :",this.current_user.employeeLogin);
    if(this.loginUser.toUpperCase() == this.creatorBy.toUpperCase()) {
         this.workflowService.getDocumentProperties(doc.id).subscribe(data => this.openCheckoutModel(data, docCheckInModal))
    }
    else {
        this.isReserved = doc.reserved;
        docCannotDoCheckInOutModel.open();
        this.refreshNodes();
    }
  }

  openCheckoutModel(doc: any, docCheckInModal: any) {
    for (let val of doc) {
      if (val.propertyName == "IsReserved") {
        this.isReserved = val.propertyValue == "true";
      }
    }
    this.currentCheckInDoc = new DocCheckIn(doc);
    docCheckInModal.open();
  }

  /*sendCheckoutDoc(docCheckInModal: any, $event: any) {
    let propertyValue: any = [];
    if (!this.isReserved) {
      this.browseService.docCheckOut(this.currentDoc.id).subscribe(data => this.refreshNodes());
    } else {
      for (let inputField of [].slice.call($event.target.getElementsByTagName('input'))) {
        if (inputField.getAttribute('type') != "file") {
          let value = { "propertyName": inputField.name, "propertyValue": inputField.value };
          propertyValue.push(value);
        }
      }
      var formData = new FormData();
      formData.append('documentJSONString', JSON.stringify({
        properties: propertyValue
      }));
      formData.append('osName', global.os_name)
      formData.append('objectId', this.currentDoc.id)
      formData.append('documentClass', this.attachmentFile)

      this.browseService.docCheckIn(formData).subscribe(data => this.refreshNodes());
      this.attachmentFile = null;
    }
    docCheckInModal.close();
  }*/
  
  sendCheckoutDoc(docCheckInModal: any, docCheckInCheckOutStatus: any, $event: any) {
    let propertyValue: any = [];
    
    if (!this.isReserved) {
      this.browseService.docCheckOut(this.currentDoc.id).subscribe(data => {
          this.currentDoc.reserved = true;
          this.handleDocumentChange(this.currentDoc);
      });
        this.checkin = false;
    } else {
      for (let inputField of [].slice.call($event.target.getElementsByTagName('input'))) {
        if (inputField.getAttribute('type') != "file") {
          let value = { "propertyName": inputField.name, "propertyValue": inputField.value };
          propertyValue.push(value);
        }
      }
      var formData = new FormData();
      formData.append('documentJSONString', JSON.stringify({
        properties: propertyValue
      }));
      formData.append('osName', global.os_name)
      formData.append('objectId', this.currentDoc.id)
      formData.append('documentClass', this.attachmentFile)

      this.browseService.docCheckIn(formData).subscribe(data => {
          this.currentDoc.reserved = false;
          this.handleDocumentChange(this.currentDoc);
      });
        
      this.attachmentFile = null;
        this.checkin = true;
    }
    docCheckInModal.close();
    docCheckInCheckOutStatus.open();
    //this.documents_items = this.documents_items.slice();
  }


  fileChange(event) {
    this.attachmentFile = event.target.files[0];
    if(this.attachmentFile != undefined){
      document.getElementById("checkIN").disabled = false;
    }
  }

  checkout_actn(docCheckInModal, docCannotDoCheckInModel) {
    /*if (this.selectedDocuments.length == 1) {
      this.checkoutDoc(this.selectedDocuments[0], docCheckInModal)
    }*/
    console.log('this.creatorBy   :',this.creatorBy);
    console.log('this.loginUser   :',this.loginUser);
    console.log('this.selectedDocuments[0]   :',this.selectedDocuments[0]);
    console.log(this.loginUser.toUpperCase() == this.creatorBy.toUpperCase());
    if(this.loginUser.toUpperCase() == this.creatorBy.toUpperCase()) {
        if (this.selectedDocuments.length == 1) {
            this.checkoutDoc(this.selectedDocuments[0], docCheckInModal)
        }
    }
    else {
        this.isReservedAction = this.selectedDocuments[0].reserved;
        docCannotDoCheckInModel.open();
        this.refreshNodes();    
    }  
  }

  applyFilter(event: any) {
    this.selectedDocuments = [];
    this.applyFilterType = event;
    this.deSelectall();
  }

  toggleSelectAll(event: any) {
    this.is_all_selected = event.currentTarget.checked
	//Commented By Rameshwar
    /*for (let folder of this.sub_folders) {
      folder.is_checked = event.currentTarget.checked
      folder.visible = event.currentTarget.checked
    }
    for (let doc of this.documents_items) {
      doc.is_checked = event.currentTarget.checked;
      doc.showOptions = event.currentTarget.checked;
    }*/
	//Added if cond by Rameshwar
	 if(this.sub_folders){
      for (let folder of this.sub_folders) {
        folder.is_checked = event.currentTarget.checked
        folder.visible = event.currentTarget.checked
      }
    }
    if(this.documents_items){
      for (let doc of this.documents_items) {
        doc.is_checked = event.currentTarget.checked;
        doc.showOptions = event.currentTarget.checked;
      }
    }

  //Added if cond by Rameshwar
	if (event.currentTarget.checked) {
      if(this.documents_items)
          this.applyFilterType == "Folders" ? this.selectedDocuments = [] : this.selectedDocuments = this.documents_items.slice(0);
      if(this.sub_folders)
        this.applyFilterType == "Documents" ? this.selectedFolders = [] : this.selectedFolders = this.sub_folders.slice(0);
    }	else {
      this.selectedDocuments = [];
      this.selectedFolders = [];
      this.deSelectall();
    }
  }

  opennewWindow() {
    window.open("");
  }

  actionNewFolder(folderName: any) {
    var parentfolderId = this.selectedNode.id
    if(parentfolderId == undefined){
        document.getElementById("folderError").style.display = "block";
        return;
    }
    var $input = $(folderName.currentTarget).find('input[type=text]')
     
    if (!$input.val().match(/^[0-9a-zA-Z$-)+-.;=~@#!^_`{}\[\]\s]*$/)){
     document.getElementById("errorAlert").style.display = "block";
     //alert('A folder name cannot contain any of the following characters: \/:*?"<>|);
     return;
    }  
    this.browseService.newFolder(encodeURIComponent($input.val()), parentfolderId).subscribe(data => this.moveToNewFolder(data), error => this.browseSharedService.emitMessageChange(error));
    $input.val('');
    $('#newfolder').modal('hide');
  }
  clearAlert(){
      if(document.getElementById("errorAlert")!=null){
          document.getElementById("errorAlert").style.display = "none";
      }
      document.getElementById("folderError").style.display = "none";
  }
  moveToNewFolder(data) {
    document.getElementById("errorAlert").style.display = "none";
    document.getElementById("folderError").style.display = "none";
    //let newFolderId = data._body
    //this.moveSelectedFiles(newFolderId)
    this.browseService.getContainees(this.selectedNode.data).subscribe(data => this.addTreeNodes(data));
    //this.browseSharedService.emitMessageChange(data)
  }

  moveSelectedFiles(destinationFolderId) {
    for (let doc of this.selectedDocuments) {
      if (doc.objectType == "Document") {
        this.browseService.moveDocument(doc.id, destinationFolderId, doc.parentPath).subscribe(data => this.ShowSuccessMessage(data,destinationFolderId,doc.parentPath))
      }
    }
    //this.browseSharedService.emitChange(destinationFolderId)
  }

  moveToFolder(event: any) {
    var destinationFolderId = event.data
    this.moveSelectedFiles(destinationFolderId)
    $('#moveFolder').modal('hide');
    this.browseService.getfolderTree('').subscribe(data => this.nodesList= this.getTreeNodes(data,false));
    
  }

  launchDocInfoView() {
    this.docVersions = this.browseService.getDocVersions();
    this.docFolders = this.browseService.getDocFolders();
    this.docHistory = this.browseService.getDocHistory();

  }

ngOnChanges(changes: any) {
    //Added By Rameshwar
    
    if(localStorage.getItem('searchedDocuments') != null) {
      this.documents_items = this.sharedService.getSearchedDocuments();
    } 
    /*if (this.router.url == '/browse/document-search') {
   this.documents_items = this.sharedService.getSearchedDocuments();
   } */
//   console.log("documents_items  ::: ",this.documents_items);
    this.is_all_selected = false;
    if (changes.containees && changes.containees.currentValue != undefined) {
      var currentValue = changes.containees.currentValue;
      if (currentValue.folders) {
        this.documents_items = currentValue.documents.map(document => new DocumentInFolder(document));
      console.log('this.documents_items ::',this.documents_items);
      this.enableNextPage(this.documents_items.length);
      }
      if (currentValue.folders) {
        this.sub_folders = currentValue.folders.map(folder => new SubFolder(folder));
      }
      if(localStorage.getItem('searchedDocuments') != null) {
        this.documents_items = currentValue.documents.map(document => new DocumentInFolder(document));
        
      }
      this.applyFilterOnSort("Date Created", "Descending", '6');
    }
    if(this.documents_items != undefined){
        this.totalItemsCount = this.documents_items.length;
         this.enableNextPage(this.totalItemsCount);
    }
    this.selectedDocuments = [];
    this.selectedFolders = [];
 
    //AMZ Changes
    if(localStorage.getItem('searchTerm')) {
      this.searchTerm = localStorage.getItem('searchTerm');
     // localStorage.removeItem('searchTerm');
    } else {
      this.searchTerm = ''
    } // End AMZ change
 
    if(!this.goingPage) {
      this.p.setCurrent(1);
    } 
    if(this.documents_items && this.documents_items.length > 0){
      console.log("Yes Documents");
      this.isSortButtonEnabled = true;
      document.getElementById("errortext_search").style.display = "none";
    }else{
      console.log("No Documents")
      this.isSortButtonEnabled = false;
      document.getElementById("errortext_search").style.display = "block";
    }
     

    console.log("isSortButtonEnabled ::: ",this.isSortButtonEnabled)
    
    /*if(!this.goingPage) {
      setTimeout(() => {
        this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = 2;
        this.p.previous();
      }, 1000);
    } */
  }

  setAttachmentID(id){
    this.selectedAttachmentID = id;
  }

  setAttachmentDocHist(doc) {
    this.currentTab = "History";
   // console.log("selected Docuemnt ::  ",doc)
      this.selectedAttachmentID = doc.id;
      this.selectedDocument = doc;
  }
  setAttachmentDoc(doc){
    this.currentTab = "Properties";
    console.log("selected Docuemnt ::  ",doc)
      this.selectedAttachmentID = doc.id;
      this.selectedDocument = doc;
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

  openDocumentActionPopup(document: any, actionPopup: any) {
    console.log("document  ::: ",document);
    var docs = [];
    docs.push(document);
    //Commented by Rameshwar 27102017
    //this.setAttachmentID(documentId);
   // actionPopup.openPopup(this.setEmailAttachmentDocumentsID(docs));
     actionPopup.openPopup(docs);
  }

  openDocumentLink(documentLink: any) {
    documentLink.openPopup(this.selectedAttachmentID);
  }

  cancelCheckout(docCannotDoCancelCheckOutModel) {
   // this.browseService.cancelCheckout(this.selectedDocuments[0].id).subscribe(data => this.refreshNodes());
   if(this.loginUser.toUpperCase() == this.creatorBy.toUpperCase()) {
    this.browseService.cancelCheckout(this.selectedDocuments[0].id).subscribe(data =>{ 
    this.selectedDocuments[0].reserved = false;
    this.handleDocumentChange(this.selectedDocuments[0]);
    });
    }
    else {
        docCannotDoCancelCheckOutModel.open();
        this.refreshNodes(); 
    }
  }

  ngOnInit() {
    this.browseSharedService.emitchangeEvent$.subscribe(showmessage => this.ShowMessage(showmessage));
    //AMZ Changes
   // if(localStorage.getItem('searchTerm')) {
   //   this.searchTerm = localStorage.getItem('searchTerm');

    //} else {
     // this.searchTerm = ''
   // } // End AMZ change
  }

  ShowSuccessMessage(event: any,destinationFolderId :any,parentPath : any){
       if (event.status == 200) {
          this.message_success = true;
          
         this.browseService.getContainees(parentPath).subscribe(data => this.refreshGrid(data));
         this.browseSharedService.emitMessageChange(event);
      }
  }
    refreshGrid(data){
        this.containees = data;
        if (this.containees != undefined) {
              if (this.containees.documents) {
                this.documents_items = this.containees.documents.map(document => new DocumentInFolder(document));
              }
              this.applyFilterOnSort("Date Created", "Descending", '6');
            }
            this.selectedDocuments = [];
            this.selectedFolders = [];
        }
  ShowMessage(event: any) {
    if (event.status == 200) {
      this.message_success = true;
    }
  }
  removeFolderMessage() {
    this.message_success = false;
  }
    
   userSelected_docs: any[];
    switchTab1(event){
        this.newTask.subject = this.selectedDocuments[0].EmailSubject;
        this.newTask.name = this.selectedDocuments[0].className;
        //this.selected_docs = this.selected_docs.slice(); 
        this.userSelected_docs =[];
        this.userSelected_docs = this.selectedDocuments.slice();
        
    }
    
  loadUsersForDepartment(department: Department) {
    window.document.getElementById("to").disabled = false;
    window.document.getElementById("cc").disabled = false;
    this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
    this.recipientsService.getUsersForDepartment(department).subscribe();
  }
    
  loadUsersForDivision(division: Division) {
    window.document.getElementById("to").disabled = false;
    window.document.getElementById("cc").disabled = false;
    this.userSearchUrl = global.base_url + "/EmployeeService/getDivisionUsers?division_code="+division.divisionCode+"&user_login="+this.current_user.employeeLogin+"&searchCrtieria=:keyword";
    this.recipientsService.getUsersForDivision(division,this.current_user.employeeLogin).subscribe();
  }
    
    addTosList(event) {
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
    
   launch_existingdoc(launchModal: any) {
    this.workflowService.launchDocuments(
      this.userSelected_docs, this.newTask, this.wradio_to, this.wradio_cc, null
    ).subscribe(data => this.launchCompletionHandler(data), error => this.launchCompletionHandler(error));
    this.wradio_to = [];
    this.wradio_cc = [];
    this.userSelected_docs = [];
    this.selectedDocuments =[];
    $('#launch123').modal('hide');
  }
    
     launchCompletionHandler(response: any) {
    this.browseSharedService.emitMessageChange(response);
    this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime()} })
  }
    
    disableToCC(){
        this.userSearchUrl = "";
        window.document.getElementById("to").disabled = true;
        window.document.getElementById("cc").disabled = true;
    }
    
     loadKNPCHierarchy(){
        this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.knpcHierarchy = data);
    }
    
   viewDocument(fileToView: string){
       window.open(fileToView, '_blank', 'fullscreen=no');
       //window.open(fileToView, "_blank", "toolbar=yes,scrollbars=yes,resizable=yes,top=0,left=0,width="+screen.width+",height="+screen.height+""); 
  } 
    
  setSelectedFolder(event) {
  //Added By Rameshwar
    //document.getElementById("addLoader").innerHTML = "<div id='displayfade_in' class='ieblocker modal-backdrop fade in' style='display:block;'><div id='loader_MD_model' style='display:block;' ></div></div>";
   // document.getElementById("addLoader").innerHTML = "<div id='displayfade_in' class='ieblocker modal-backdrop fade in' style='display:block;position:absolute'><div id='loader_MD_model' style='display:block;left: -8%;top: -54%;position: absolute;'></div></div>";
  
      this.selectedNode = event.node
     // this.onNodeChange.emit(this.selectedNode)
      //this.browseService.getContainees(this.selectedNode.id).subscribe(data => this.addTreeNodes(data))
    
   
  }
    addTreeNodes(data){
     
     this.constructSubTree(data.folders);
  }
    
    constructSubTree(data) {
    let nodes = [];
    if(this.selectedNode != undefined){
        for (var i = 0; i < data.length; i++) {
      var test = data[i].path.replace((this.selectedNode.path),"");
      var chain = test.split("/");
      chain.shift();
      var nodes_list = nodes;
      for (var j = 0; j < chain.length; j++) {
        var wantedNode = chain[j];
        var lastNode = nodes_list;
        for (var k = 0; k < nodes_list.length; k++) {
          if (nodes_list[k].name == wantedNode) {
            nodes_list = nodes_list[k].children;
            break;
          }
        }
        if (lastNode == nodes_list) {
          var newNode = nodes_list[k] = { id: data[i].id, name: wantedNode, path: data[i].path,label:wantedNode,expandedIcon: "fa-folder-open",collapsedIcon: "fa-folder", children: []};
          nodes_list = newNode.children;
        }
      }
    }
        this.selectedNode.children = nodes;
        }
    //Added By Rameshwar
     document.getElementById("addLoader").innerHTML = "";
      
  }
  
    getTreeNodes( objs: any, isChildren: boolean): TreeNode[] {
            let _nodes: TreeNode[] = [];
            for ( let i = 0; i < objs.length; i++ ) {
                let tn: TreeNode = {};
                let obj = objs[i];
                if(isChildren){
                    tn.label = obj.path.substr(obj.path.lastIndexOf("/")+1, obj.path.length-1)
                    tn.ParentPath = obj.path.substr(0,obj.path.lastIndexOf("/") )
                }else{
                    tn.label = obj.path.substr(1, obj.path.length-1)
                    tn.ParentPath = global.os_name;
                }
                tn.data = obj.id; //"Documents Folder";
                tn.path = obj.path;
                tn.expandedIcon = "fa-folder-open";
                tn.collapsedIcon = "fa-folder";
                //tn.selectable = false;
              tn.leaf = false;
                tn.children = [];
                if ( obj.documents ) {
                    for ( let i = 0; i < obj.documents.length; i++ ) {
                        let child: TreeNode = {};
                        child.label = obj.documents[i].documentName;
                        child.data = obj.documents[i].documentId; //"Documents Folder";
                        child.icon = "fa-file-word-o";
                        tn.children.push( child );
                    }
                }
                _nodes.push( tn );
            }
            return _nodes;
        }
    loadNode( event ) {
        if ( event.node ) {
            this.browseService.getfolderTree( encodeURIComponent(event.node.path) ).subscribe( result => {
                event.node.children = this.getTreeNodes( result, true );
            } );
        }
    }
    handleDocumentChange(doc){
        if(this.documents_items && this.documents_items.length > 0){
            for(var i = 0;i<this.documents_items.length; i++){
            
                if(this.documents_items[i].id == doc.id ){
                    
                        this.documents_items[i] = doc;
                        this.documents_items = this.documents_items.slice();
            
                }
            
            
            }
            
        }
    }
}

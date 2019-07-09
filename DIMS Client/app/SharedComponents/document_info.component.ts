import {Component, Input, Output, EventEmitter} from '@angular/core'
import {WorkflowService} from './../services/workflowServices/workflow.service'
import {RecipientsService} from './../services/recipients.service'
import {UserService} from './../services/user.service'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker,IMyInputFieldChanged} from 'mydatepicker';
import global = require('./../global.variables')

@Component({
  selector: 'document-info',
  templateUrl: 'app/SharedComponents/document_info.component.html'
})

export class DocumentInfo{
  private base_url:string
  private attachmentProperties = []
  private attachmentVersions = []
  private attachmentHistory = []
  private attachmentFolders = []
  private historyDetails: any
  private departmentsAdv :any;  
  private current_user: any;
  private currentDelegateForEmployeeLogin: string = "";
  private documentType : any;
  private document_from : any
  private document_to : any
  private selectedSite_to : any
  private document_to_selected : any[] = [];
  private preActionDetailId : any;
  private prevWorkflowId : any;
  DocumentDate:string;
  private isCreator : boolean = false;
  /*private myDatePickerOptions: IMyOptions ={
    editableDateField:false
  };*/
  private myDatePickerOptions: IMyOptions =global.date_picker_options;
  private docDate: IMyDate = {year: 0, month: 0, day: 0}; 
     private recDate: IMyDate = {year: 0, month: 0, day: 0};
  modifyPropFormData: FormData;
  selectedFirstTab : any;
  @Input() selectedAttachmentID: string;
  @Input() selectedDocument: any; 
  @Input() currentTab: any;
  @Output() docAfterChange: EventEmitter<any> = new EventEmitter<any>();
  docChanged : boolean =false;

  constructor(private workflowService:WorkflowService,private recipientsService: RecipientsService, private userService: UserService) {
    this.base_url = global.base_url;
    if(this.currentTab == undefined) {
        this.currentTab = "Properties"
    }
   
    this.current_user = this.userService.getCurrentUser();
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }

    this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => {
        this.document_from = data;
        this.document_to = data;
    });
    
     
  }
 
  ngOnChanges() {
    // console.log("current Tab :: ",this.currentTab);
    // console.log("selected Tab :: ",this.selectedFirstTab)
    if(this.currentTab == undefined) {
        this.currentTab = "Properties"
    }

    if(this.currentTab == "")
        this.currentTab =this.selectedFirstTab;
    else
        this.selectedFirstTab = this.currentTab;

    // console.log("current Tab AFTER:: ",this.currentTab);
    // console.log("selected Tab AFTER:: ",this.selectedFirstTab)

       
    if (this.selectedAttachmentID != null) {
      let split = this.selectedAttachmentID.split('~~');
      this.selectedAttachmentID = split[0];
      this.getAttachmentProperties();
      this.getAttachmentVersions();
      this.getAttachmentVersions();
      this.getAttachmentHistory();
      this.getAttachmentFolders();
    }
  }

  
  getWorkflowHistroy(workitemId:any, workflowID:any){
    
    this.workflowService.getItemsHistory(workitemId,workflowID).subscribe(data => this.getHistoryDetails(data,workflowID));
  }

  getWorkflowHistroyByDept(workitemId:any, workflowID:any){
    this.workflowService.getItemsHistoryByDept(workitemId,workflowID).subscribe(data => this.getHistoryDetails(data,workflowID));
  }
  
  getHistoryDetails(data: any , workflowID: any){
      this.historyDetails = [];
      this.historyDetails = data;
      this.preActionDetailId = undefined;
      if(this.prevWorkflowId == undefined){
         this.prevWorkflowId = workflowID;
     }else if(this.prevWorkflowId != workflowID){
         if(document.getElementById("collapseWorkflowHistory"+this.prevWorkflowId).className == "panel-collapse collapse in"){
            document.getElementById("collapseWorkflowHistory"+this.prevWorkflowId).className = "panel-collapse collapse";
         }
         this.prevWorkflowId = workflowID;
     }
     //console.log(document.getElementById("collapseWorkflowHistory"+workflowID).className);
  }     
  
  getAttachmentProperties(){
    this.workflowService.getDocumentProperties(this.selectedAttachmentID).subscribe(data => this.asssignData(data))
  }

  getAttachmentVersions(){
    this.workflowService.getAttachmentVersions(this.selectedAttachmentID).subscribe(data => this.attachmentVersions = data)
  }

  getAttachmentHistory(){
    this.workflowService.getAttachmentHistory(this.selectedAttachmentID).subscribe(data => this.attachmentHistory = data)
  }

  getAttachmentFolders(){
    this.workflowService.getAttachmentFolders(this.selectedAttachmentID).subscribe(data => this.attachmentFolders = data)
  }
  
  asssignData(propertiesData: any){
      
      this.recipientsService.loadKNPCHierarchy(this.current_user.employeeLogin).subscribe(data => this.assignDepartment(data,propertiesData));
  }
    
  assignDepartment(departData :any ,propertiesData : any){
      
      this.recipientsService.getDocumentType().subscribe(data=> this.assignDocumentType(data,departData,propertiesData));
  }
    
    assignDocumentType(documentTypedata,departData,propertiesData){
        this.documentType = [];
        this.attachmentProperties =[];
        this.departmentsAdv = [];
        
        this.documentType = documentTypedata;
        this.attachmentProperties = propertiesData;
        for(let prop of this.attachmentProperties){
            if(prop.propertyName == 'Creator'){
                if(prop.propertyValue == this.current_user.employeeLogin){
                    this.isCreator = true;
                    break;
                }else{
                     this.isCreator = false;
                    break;
                }
            }
            
            
            if(prop.propertyName == 'DateReceived'){
                if(prop.propertyValue !=undefined){
                    let date = prop.propertyValue.split("/");
                        this.recDate = {year: date[2], month: date[1].replace(/\b0+/g, ''), day: date[0].replace(/\b0+/g, '')};
                }
             }
             if(prop.propertyName == 'DocumentTo'){
                if(prop.propertyValue !=undefined){
                    let siteIds = prop.propertyValue.split(";");
                    this.document_to_selected = [];
                    for(var i = 0; i < siteIds.length; i++) {
                        for(var j = 0; j < this.document_to.length; j++) {
                            if(this.document_to[j].siteId == siteIds[i] ){
                                this.document_to_selected.push(this.document_to[j]);
                            }
                        }
                    }

                }
             }
            if(prop.propertyName == 'DocumentDate'){
                if(prop.propertyValue !=undefined){
                    let date = prop.propertyValue.split("/");
                        this.docDate = {year: date[2], month: date[1].replace(/\b0+/g, ''), day: date[0].replace(/\b0+/g, '')};
                }
             }
        }
        this.departmentsAdv = departData;
        
    }
    
    openClosePanel(workflowId,index){
     if(this.preActionDetailId == undefined){
         this.preActionDetailId = workflowId+index;
         if(document.getElementById("show"+this.preActionDetailId).className == "panel-collapse collapse"){
            document.getElementById("show"+this.preActionDetailId).className = "panel-collapse collapse in";
            document.getElementById("show"+this.preActionDetailId).style.height = "auto";
         }
     }else if(this.preActionDetailId != (workflowId+index)){
         
         if(document.getElementById("show"+this.preActionDetailId).className == "panel-collapse collapse in"){
            document.getElementById("show"+this.preActionDetailId).className = "panel-collapse collapse";
         }
         this.preActionDetailId = workflowId+index;
         if(document.getElementById("show"+this.preActionDetailId).className == "panel-collapse collapse"){
            document.getElementById("show"+this.preActionDetailId).className = "panel-collapse collapse in";
            document.getElementById("show"+this.preActionDetailId).style.height = "auto";
         }
     }
    }
    
    modifyDocProperties($event: any){
       var inputTags = document.getElementsByTagName('input');
       var inputTagList = Array.prototype.slice.call(inputTags);
       var EmailSub = "";
       for (let inputField of inputTagList) {

           for( let prop of this.attachmentProperties){
            
               if(inputField.id !=undefined && inputField.id == prop.propertyName){
                   
                   if(inputField.id =='ReferenceNo'){
                       prop.propertyValue = inputField.value
			            if(this.selectedDocument && this.selectedDocument.referenceNo ){
                           this.selectedDocument.referenceNo = inputField.value;
                           this.docChanged =true;
                       }
                   }
                   
                   if(inputField.id =='DocumentDate'){
                       prop.propertyValue = inputField.value
			            if(this.selectedDocument && this.selectedDocument.documentDate ){
                           this.selectedDocument.documentDate = inputField.value;
                           this.docChanged =true;
                       }
                   }

                  /* if(inputField.id =='DocumentTitle'){
                       prop.propertyValue = inputField.value
                       if(this.selectedDocument && this.selectedDocument.referenceNo ){
                           this.selectedDocument.symbolicName = inputField.value
                           this.docChanged =true;
                       }
                      
                   }*/

                   if(inputField.id =='EmailSubject'){
                    prop.propertyValue = inputField.value
                    EmailSub = inputField.value;
                    if(this.selectedDocument && this.selectedDocument.subject ){
                        this.selectedDocument.subject = inputField.value;
                        this.selectedDocument.EmailSubject = inputField.value;
                        this.docChanged =true;
                    }
                   
                }
                   
               }
           }
           
       }
       
       for( let prop of this.attachmentProperties){
           if(prop.propertyName =='DocumentTitle'){
                prop.propertyValue = EmailSub;
                if(this.selectedDocument && this.selectedDocument.DocumentTitle ){
                   this.selectedDocument.symbolicName = EmailSub;
                  }
            }
            if(prop.propertyName == 'DocumentDate'){
               // this.selectedDocument.documentDate =   this.DocumentDate.date.day+"/"+this.DocumentDate.date.month+"/"+this.DocumentDate.date.year.substring(0,4);
                this.docChanged =true;
                }
            if(prop.propertyName =='DocumentTo'){
                prop.propertyValue = "";
                for(var i = 0; i < this.document_to_selected.length; i++) {
                  prop.propertyValue += this.document_to_selected[i].siteId + ";";
                   }
               
             }
       }
    
        
       var selectTags = document.getElementsByTagName('select');
       var selectTagList = Array.prototype.slice.call(selectTags);
       for (let selectField of selectTagList) {
           for( let prop of this.attachmentProperties){
               if(selectField.id !=undefined && selectField.id == prop.propertyName){
                    if(selectField.options.selectedIndex != -1){
                        if(selectField.id =='DocumentFrom'){
                          prop.propertyValue = selectField.options[selectField.options.selectedIndex].value;
                       }
                       
                       
                       
                       if(selectField.id =='DocumentType'){
                           prop.propertyValue = selectField.options[selectField.options.selectedIndex].value
                       }
                       
                       if(selectField.id =='CorrespondenceType'){
                           prop.propertyValue = selectField.options[selectField.options.selectedIndex].value
                       }
                
                    }
               }
           }
           
       }
        for (let dateField of [].slice.call(document.getElementsByTagName('my-date-picker'))) {
            console.log(dateField.id +"  ::: "+dateField.childNodes[0].children[0].children[0].value);
           for( let prop of this.attachmentProperties){
                   if(dateField.id !=undefined){
                       if(dateField.id == prop.propertyName+"recDate"){
                          prop.propertyValue = this.getDateInFormate(dateField.childNodes[0].children[0].children[0].value);
                       }
                       
                       if(dateField.id == prop.propertyName+"docDate"){
                           prop.propertyValue = this.getDateInFormate(dateField.childNodes[0].children[0].children[0].value);
                       }
                      
                   }
               }
        }
       
        this.modifyPropFormData = new FormData();
        var submissionValues: any = {
          "properties":this.attachmentProperties,
          "objectStoreName": global.os_name,
          "documentID": this.selectedAttachmentID
        };
        
        this.modifyPropFormData.append('modifyPropJson',JSON.stringify(submissionValues));
        this.workflowService.updateDocumentProperties(JSON.stringify(submissionValues)).subscribe(_res => {
           if( this.docChanged ){
               this.docAfterChange.emit(this.selectedDocument); 
           }
        });
        
    }

    getDateInFormate(targetDate){
       // var d = new Date(targetDate);
        //return d.getDate()+"/"+(d.getMonth()+1)+"/"+d.getFullYear();
        return targetDate;
    }
    
    validateDate(proName){
        var chkdate = document.getElementById(proName).value;
        if(!chkdate.match(/^(0[1-9]|[12][0-9]|3[01])[\- \/.](?:(0[1-9]|1[012])[\- \/.](201)[2-9]{1})$/))
        {
            //alert('date format is wrong');
            return false;
        }

    }
    onDocToChange(value){
        var found = false;
        for(var i = 0; i < this.document_to_selected.length; i++) {
            if (this.document_to_selected[i].siteId == this.selectedSite_to.siteId) {
                found = true;
                break;
            }
        }
        if(!found){
            this.document_to_selected.push(this.selectedSite_to);
        }
        
    }
    
}

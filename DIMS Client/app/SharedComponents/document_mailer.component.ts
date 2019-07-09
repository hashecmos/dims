import {Component, Input, ViewChild} from '@angular/core'
import {BrowseService} from './../services/browse.service'
import {UserService} from '../services/user.service'
import {EmployeeDetails} from './../model/employeeDetails.model'

import global = require('./../global.variables')

@Component({
  selector: 'document-mailer',
  templateUrl: 'app/SharedComponents/document_mailer.component.html'
})

export class DocumentMailer{
  public emailSearchUrl: string="";
  public toEmail: string="";
  public toList: any=[];
  public ccEmail: string="";
  public ccList: any=[];
  public documents: any;
  private selectedAttachmentID: any=[];
  emailFormData: FormData;
  emailRecipientList : any =[];
  private current_user: EmployeeDetails;
  private body : string = "This is an Automated message sent to you using DIMS."
  private subject : any;
  private disableClose : boolean = false;
  private selectDocument : any;

  @ViewChild('documentMailerModal') modal; 
  @ViewChild('documentMailerForm') inputForm; 

  constructor(private browseService: BrowseService,private userService: UserService) {
      
    this.emailSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
    this.current_user = this.userService.getCurrentUser();
  }

  toEmailSelected(data){
    if(data.employeeEmail){
      this.toList.push(data.employeeEmail);
      this.toEmail = "";
      setTimeout(() => {
          document.getElementById("to").value = "";
        }, 100);
    }
  }

  ccEmailSelected(data){
    if(data.employeeEmail){
      this.ccList.push(data.employeeEmail);
      this.ccEmail = "";
      setTimeout(() => {
          document.getElementById("cc").value = "";
        }, 100);
    } 
  }

  removeFromList($event:any, email:string, list:any){
    list.splice(list.indexOf(email), 1);
    $event.preventDefault();
    $event.stopPropagation();
    return false;
  }

  openPopup(selectedAttachmentID: any){
      this.documents =[];
      var documentIds =[];
      this.disableClose = false;
      this.selectDocument = selectedAttachmentID;
      this.selectedAttachmentID =[];
        for(let id of selectedAttachmentID)
        {
            if(id.document_id != undefined){
                documentIds.push(id.document_id);
                this.selectedAttachmentID.push(id.document_id)
            }
          //this.browseService.loadDocumentsFileProperties([id.document_id]).subscribe(data => this.updateDocumentName(data))
        }
      
        
        if(documentIds.length == 0){
          documentIds.push(selectedAttachmentID[0]);
            this.selectedAttachmentID.push(selectedAttachmentID[0])
        }
          this.browseService.loadDocumentsFileProperties(documentIds).subscribe(data => this.updateDocumentName(data));
      }
     
      removeDocument($event:any,doc:any){
        var index = this.selectedAttachmentID.indexOf(doc.docId);
        if(this.selectedAttachmentID.length>1){
          for(var i = 0 ; i<this.selectedAttachmentID.length ; i++){
            if(this.selectedAttachmentID[i] == doc.docId){
              if (index > -1) {
                this.selectedAttachmentID.splice(index, 1);
                this.documents.splice(index,1);
            }
            }
          }
        }else{
          this.disableClose = true;
        }
        $event.preventDefault();
        $event.stopPropagation();
        return false;
      }
      
    updateDocumentName(data){
      this.resetForm();
        for(let doc of data){
            this.documents.push(doc)
        }
        this.body =  "This is an Automated message sent to you using DIMS.";
        this.toEmail = "";
        this.toList = [];
        this.ccEmail = "";
        this.ccList = [];
        this.ccList.push(this.current_user.employeeEmail);
        this.modal.modalClass ="modal-lg";
        this.modal.open();
         //Added By Rameshwar 30102017
        var subjectVal = ''; 
        if(this.selectDocument.length > 1){
          subjectVal = "Multi-Documents";
        }else {
          if(this.selectDocument[0].subject != null || this.selectDocument[0].subject != 'undefined' || this.selectDocument[0].subject !=""){
            subjectVal = ''+this.selectDocument[0].subject +'';
          }
        }
        console.log("subject subjectVal ::: ",subjectVal);
        this.subject = subjectVal;
    }
  sendDocumentMail($event: any, bodyContent: any,subject: any){
    this.emailFormData = new FormData();
    var submissionValues: any = {
          "from":this.current_user.employeeEmail,
          "subject": subject,
          "body": bodyContent,
          "osName": global.os_name,
          "objectId": this.selectedAttachmentID,
          "emailRecipientList": []
        };
    this.toList.map(u => submissionValues["emailRecipientList"].push({
      "emailRecipient": u,
      "emailRecipientType": "to"
    }));
    this.ccList.map(u => submissionValues["emailRecipientList"].push({
      "emailRecipient": u,
      "emailRecipientType": "cc"
    }));

    
    this.emailFormData.append('emailJson',JSON.stringify(submissionValues));
    if((this.ccList.length<1 && this.toList.length<1)){
      document.getElementById("to").focus();
      return false;
    }else{
      this.browseService.sendDocumentMail(JSON.stringify(submissionValues)).subscribe();
      this.modal.close();
      $event.preventDefault();
      $event.stopPropagation();
      return false;
    }
   
  }
    
  resetForm(){
    this.subject = '';
      this.toList = [];
      this.ccList = [];
      this.body = '';
      this.inputForm.nativeElement.getElementsByClassName('form-control')[2].value = "";
  }
    
}

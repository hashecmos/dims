import {Component, ViewChild} from '@angular/core'
import {BrowseService} from './../services/browse.service'

import global = require('./../global.variables')

@Component({
  selector: 'document-link',
  templateUrl: 'app/SharedComponents/document_link.component.html'
})

export class DocumentLink{
  private documentLink: string;
  private documentName: string;
  private selectedAttachmentID: string;
  @ViewChild('documentLinkModal') modal; 

  constructor(private browseService:BrowseService) {
  }

  openPopup(selectedAttachmentID: string){
    this.selectedAttachmentID = selectedAttachmentID;
    this.browseService.loadDocumentsFileProperties([this.selectedAttachmentID]).subscribe(data => this.documentName = data[0].fileName)
    this.documentLink = global.viewer_url+'id='+this.selectedAttachmentID+'&objectStoreName=DIMS&objectType=document'
    /*  '/bookmark.jsp?desktop='+global.os_name+'&repositoryId='+global.os_name+'&repositoryType=p8&docid=Correspondence%2C%7B8FF4CCAC-398F-435E-BF42-E65E230E65A2%7D%2C'+
      +'&version=released'*/
    this.modal.open();
  }
}

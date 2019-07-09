import {Component, Input} from '@angular/core'
import {WorkflowService} from './../services/workflowServices/workflow.service'
import global = require('./../global.variables')
import {BrowseSharedService} from './../services/browseEvents.shared.service'


@Component({
  selector: 'new-folder-btn',
  templateUrl: 'app/SharedComponents/newFolder.component.html'
})

export class NewFolder {
  @Input() private folderFor
  private userName
  private folderName
  private userSearchUrl
  constructor(private workflowService: WorkflowService, private sharedService: BrowseSharedService) {
    this.userSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
    this.userName = ""
    this.folderName = ""
  }

  createFolder(userListModal) {
    if (this.folderName) {
      
      this.workflowService.getworkflowFolder(this.folderFor).subscribe(data => this.checkFolderIfExist(data,userListModal));
     
    }
  }
  
  checkFolderIfExist(data,userListModal){
     let folderExist = false;
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
    for(let folder of folderData){
        if(folder.folderName == this.folderName){
          folderExist = true;
          break;          
        }
    }
    if(folderExist){
       document.getElementById("folderMsg").style.display = "block";
    }else{
      document.getElementById("folderMsg").style.display = "none";
      this.workflowService.createWorkflowFolder(this.folderFor, this.folderName).subscribe(data => this.updateView(userListModal, data), error => this.sharedService.emitMessageChange(error))
    }
    
  }
  clearMsg(){
    document.getElementById("folderMsg").style.display = "none";
  }
  updateView(userListModal, data) {
    userListModal.close()
    this.sharedService.emitSideNavigationReload(this.folderFor)
    this.folderName = ""
      this.userName = ""
    this.sharedService.emitMessageChange(data)
  }

  setFolderName(data) {
    if (data && data.employeeLogin) {
      this.folderName = data.employeeLogin
    }
  }
}

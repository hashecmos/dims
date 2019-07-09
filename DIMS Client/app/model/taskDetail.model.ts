import {WorkflowRecipientList} from '../model/workflowRecipientList.model'
import {WorkflowAttachments} from '../model/workflowAttachments.model'

export class TaskDetail {
  public workflowWorkItemID: string;
  public workflowStepNo: number;
  public workflowInstruction: string;
  public workflowItemReceivedOn: any;
  public workflowItemDeadline: any;
  public workflowItemReminderDate: any;
  public workflowItemRootSender: string;
  public workflowItemStatus: string;
  public workflowItemSenderDepartment: number;
  public workflowItemReceiverDepartment: number;
  public workflowItemSenderDiv: number;
  public workflowItemReceiverDiv: number;
  public workflowItemSubject: string;
  public workflowSenderName: string;
  public workflowWorkItemType: string;
  public workflowRecipientList: WorkflowRecipientList[];
  public workflowAttachments: WorkflowAttachments[];

  assignAttribiteValuesFromData(data) {
    for (let attribute of Object.keys(data)) {
      this[attribute] = data[attribute];
    }
  }

  assignMimeTypeDataToAttachments(mimeTypeData: any) {
    var index = 0;
    for (let attachment of this.workflowAttachments) {
        if(attachment.workflowDocumentId == mimeTypeData[index].docId && mimeTypeData[index].fileName != 'Document not available'){
            attachment.fileName = mimeTypeData[index].fileName;
            attachment.mimeType = mimeTypeData[index].mimeType;
        }else{
           this.workflowAttachments.pop(attachment);
        }
     
      index++;
    }
  }

  public mimeTypeIcon(index: number) {
    if (this.workflowAttachments[index].mimeType == undefined) {
         return "fa fa-file-o"
    } else if (this.workflowAttachments[index].mimeType.indexOf('image') > -1) {
         return "fa-file-image-o";  
    } else if (this.workflowAttachments[index].mimeType.indexOf('application') > -1) {
          if (this.workflowAttachments[index].mimeType.indexOf('application/pdf') > -1) {
                return "fa-file-pdf-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') > -1) {
                return "fa-file-excel-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/vnd.openxmlformats-officedocument.wordprocessingml.document') > -1) {
                return "fa-file-word-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/vnd.openxmlformats-officedocument.presentationml.presentation') > -1) {
                return "fa-file-powerpoint-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/msword') > -1) {
                return "fa-file-word-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/zip') > -1) {
                return "fa-file-zip-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/x-zip-compressed') > -1) {//
                return "fa-file-zip-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/xlsx') > -1) {
                return "fa-file-excel-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/ppt') > -1) {
                return "fa-file-powerpoint-o"
          } else if (this.workflowAttachments[index].mimeType.indexOf('application/vnd.ms-excel') > -1) {
                return "fa-file-excel-o"
          } else {
                return "fa fa-file-o";
          }
    } else if (this.workflowAttachments[index].mimeType.indexOf('text') > -1) {
         return "fa-file-text-o"
    } else if (this.workflowAttachments[index].mimeType.indexOf('video') > -1) {
         return "fa-file-video-o"
    } else if (this.workflowAttachments[index].mimeType.indexOf('audio') > -1) {
         return "fa-file-audio-o"
    } else {
         return "glyphicon-tags"
    }
  }
}

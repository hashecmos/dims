import {DocumentAttachment} from '../model/docAttachmentContainee.model'
import {DocumentVersion} from '../model/docVersionContainee.model'
import {FolderFiledIn} from '../model/folderFileContainee.model'

export class LaunchPrimaryDoc {
  public document_id: string;
  public symbolicName: string;

  constructor(attrs: any) {
    this['document_id'] = attrs['workflowDocumentId'];
    this['symbolicName'] = attrs['fileName'];
  }
}

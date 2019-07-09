import {DocumentAttachment} from '../model/docAttachmentContainee.model'
import {DocumentVersion} from '../model/docVersionContainee.model'
import {FolderFiledIn} from '../model/folderFileContainee.model'

export class DocumentInFolder {
  public classDescription: string;
  public className: string;
  public createdBy: string;
  public dateCreated: string;
  public dateModified: string;
  public hasLinks: boolean;
  public hasMultipleAttachments: boolean;
  public id: string;
  public document_id: string;
  public lastModifiedBy: string;
  public majorVersion: number;
  public mimeType: string;
  public objectStoreName: string;
  public objectType: string;
  public parentPath: string;
  public reserved: boolean;
  public size: number;
  public symbolicName: string;
  public versionSeriesId: string;
  public attachments: DocumentAttachment[];
  public foldersFiledIn: FolderFiledIn[];
  public versions: DocumentVersion[];
  public is_checked: boolean = false;
  public showOptions: boolean = false;

  constructor(attrs: any) {
    this['document_id'] = attrs['id']
    for (let attr in attrs) {
      this[attr] = attrs[attr];
    }
  }
}

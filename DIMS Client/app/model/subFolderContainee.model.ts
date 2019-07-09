
export class SubFolder {
  public classDescription: string;
  public className: string;
  public createdBy: string;
  public dateCreated: string;
  public folderPath: string;
  public id: string;
  public isFilingAllowed: string;
  public lastModifiedBy: string;
  public objectStoreName: string;
  public objectType: string;
  public parentPath: string;
  public path: string;
  public symbolicName: string;
  public type: string;
  public Name: string;
  public is_checked: boolean = false;
  public visible: boolean = false;
  constructor(attrs: any) {
    for (let attr in attrs) {
      this[attr] = attrs[attr];
    }
  }
  getFolderName() {
    var splitPath = this.path.split("/")
    return splitPath[splitPath.length - 1]
  }
}

import {LaunchWorkflowSearchFilter} from '../model/launchWorkflowSearchFilter.model'
import global = require('../global.variables')

export class SimpleSearchRequestObject{
  searchBaseType: string
  objectStore: string
  docClassName: string[]
  filter: LaunchWorkflowSearchFilter[];
  folderName: string
  content: string
  includeSubClasses: boolean
  searchType: string
  includeSubFolders: boolean
  crossDepartment: boolean
  constructor(){
    this.searchBaseType = "Document"
    this.objectStore = global.os_name;
    this.docClassName = ['Correspondence']
    this.folderName = ''
    this.content = ''
    this.filter = [];
    this.includeSubClasses = false
    this.searchType = "SimpleSearch"
    this.includeSubFolders = false
  }
}

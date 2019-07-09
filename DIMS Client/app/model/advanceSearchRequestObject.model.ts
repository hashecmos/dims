import {SimpleSearchRequestObject} from '../model/simpleSearchRequestObject.model'

export class AdvanceSearchRequestObject extends SimpleSearchRequestObject{
  constructor(){
    super()
    this.searchType = "AdvanceSearch"
  }

}

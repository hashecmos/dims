import {SimpleSearchForm} from '../model/simpleSearchForm.model'

export class AdvanceSearchForm {
  // super
  DocumentTitleSearchOperator: string = "like"
  idSearchOperator: string = "like"
  SubjectSearchOperator: string = "like"
  dateCreatedSearchOperator: string = "equal";

  public attributes: any = [];
  constructor(data?: any) {
    for (let attr in data) {
      if (data[attr].propertyName != "") {
        var prop = {};
        prop[data[attr].propertyName] = data[attr].propertyValue;
        this.attributes.push(prop);
      }
    }
  }

  getPropertyForIndex(index: number) {
    if (this.attributes.length > 0) {
      return Object.keys(this.attributes[index])
    } else {
      return null;
    }
  }

}

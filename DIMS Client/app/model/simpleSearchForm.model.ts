export class SimpleSearchForm{
  DocumentID : any
  ReferenceNo : string
  DocumentTitle : string;
  dateCreated : any=(new Date());
  attributeDataTypeMap(){
    return {DocumentID: "String", ReferenceNo: "String", DocumentTitle: "String", dateCreated: "Date"}
  }
}

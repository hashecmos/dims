
export class DocumentResult{
  formatedCreatedDate: string
  constructor(private className: string,
    private symbolicName: string,
    private subject:string,
    private document_id: any,
    private dims_id:string,
    private dateCreated:any,
    private objectType:string){
    if(this.dateCreated != null && this.dateCreated != ""){
      this.formatedCreatedDate = new Date(this.dateCreated).toISOString().split('T')[0]
    }
  }
}

export class ReportDocumentsScanned {
  department: string;
  division: string;
  fromDate: any;
  toDate: any;
}

export class DocumentsScannedProperties {
  public totalWorkFlows: string;
  public newWorkFlows: string;
  public activeWorkFlows: string;
  public overdueWorkFlows: string;
  public documentsScannedPropertiesList: DocumentsScannedPropertiesList[];
}

export class DocumentsScannedPropertiesList {
  public division: string;
  public all: string;
  public newWorks: string;
  public active: string;
  public overdue: string;
}

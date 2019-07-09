export class PendingWorkflowsSpecific {
  department: string;
  senderName: string;
  filterUsers: string;
  itemStatus: string;
  reciepient: string;
  fromDate: any;
  toDate: any;
}

export class PendingWorkflowsSpecificProperties {
  public totalWorkFlows: string;
  public newWorkFlows: string;
  public activeWorkFlows: string;
  public overdueWorkFlows: string;
  public pendingWorkflowsSpecificPropertiesList: PendingWorkflowsSpecificPropertiesList[];
}

export class PendingWorkflowsSpecificPropertiesList {
  public division: string;
  public all: string;
  public newWorks: string;
  public active: string;
  public overdue: string;
}

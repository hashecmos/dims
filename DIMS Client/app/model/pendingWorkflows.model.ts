export class PendingWorkflows {
  department: string;
  division: string;
  fromDate: any;
  toDate: any;
}

export class PendingWorkflowsProperties {
  public totalWorkFlows: string;
  public newWorkFlows: string;
  public activeWorkFlows: string;
  public overdueWorkFlows: string;
  public pendingWorkflowsPropertiesList: PendingWorkflowsPropertiesList[];
}

export class PendingWorkflowsPropertiesList {
  public division: string;
  public all: string;
  public newWorks: string;
  public active: string;
  public overdue: string;
}

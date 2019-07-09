export class PendingWorkflowsFullHistory {
  department: string;
  division: string;
  fromDate: any;
  toDate: any;
}

export class PendingWorkflowsFullHistoryProperties {
  public totalWorkFlows: string;
  public newWorkFlows: string;
  public activeWorkFlows: string;
  public overdueWorkFlows: string;
  public pendingWorkflowsFullHistoryPropertiesList: PendingWorkflowsFullHistoryPropertiesList[];
}

export class PendingWorkflowsFullHistoryPropertiesList {
  public division: string;
  public all: string;
  public newWorks: string;
  public active: string;
  public overdue: string;
}

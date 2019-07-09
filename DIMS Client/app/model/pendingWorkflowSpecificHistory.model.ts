export class PendingWorkflowSpecificList {
  PendingWorkflowSpecificList: PendingWorkflowSpecific[];
}
export class PendingWorkflowSpecific {
  workflowBeginDate: string
  workItemReceiveDate: string;
  workflowDeadline: string;
  pendingWorkflowList: PendingWorkflowList[];
}

export class PendingWorkflowList {
  workflowSender: string;
  workflowRecipient: string;
  workflowStatus: string;
  workflowType: string;
  workflowActionDate: string;
}

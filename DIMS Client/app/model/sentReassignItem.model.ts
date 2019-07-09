import {TaskDetail} from './taskDetail.model'

export class WorkItemDetail extends TaskDetail {
  super
  workflowItemAction: string;
  workflowItemActionComment: string;
  workflowItemActionBy: string;
  workflowItemActionOnBehalf: string;
  workflowSender: string;

}

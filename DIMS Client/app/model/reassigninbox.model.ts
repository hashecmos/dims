import {TaskDetail} from './taskDetail.model'

export class ReassignTask extends TaskDetail {
  super
  workflowItemAction: string;
  workflowItemActionComment: string;
  workflowItemActionBy: string;
  workflowItemActionOnBehalf: string;
  workflowSender: string;
}

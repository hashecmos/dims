import {TaskDetail} from './taskDetail.model'

export class DoneTask extends TaskDetail {
  super
  workflowItemActionComment: string
  workflowItemAction: string;
  workflowItemActionBy: string;
  workflowItemActionOnBehalf: string;
  workflowSender: string;
}

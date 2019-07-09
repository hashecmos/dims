import {TaskDetail} from './taskDetail.model'

export class ForwardTask extends TaskDetail {
  super
  workflowItemAction: string;
  workflowItemActionComment: string;
  workflowItemActionBy: string;
  workflowItemActionOnBehalf: string;
  workflowSender: string;
  DocumentClass: any
  deadline: any;
}

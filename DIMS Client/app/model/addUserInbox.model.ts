import {TaskDetail} from './taskDetail.model'


export class AddUserTask extends TaskDetail {
  super
  workflowItemActionComment: string
  workflowItemAction: string;
  workflowItemActionBy: string;
  workflowItemActionOnBehalf: string;
  workflowSender: string;
}

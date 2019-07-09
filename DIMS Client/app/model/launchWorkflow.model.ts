
export class LaunchWorkflow{
  subject : string
  name: string
  to :string
  cc : string
  comment :string
  deadline :any=(new Date());
  instruction: string
  priority: number;
}

import { Component } from '@angular/core';

import {WorkflowService} from './services/workflowServices/workflow.service'


@Component({
  selector: 'my-app',
  template: `
  <router-outlet></router-outlet>
  `,
  providers: [WorkflowService]
})
export class AppComponent  { name = 'Angular'; }

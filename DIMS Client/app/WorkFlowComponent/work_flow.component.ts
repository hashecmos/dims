import { Component } from '@angular/core'
import {WorkflowService} from '../services/workflowServices/workflow.service'
import {SimpleSearchForm } from './../model/simpleSearchForm.model'
import {LaunchWorkflow} from './../model/launchWorkflow.model'
import {DocumentResult} from './../model/documentResult.model'
import {AdvanceSearchRequestObject} from './../model/advanceSearchRequestObject.model'
import {SimpleSearchRequestObject} from './../model/simpleSearchRequestObject.model'
import {LaunchWorkflowSearchFilter} from '../model/launchWorkflowSearchFilter.model'
import {AdvanceSearchForm} from './../model/advanceSearchForm.model'
import { Router } from '@angular/router'
import { AccordionModule } from "ng2-accordion";
import {UserService} from '../services/user.service'
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import { ModalModule } from "ng2-modal"
import {BrowseService} from '../services/browse.service'
import {LaunchService} from '../services/launch.service'
import {FooterService} from '../services/footer.service'

import {RecipientsService} from '../services/recipients.service'
import {Directorate} from '../model/recipients.model'
import {User} from '../model/userRecipients.model'
import {Department} from '../model/departmentRecipients.model'
import {Division} from '../model/divisionRecipients.model'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';
import global = require('./../global.variables')
import * as _ from 'lodash';
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'work-flow',
  providers: [WorkflowService, BrowseService, LaunchService, RecipientsService,FooterService],
  templateUrl: 'app/WorkFlowComponent/work_flow.component.html'

})

export class WorkflowComponent {
  private activeTab: string
  private documentResults: DocumentResult[];
  private selected_doc: any;
  private filter = new LaunchWorkflowSearchFilter()
  private simpleSearchForm = new SimpleSearchForm()
  private advanceSearchForm: AdvanceSearchForm;
  private simpleSearchRequestObject = new SimpleSearchRequestObject();
  private advanceSearchRequestObject = new AdvanceSearchRequestObject();
  private newTask = new LaunchWorkflow();
  private first_level_support_staff: any = [];
  private second_level_support_staff: any = [];
  private third_level_support_staff: any = [];
    
  public roles: any;

  wradio_to: any = [];
  wradio_cc: any = [];


  classes: Array<Object> = [];
  public directorates: any = [];
  public instructions: any = [];
  public textComparisonOperators: string[] = ["is equal to", "contains", "starts with", "ends with"];
  public numberComparisonOperators: string[] = ["is equal to", ">", "<", "<=", ">="];
  private myDatePickerOptions: IMyOptions = global.date_picker_options
  private subscription: ISubscription[] = [];

  constructor(private router: Router, private workflowService: WorkflowService,
    private browseService: BrowseService, private launchService: LaunchService,
    private recipientsService: RecipientsService, private userService: UserService,private FooterService: FooterService,private browseSharedService: BrowseSharedService) {
    let d: Date = new Date();
    this.first_level_support_staff = this.userService.getFirstLevelSupportStaff();
    this.second_level_support_staff = this.userService.getSecondLevelSupportStaff();
    this.third_level_support_staff = this.userService.getThirdLevelSupportStaff();
    this.activeTab = 'simpleSearch';
    this.selected_doc = []
    this.documentResults = [];
    //this.launchService.getLaunchClasses().subscribe(data => this.classes = data);
    this.subscription.push(this.recipientsService.getDirectorates().subscribe(data => this.directorates = data));
    this.subscription.push(this.workflowService.getLaunchInstructions().subscribe(data => this.instructions = data));

  }


  switchTab(activeTab) {
    this.activeTab = activeTab;
  }



  removeFromList(event: any, value: any, listType: any) {
    if (listType == "wto") {
      this.wradio_to.splice(this.wradio_to.indexOf(value), 1)
    }
    if (listType == "wcc") {
      this.wradio_cc.splice(this.wradio_cc.indexOf(value), 1)
    }
    event.preventDefault();
    event.stopPropagation();
    return false;
  }

  ngOnInit() { }


  selectDocLaunch(e: any, doc: any) {
    if (e.currentTarget.checked == true) {
      this.selected_doc.push(doc);
    } else if (e.currentTarget.checked == false) {
      this.selected_doc.pop(doc);
    }
  }

  launch_existingdoc(launchModal: any) {
    this.newTask.deadline = this.newTask.deadline.formatted
    this.subscription.push(this.workflowService.launchDocuments(this.selected_doc, this.newTask, this.wradio_to, this.wradio_cc).subscribe(data=>this.browseSharedService.emitMessageChange(data),error=>this.browseSharedService.emitMessageChange(error)));
    this.selected_doc = []
    launchModal.close();
    this.router.navigate(['work-flow/sent-item'], { queryParams: { random: new Date().getTime() } })
  }

  SelectedToRadio(e: any, user: User) {
    if (this.wradio_to.indexOf(user.employeeLogin) == -1) {
      if (this.wradio_cc.indexOf(user.employeeLogin) > -1) {
        this.wradio_cc.splice(this.wradio_cc.indexOf(user.employeeLogin), 1)
      }
      this.wradio_to.push(user.employeeLogin)
    }
  }

  SelectedCcRadio(e: any, user: User) {
    if (this.wradio_cc.indexOf(user.employeeLogin) == -1) {
      if (this.wradio_to.indexOf(user.employeeLogin) > -1) {
        this.wradio_to.splice(this.wradio_to.indexOf(user.employeeLogin), 1)
      }
      this.wradio_cc.push(user.employeeLogin)
    }
  }

  loadUsersForDirectorate(directorate: Directorate) {
    this.subscription.push(this.recipientsService.getUsersForDirectorate(directorate).subscribe());
  }

  loadDepartmentsForDirectorate(directorate: Directorate) {
    this.subscription.push(this.recipientsService.getDepartmentsForDirectorate(directorate).subscribe());
  }

  loadUsersForDepartment(department: Department) {
    this.subscription.push(this.recipientsService.getUsersForDepartment(department).subscribe());
  }

  loadDivisionsForDepartment(department: Department) {
    this.subscription.push(this.subscription.push(this.recipientsService.getDivisionsForDepartment(department).subscribe()));
  }

  loadUsersForDivision(division: Division) {
    this.subscription.push(this.recipientsService.getUsersForDivision(division).subscribe());
  }
}

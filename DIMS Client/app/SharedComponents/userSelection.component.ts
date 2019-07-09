import {Component} from '@angular/core'
import {WorkflowService} from './../services/workflowServices/workflow.service'
import {Input, ViewChild, Output, EventEmitter} from '@angular/core'
declare var $: any;

@Component({
  selector: 'user-list',
  templateUrl: 'app/SharedComponents/userSelection.component.html'
})

export class UserSelect {
  @Input() currentTask: any;
  @Input() action
  @Output() notifyParentToClose: EventEmitter<any> = new EventEmitter();
  @Output() sendSelectedUsers: EventEmitter<any> = new EventEmitter();
  private userLists = []
  private allUsers = []
  @Input() selectedToList = []
  @Input() selectedCcList = []
  private To_radio
  private Cc_radio

  constructor(private workflowService: WorkflowService) {

  }

  ngOnInit() {
    this.getUserlist()
  }

  emitCloseEvent() {
    this.notifyParentToClose.emit()
    this.sendSelectedUsers.emit({ userlist: this.selectedToList, listtype: "to" })
    this.sendSelectedUsers.emit({ userlist: this.selectedCcList, listtype: "cc" })
  }

  fetchUsersOfList(listId) {
    this.workflowService.getUsersOfList(listId).subscribe(data => this.allUsers = data)
  }

  getUserlist() {
    this.workflowService.getUserlist().subscribe(data => this.userLists = data)
  }

  selectedTo(event, value: any): void {
    if (event.target.checked && this.selectedToList.indexOf(value) == -1) {
      if (this.selectedCcList.indexOf(value) >= 0) {
        this.selectedCcList.splice(this.selectedCcList.indexOf(value), 1)
      }
      this.selectedToList.push(value)
    }
  }

  selectedCc(event, value: any): void {
    if (event.target.checked && this.selectedCcList.indexOf(value) == -1) {
      if (this.selectedToList.indexOf(value) > -1) {
        this.selectedToList.splice(this.selectedToList.indexOf(value), 1)
      }
      this.selectedCcList.push(value)
    }
  }

  removeFromList(event, value, listtype) {
    switch (listtype) {
      case "To":
        this.selectedToList.splice(this.selectedToList.indexOf(value), 1)
        break;
      case "Cc":
        this.selectedCcList.splice(this.selectedCcList.indexOf(value), 1)
        break;
    }
    event.stopPropagation();
  }


  clearAllRadio(event: any) {
    event.stopPropagation();
    $(".clear").prop("checked", false);
    this.selectedToList = [];
    this.selectedCcList = [];
  }
}

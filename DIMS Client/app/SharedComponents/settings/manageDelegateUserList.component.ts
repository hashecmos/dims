import {Component, Output, EventEmitter} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseService} from './../../services/browse.service'
import {SettingsDelegateUserService} from './../../services/settingsDelegateUser.service'
import { AddDelegateUser } from './../../model/addDelegateUser.model'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';
import {UserService} from '../../services/user.service'
import {EmployeeDetails} from './../../model/employeeDetails.model'
import global = require('./../../global.variables')

@Component({
  selector: 'manage-delegate-user-list',
  providers: [BrowseService, SettingsDelegateUserService],
  templateUrl: 'app/SharedComponents/settings/manageDelegateUserList.component.html'
})

export class ManageDelegateUserList {
  @Output() notifyParenttofilter: EventEmitter<any> = new EventEmitter();

  delegatedUsers: any = [];
  addDelegateUser: AddDelegateUser;
  private current_user: EmployeeDetails;
  private myDatePickerOptions: IMyOptions = global.date_picker_options;
  deleteUser: any;
  modalHeader: any;
  private userSearchUrl;
  constructor(private settingsDelegateUserService: SettingsDelegateUserService, private browseService: BrowseService, private userService: UserService) {
    this.settingsDelegateUserService.getDelegateUserList().subscribe(data => this.delegatedUsers = data);
    this.userSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
    this.addDelegateUser = new AddDelegateUser();
    this.current_user = this.userService.getCurrentUser();
  }

  openDelegateUserModel(userListModal: any) {
    this.modalHeader = "Add Delegate User";
    this.addDelegateUser.absentLogin = this.current_user.employeeLogin;
    userListModal.open();
  }

  addModifyDelegatedUser(userListModal: any, action: any) {
    this.addDelegateUser.delegateFrom = this.addDelegateUser.delegateFrom.formatted
    this.addDelegateUser.delegateTo = this.addDelegateUser.delegateTo.formatted
    this.addDelegateUser.absentLogin = this.current_user.employeeLogin;
    this.addDelegateUser.delegateLogin = this.addDelegateUser.delegateLogin.employeeName;
    if (action == "Add Delegate User") {
      this.settingsDelegateUserService.addDelegateUser(this.addDelegateUser).subscribe(data => this.refreshDelegateUsers());
      userListModal.close();
    } else {
      this.settingsDelegateUserService.modifyDelegateUser(this.addDelegateUser).subscribe(data => this.refreshDelegateUsers());
    }

  }
  refreshDelegateUsers() {
    this.settingsDelegateUserService.getDelegateUserList().subscribe(data => this.delegatedUsers = data);
  }
  clearList() {
    this.addDelegateUser = new AddDelegateUser();
  }

  openDeleteDelegateUser(confirmDeleteModel: any, user: any) {
    this.deleteUser = user;
    confirmDeleteModel.open();
  }

  deleteDelegateUser() {
    this.settingsDelegateUserService.deleteDelegateUser(this.deleteUser.delegationId).subscribe(data => this.refreshDelegateUsers());
  }

  openModifyDelegateUser(userListModal: any, user: any) {
    this.modalHeader = "Modify Delegate User";
    this.addDelegateUser = user;
    userListModal.open();
  }

}

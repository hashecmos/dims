import {Component, Output, Input, EventEmitter} from '@angular/core'
import {RecipientsService} from './../services/recipients.service'
import {Directorate} from './../model/recipients.model'
import {User} from './../model/userRecipients.model'
import {Department} from './../model/departmentRecipients.model'
import {Division} from './../model/divisionRecipients.model'
import {BrowseSharedService} from './../services/browseEvents.shared.service'
import {UserService} from './../services/user.service'
@Component({
  providers: [RecipientsService],
  selector: 'directorate-accordion',
  templateUrl: 'app/SharedComponents/directorateAccordion.component.html'
})

export class DirectorateAccordion {
  private directorates;
  private divisions;
  private current_user: any;
  private currentDelegateForEmployeeLogin: string = "";
  
  @Input() currentTask: any;
  @Input() isCcAvailable: boolean;
  @Input() checkboxes: boolean;
  @Input() selectedUsers: string[];
  @Input() selectedToList: string[];
  @Input() selectedCcList: string[];
  constructor(private recipientsService: RecipientsService, private sharedService: BrowseSharedService, private userService: UserService) {
    this.current_user = this.userService.getCurrentUser();
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }
    this.userService.notifyDelegateForChange.subscribe(data => this.backClicked());
  }

  ngOnInit() {
    this.recipientsService.getDirectorates().subscribe(data => this.directorates = data);
    this.recipientsService.getDivision(this.current_user.employeeDivision.empDivisionCode,this.current_user.employeeLogin).subscribe(data => this.divisions = data);
  }

  isChecked(user) {
    if (this.selectedUsers.indexOf(user.employeeLogin) >= 0) {
      return true
    }
    return false
  }

  selectedToRadio(event, user) {
    if (event.target.checked) {
      if (this.isInCcList(user)) {
        this.selectedCcList.splice(this.selectedCcList.indexOf(user.employeeLogin), 1)
      }
      if (this.selectedToList.indexOf(user.employeeLogin) == -1) {
        this.selectedToList.push(user.employeeLogin)
      }
    } else {
      this.selectedToList.splice(this.selectedToList.indexOf(user.employeeLogin), 1)
    }
  }

  selectedCcRadio(event, user) {
    if (event.target.checked) {
      if (this.isInToList(user)) {
        this.selectedToList.splice(this.selectedToList.indexOf(user.employeeLogin), 1)
      }
      if (this.selectedCcList.indexOf(user.employeeLogin) == -1) {
        this.selectedCcList.push(user.employeeLogin)
      }
    } else {
      this.selectedCcList.splice(this.selectedCcList.indexOf(user.employeeLogin), 1)
    }
  }

  selectedCheckbox(event, user) {
    if (event.target.checked && this.selectedUsers.indexOf(user.employeeLogin) == -1) {
      this.selectedUsers.push(user.employeeLogin)
    } else if (!event.target.checked) {
      this.selectedUsers.splice(this.selectedUsers.indexOf(user.employeeLogin), 1)
    }
  }

  isInToList(user) {
    if (this.selectedToList.indexOf(user.employeeLogin) >= 0) {
      return true
    }
    return false
  }

  isInCcList(user) {
    if (this.selectedCcList.indexOf(user.employeeLogin) >= 0) {
      return true
    }
    return false
  }


  loadUsersForDirectorate(directorate: Directorate) {
    this.recipientsService.getUsersForDirectorate(directorate).subscribe();
  }

  loadDepartmentsForDirectorate(directorate: Directorate) {
    this.recipientsService.getDepartmentsForDirectorate(directorate).subscribe();
  }

  loadUsersForDepartment(department: Department) {
    this.recipientsService.getUsersForDepartment(department).subscribe();
  }

  loadDivisionsForDepartment(department: Department) {
    this.recipientsService.getDivisionsForDepartment(department).subscribe();
  }

  loadUsersForDivision(division: Division) {
     this.recipientsService.getUsersForDivision(division).subscribe();
  }

  loadMyDivisionUser(){
    this.recipientsService.getUsersForMyDivision(this.current_user.employeeDivision).subscribe();  
  }
}

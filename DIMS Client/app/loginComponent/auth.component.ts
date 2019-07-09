import {Component} from '@angular/core'
import {User} from '../model/user.model'
import {EmployeeDetails} from '../model/employeeDetails.model'
import {UserService} from '../services/user.service'
import {WorkflowService} from './../services/workflowServices/workflow.service'
import {Router} from '@angular/router'
import {SettingsDelegateUserService} from './../services/settingsDelegateUser.service'
import {ModuleSelection} from '../SharedComponents/module_selection.component'


@Component({
  selector:'my-login',
  providers: [UserService,SettingsDelegateUserService,ModuleSelection],
  templateUrl: 'app/loginComponent/authlogin.component.html'

}) 

export class AuthComponent{ 

  user= new User();
  private authenticating_user: EmployeeDetails;
  public errorMsg=''
  private isSecretary : boolean = false;

  constructor(private _router: Router, private userService: UserService, private workflowService: WorkflowService,
     private settingsDelegateUserService:SettingsDelegateUserService,private moduleSelection: ModuleSelection){
      // this.login(); // Remove comment on this line for KNPC  build
       localStorage.removeItem("isDelegate");
       	if(localStorage.getItem('searchFilter'))
		      localStorage.removeItem('searchFilter');
  }
   
  grantAccess(data,user){
    console.log("Data USER :: ",data);
    this.authenticating_user = data;
    if ((this.authenticating_user.employeeLogin != null)){
      localStorage.setItem("isCurrentUserHaveInbox", this.authenticating_user.isHaveInbox+"");
      localStorage.setItem("isCurrentUserHaveReports", this.authenticating_user.isHaveReports+"");
      localStorage.setItem("CurrentUser", JSON.stringify(this.authenticating_user));
       localStorage.setItem("isDelegate", "false")
      this.userService.setDelegatedisHaveAccessReports(this.authenticating_user.isHaveReports);
      this._router.navigate(['work-flow/inbox']);
      console.log("------------  :: ",this.authenticating_user.isHaveReports);
      if(this.authenticating_user.isHaveReports == 1){
        console.log("# IN IF")
        this.moduleSelection.flag= true;
      }else{
        console.log("# ELSE ")
          this.moduleSelection.flag= false;
      }
      }
    else{
      this.errorMsg = "Invalid Username/Password"
    }
  }

  login() {
    localStorage.removeItem("isDelegate");
    this.userService.getEmployeeDetails(this.user.username,this.user.password).subscribe((data) => this.grantAccess(data,this.user));
    }

}

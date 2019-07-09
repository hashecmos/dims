import {Component} from '@angular/core'
import {Router} from '@angular/router'
import {UserService} from '../services/user.service'

@Component({
  selector: 'module-selection',
  templateUrl: 'app/SharedComponents/module_selection.component.html'
})

export class ModuleSelection {
  name: string;
  private current_user: any;
  private delegatedJobTitle : any;
  public flag : boolean;
  constructor(public _router: Router,private userService: UserService) {
    if(this.userService.getCurrentUser()){
      this.current_user = this.userService.getCurrentUser();
      console.log("IN MODULE SELECTION :::  ",this.current_user);
      //var isHaveReportsAccess = this.userService.getDelegatedisHaveAccessReports();
      //console.log("isHaveReportsAccess ::: ",isHaveReportsAccess)
      if(this.current_user.isHaveReports == "1"){
        this.flag = true;
      }else{
        this.flag = false;
      }
      /*if(this.current_user.employeeJobTitle =='DCEO' || this.current_user.employeeJobTitle =='MGR' || this.current_user.employeeJobTitle =='TL'){
          this.flag = true;
      }else{
          this.flag = false;
      }*/
    }
    if (this._router.url.indexOf('browse') !== -1) {
      this.name = 'Browse'
    }
    else if (this._router.url.indexOf('pending-work-flow') !== -1 || this._router.url.indexOf('report-list') !== -1 || this._router.url.indexOf('document-scanned') !== -1) {
      this.name = 'Report'
    }
    else if (this._router.url.indexOf('work-flow') !== -1) {
      this.name = 'WorkFlow'
    }
  }
    
  update(){
      
      //this.flag = false;
      if(this.userService.getDelegatedJobTitle()){
        this.delegatedJobTitle  = this.userService.getDelegatedJobTitle();
        console.log("update method ::: ",this.delegatedJobTitle);
        /*if(this.delegatedJobTitle != null && this.delegatedJobTitle != undefined){
            if(this.delegatedJobTitle == 'DCEO' || this.delegatedJobTitle == 'MGR'|| this.delegatedJobTitle == 'TL' || this.delegatedJobTitle == 'SEC' || this.delegatedJobTitle == 'CRDEP'){
              this.flag = true;
          }else{
              this.flag = false;
          }
        }*/

        var isHaveReportsAccess = this.userService.getDelegatedisHaveAccessReports();
        console.log("isHaveReportsAccess ::: ",isHaveReportsAccess)
        if(isHaveReportsAccess == "1"){
          this.flag = true;
        }else{
          this.flag = false;
        }


      } 
  }
}

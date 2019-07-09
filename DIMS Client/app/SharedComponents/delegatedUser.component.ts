import {Component} from '@angular/core'
import {UserService} from './../services/user.service'
import {SettingsDelegateUserService} from './../services/settingsDelegateUser.service'
import {WorkflowService} from './../services/workflowServices/workflow.service'
import {Router} from '@angular/router'
import {ModuleSelection} from './module_selection.component'

import global = require('./../../global.variables')

@Component({
  selector: 'delegate-user',
  providers: [SettingsDelegateUserService,ModuleSelection],
  templateUrl: 'app/SharedComponents/delegatedUser.component.html'

})

export class DelegateUser {
  public delegatedUsers: any[];
  public delegateForEmployeeLogin: any;
  public current_user_name: string;
  private isSecretary : boolean = false;
  public showUser : boolean = true;

  constructor(private _router: Router,private workflowService: WorkflowService,
    private userService: UserService, private settingsDelegateUserService: SettingsDelegateUserService,
    private moduleSelection: ModuleSelection) {
    this.settingsDelegateUserService.getDelegateUsers().subscribe((data) => this.setDelegateUser(data));
    this.userService.notifyDelegateForChange.subscribe(data => this.setDelegate(data));
    this.current_user_name = this.userService.getCurrentUser().employeeName;
    if(JSON.parse(localStorage.getItem("delegateForEmployeeLogin")) != null){
        this.delegateForUser(JSON.parse(localStorage.getItem("delegateForEmployeeLogin")));
    }
  }

  delegateForUser(employeeLogin: any) {
     // var a = '1';
    //localStorage.setItem("sentItemCurrentPage",a);
    //localStorage.setItem("inboxItemCurrentPage",a);
    //localStorage.setItem("archiveItemCurrentPage",a);
    if(this._router.url == '/work-flow/inbox'){
        this._router.navigateByUrl( '/work-flow/reload' );
        let self: any = this;
        setTimeout( function() {  
            self._router.navigateByUrl( '/work-flow/inbox' );
            }, 20 );
    }else{
        this._router.navigateByUrl( '/work-flow/inbox' );
    }
      //console.log("Delegating User SETTINGS  :: ",employeeLogin);
      this.userService.setDelegatedJobTitle(employeeLogin.EmpJobTitle);
      
      if(employeeLogin.EmpIsHaveReports == "1" && employeeLogin.DelegateduserHaveAccessForReports == "1"){
        //console.log("INER IFFFFF ****************")
        //this.moduleSelection.flag= true;
        this.userService.setDelegatedisHaveAccessReports("1");
    }else{
        //console.log("INNER ELSEEEEEE ***************");
        //this.moduleSelection.flag= false;
        this.userService.setDelegatedisHaveAccessReports("0");
    }
    
      
     /* if(employeeLogin.EmpIsHaveReports){
        //console.log("INER IFFFFF")
        this.moduleSelection.flag= true;
    }else{ 
        //console.log("INNER ELSEEEEEE");
        this.moduleSelection.flag= false;
    }*/
    this.workflowService.isDelegatedUserSecretary(employeeLogin.userLogin).subscribe(data => this.setIsSecretary(data,employeeLogin)); 
    
    
  }
  setIsSecretary(data: any,employeeLogin){
      if(data){
          this.isSecretary = true;
      }
      
      if(!this.isSecretary){
       this.userService.getEmployeeDetails(employeeLogin.userLogin,"").subscribe((data) => this.storeDelegateUserDetails(data,employeeLogin))   
      }else{
          this.storeDelegateUserDetails("SEC",employeeLogin)
      }
  } 
  storeDelegateUserDetails(data,empLogin){
      if(data == 'SEC'){
          this.userService.setDelegatedJobTitle("SEC");
      }else{
          this.userService.setDelegatedJobTitle(data.employeeJobTitle);
      }
      
      this.userService.delegateForUser(JSON.stringify(empLogin));
  }
  storeSectDetails(data,empLogin){
      this.userService.setDelegatedJobTitle(data.employeeJobTitle);
      this.userService.delegateForUser(JSON.stringify(empLogin));
  }
  unDelegateForUser() {
    //var a = '1';
    //localStorage.setItem("sentItemCurrentPage",a);
    //localStorage.setItem("inboxItemCurrentPage",a);
    //localStorage.setItem("archiveItemCurrentPage",a);
    
        if(this._router.url == '/work-flow/inbox'){
            this._router.navigateByUrl( '/work-flow/reload' );
            let self: any = this;
            setTimeout( function() {  
                self._router.navigateByUrl( '/work-flow/inbox' );
                }, 20 );
        }else{
            this._router.navigateByUrl( '/work-flow/inbox' );
        }
    this.userService.setDelegatedJobTitle(this.userService.getCurrentUser().employeeJobTitle);
    //console.log("this.userService.getCurrentUser() ::: ",this.userService.getCurrentUser());
    //console.log("Reports :: Acces of current user :; ",this.userService.getCurrentUser().isHaveReports)
    this.userService.setDelegatedisHaveAccessReports(this.userService.getCurrentUser().isHaveReports);
    this.userService.unDelegateForUser();
  }
  
  setDelegateUser(data){
      //console.log("Swithching USER ::: ",data);
      var isHaveInbox = localStorage.getItem("isCurrentUserHaveInbox");
      var isHaveReports = localStorage.getItem("isCurrentUserHaveReports");
     let delegateUserData = [];
      for (var i = 0; i < data.length; i++) {
          var folderDetailArray = data[i].split("!~");
          var folderDetail = {
              "userLogin": folderDetailArray[0],
              "userFullName":folderDetailArray[1],
              "EmpJobTitle":folderDetailArray[2].trim(),
              "EmpIsHaveReports":folderDetailArray[3]+"".trim(),
              "DelegateduserHaveAccessForReports":folderDetailArray[4]+"".trim(),
          }
          delegateUserData[i] = folderDetail;
      } 
      //console.log("delegateUserData  ::: $$$$$$$$$$  ::: ",delegateUserData)
        this.delegatedUsers = delegateUserData;  
        if(isHaveInbox != "1"){
            if( this.delegatedUsers && this.delegatedUsers.length>0 ){ 
                this.showUser = false;
                /*if(this.delegatedUsers[0].EmpJobTitle =='DCEO' || this.delegatedUsers[0].EmpJobTitle =='MGR' || this.delegatedUsers[0].EmpJobTitle =='TL'){
                    this.moduleSelection.flag= true;
                }else{
                    this.moduleSelection.flag= false;
                }*/
                //console.log("DELEGATE USER ::: ",this.delegatedUsers[0]);
                this.userService.setDelegatedJobTitle(this.delegatedUsers[0].EmpJobTitle);
                
                this.userService.delegateForUser(JSON.stringify(this.delegatedUsers[0]));
                this._router.navigate(['work-flow/inbox']);
            }else{
               this._router.navigate(['access-denied']);
            }
        }

        if(isHaveReports != "1"){
            //console.log("in IFFFFFFFFF")
            if( this.delegatedUsers && this.delegatedUsers.length>0 ){ 
            this.userService.setDelegatedisHaveAccessReports(this.delegatedUsers[0].EmpIsHaveReports);
            if(this.delegatedUsers[0].EmpIsHaveReports == "1" && this.delegatedUsers[0].DelegateduserHaveAccessForReports == "1"){
                //console.log("INER IFFFFF")
                this.moduleSelection.flag= true;
            }else{
                this.userService.setDelegatedisHaveAccessReports(this.delegatedUsers[0].DelegateduserHaveAccessForReports);
                //console.log("INNER ELSEEEEEE");
                this.moduleSelection.flag= false;
            }
        }
            
        }else{
            if( this.delegatedUsers && this.delegatedUsers.length>0 ){ 
            if(!(this.delegatedUsers[0].EmpIsHaveReports == "1" && this.delegatedUsers[0].DelegateduserHaveAccessForReports == "1")){
                this.userService.setDelegatedisHaveAccessReports(this.delegatedUsers[0].DelegateduserHaveAccessForReports);
            }
            else{
                this.moduleSelection.flag= true;
            }
        }
        }


        
  }
   

    setDelegate(data){
        var json = data;
        if(json =="")
        {
            localStorage.removeItem("isDelegate")
            localStorage.removeItem("selectedDelegateUser")
            this.delegateForEmployeeLogin = "";
        }else{
            this.delegateForEmployeeLogin = JSON.parse(json);
            localStorage.setItem("selectedDelegateUser", JSON.stringify(json))
            localStorage.setItem("isDelegate", "true")
        }
    }
}

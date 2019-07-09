import {Component,Output, EventEmitter} from '@angular/core'
import {Router, ActivatedRoute } from '@angular/router'
import {ColumnPreference} from './../../model/columnPreference.model'
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {BrowseSharedService} from '../../services/browseEvents.shared.service'
import {UserService} from '../../services/user.service'
import global = require('./../../global.variables')
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'settings-btn',
  providers: [WorkflowService],
  templateUrl: 'app/SharedComponents/settings/settingsButton.component.html'
})

export class SettingsButton{

    
   private columnPreference: any[];
   private preference :any ;
   private folderFilterSubscription;
   private isSecretary : boolean = false;
    
   private activeTab:string;
   private current_user: any;
   private currentDelegateForEmployeeLogin: string = "";
   private snapshot : any;
   private siteManagerData : any
   private selectedSite :any
   private pref :any;
   private paramsSubscription: any;

   private delegatedorcurrentUser : any ;
   private colPrefResults : any;
    private subscription: ISubscription[] = [];
    private checkIsSEC : boolean = false;

   constructor(private router: Router, private route: ActivatedRoute, private workflowService: WorkflowService, private userService: UserService,private sharedService: BrowseSharedService) {
    this.snapshot = route.snapshot;

    this.current_user = this.userService.getCurrentUser();
    if(this.current_user.employeeJobTitle && this.current_user.employeeJobTitle == 'SEC')
        this.checkIsSEC = true;
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();
    if (this.currentDelegateForEmployeeLogin == "") {
      this.currentDelegateForEmployeeLogin = this.current_user.employeeLogin;
    }
      this.delegatedorcurrentUser =  this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
      //console.log("current_user  ::: ",this.current_user)
      //console.log("#########  delegatedorcurrentUser ##############  delegatedorcurrentUser :: ",this.delegatedorcurrentUser)
     /* this.subscription.push(this.userService.getColumnPreference(this.delegatedorcurrentUser).subscribe((data) => this.storeGlobally(data)));
      this.subscription.push(this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.setData(data,false,null)));
      this.subscription.push(this.workflowService.isSecretary().subscribe(data => this.setIsSecretary(data))); */
   }

   ngOnInit() {
      /* this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.setData(data,false));
    var context: any = this;
    this.paramsSubscription = this.route.queryParams.subscribe(
      data => {
        context.refresh(); 
      }) */
     // this.subscription.push(this.userService.getColumnPreference(this.delegatedorcurrentUser).subscribe((data) => this.storeGlobally(data)));
      //this.subscription.push(this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.setData(data,false,null)));
      setTimeout(() => {
        //console.log("YYYYYYYY   EEEEEEEEEEE   SSSSSSSSSSS:: ",this.userService.getCurrentDelegatedUserOrCurrentUserLogin());
        this.delegatedorcurrentUser = this.userService.getCurrentDelegatedUserOrCurrentUserLogin()
        
        this.workflowService.isDelegatedUserSecretary(this.delegatedorcurrentUser).subscribe(data => this.setIsSecretary(data)); 
    }, 1000);
      
      
      //this.subscription.push(this.workflowService.isSecretary().subscribe(data => this.setIsSecretary(data)));

     
  }

  
  assignUserName(data){
    //console.log("DATATATATTATTATATATTATTA ::: ",data)
    this.current_user = data;
}
    
    ngOnDestroy() {
        for(let subs of this.subscription) {
        subs.unsubscribe();
        }
        this.subscription = null;
    }

    setIsSecretary(data: any){
        //console.log("setIsSecritory 111111111111111BEFORE () :::  ",data);
        //console.log("this.current_user.1111111111111employeeLogin  :: ",this.current_user.employeeLogin)
        //console.log("this.delegatedorcurrentUser111111111111 :: ",this.delegatedorcurrentUser)
        setTimeout(() => {
            //console.log("setIsSecritory BEFORE () :::  ",data);
            //console.log("this.current_user.employeeLogin  :: ",this.current_user.employeeLogin)
            //console.log("this.delegatedorcurrentUser :: ",this.delegatedorcurrentUser)
        if(this.current_user.employeeLogin != this.delegatedorcurrentUser){
            if(this.checkIsSEC){
                if(data){
                    //console.log("setIsSecritory () :::  ",data);
                    this.isSecretary = true;
                }
            }
          }
          this.userService.getUserDetails(this.delegatedorcurrentUser).subscribe(data => this.assignUserName(data));
        }, 1000);
      
  }
   storeGlobally(data :any){
       this.colPrefResults = data;
       this.pref = data;
       for (var i = 0; i < this.pref.length; i++) { 
             if(document.getElementById(this.pref[i].itemType)!=null){
                 ////console.log("document.getElementById(this.pref[i].itemType) ::: ",document.getElementById(this.pref[i].itemType));
                 if(this.pref[i].coloumnEnabled=="true"){
                    document.getElementById(this.pref[i].itemType).checked = true; 
                 }
                 if(this.pref[i].coloumnEnabled=="false"){
                    document.getElementById(this.pref[i].itemType).checked = false; 
                 }
             }
       }
     
      global.col_pref = data;
    } 
   openColumnPrefsModal(ColumnPrefsModal){
        this.subscription.push(this.userService.getColumnPreference(this.delegatedorcurrentUser).subscribe((data) => this.storeGlobally(data)));
       ColumnPrefsModal.open();
   }
    
   openSiteManagerModal(siteManagerModal : any){
       //console.log("current_user  :: ",this.current_user)
       //console.log("current_user  :: ",this.current_user.employeeDepartment)
       //console.log("current_user  :: ",this.current_user.employeeDepartment.departmentCode)
       this.subscription.push(this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.setData(data,false,null)));
      siteManagerModal.open();
   }
   
   getSiteManagerDetails(){
    //console.log("current_user  :: ",this.current_user)
    //console.log("current_user  :: ",this.current_user.employeeDepartment)
    //console.log("current_user  :: ",this.current_user.employeeDepartment.departmentCode)
       this.subscription.push(this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,"").subscribe((data) => this.setData(data,false,null)));
   }
   
   /*setData(data,flag,message){
       if(flag){
          document.getElementById("updateSite").value = "";
          document.getElementById('updateCheckbox').checked = false;
          document.getElementById("addSite").value = "";
          document.getElementById('addCheckbox').checked =false;
          document.getElementById("siteDesc").value="";
      }   
       
       if(document.getElementById("sucessMsg")!= undefined && flag && message != null){
          
         document.getElementById("sucessMsg").innerHTML = "<span><b style='color : #237723;'> Site "+message+" sucessfully<b></span>";
     
          setTimeout(() => {
             document.getElementById("sucessMsg").innerHTML='';
            }, 3000);
       }
      
      this.siteManagerData = data;       
   }*/
   setData(data,flag,message){
    if(flag){
       document.getElementById("updateSite").value = "";
       document.getElementById('updateCheckbox').checked = false;
       document.getElementById("addSite").value = "";
       document.getElementById('addCheckbox').checked =false;
       document.getElementById("siteDesc").value="";
   }   
   if(data[0].isExisted != "EXIST" && document.getElementById("sucessMsg")!= undefined && flag && message != null){
       
      document.getElementById("sucessMsg").innerHTML = "<span><b style='color : #237723;'> Site "+message+" successfully<b></span>";
  this.siteManagerData = data; 
      // setTimeout(() => {
        //  document.getElementById("sucessMsg").innerHTML='';
        // }, 3000);
    }
    else if(data[0].isExisted == undefined ) {
        //console.log("first if")
        this.siteManagerData = data; 
        // document.getElementById("sucessMsg").innerHTML = "<span><b style='color : #237723;'><b></span>";
  
       // setTimeout(() => {
        //  document.getElementById("sucessMsg").innerHTML='';
        // }, 3000);
     
    }
        else if(data[0].isExisted != undefined ) {
        
         document.getElementById("sucessMsg").innerHTML = "<span><b style='color : #237723;'> Duplicate Site Name. Please try again!<b></span>";
       // setTimeout(() => {
        //  document.getElementById("sucessMsg").innerHTML='';
        // }, 3000);
     
    }
    else if(data[0].isExisted == "SEARCH" ) {
        
         document.getElementById("sucessMsg").innerHTML = "<span><b style='color : #237723;'> No Sites Found<b></span>";
  
       // setTimeout(() => {
        //  document.getElementById("sucessMsg").innerHTML='';
        // }, 3000);SEARCH
     
    }
   
   //this.siteManagerData = data;       
}
    
   SelectedRadio(e: any, siteManager: any){
       this.selectedSite = siteManager;
       document.getElementById("updateSite").value = siteManager.siteName;
       if(siteManager.siteType == 'External'){
          document.getElementById('updateCheckbox').checked = true;
       }else{
         document.getElementById('updateCheckbox').checked = false;
       }
   }
    
   serachSite(){
    //console.log("current_user  :: ",this.current_user)
    //console.log("current_user  :: ",this.current_user.employeeDepartment)
    //console.log("current_user  :: ",this.current_user.employeeLogin)
       var siteDesc = document.getElementById("siteDesc").value;
       this.subscription.push(this.workflowService.getSiteItems(this.current_user.employeeDepartment.departmentCode,encodeURIComponent(siteDesc)).subscribe((data) => this.setData(data,true,null)));
   }
   
   updateSite(){
    //console.log("current_user  :: ",this.current_user)
    //console.log("current_user  :: ",this.current_user.employeeDepartment)
    //console.log("current_user  :: ",this.current_user.employeeLogin)
       var siteDesc = document.getElementById("updateSite").value;
       var isChecked = document.getElementById('updateCheckbox').checked;
       var siteType="";
       if(isChecked){
           siteType = "External"
       }else{
           siteType = "Internal"
       }
       this.subscription.push(this.workflowService.updateSiteItems(this.selectedSite.siteId,siteType,encodeURIComponent(siteDesc),this.current_user.employeeDepartment.departmentCode).subscribe((data) => this.setData(data,true,'Updated')));
   }
    
    addSite(){
        //console.log("current_user  :: ",this.current_user)
        //console.log("current_user  :: ",this.current_user.employeeDepartment)
        //console.log("current_user  :: ",this.current_user.employeeLogin)
       var siteDesc = document.getElementById("addSite").value;
       var isChecked = document.getElementById('addCheckbox').checked;
       var siteType="";
       if(isChecked){
           siteType = "External"
       }else{
           siteType = "Internal"
       }
       this.subscription.push(this.workflowService.addSiteItems(siteType,encodeURIComponent(siteDesc),this.current_user.employeeDepartment.departmentCode).subscribe((data) => this.setData(data,true,'Added')));

    }
    
    clearAndClose(siteManagerModal : any){
        
        document.getElementById("updateSite").value = "";
          document.getElementById('updateCheckbox').checked = false;
          document.getElementById("addSite").value = "";
          document.getElementById('addCheckbox').checked =false;
          document.getElementById("siteDesc").value="";
        siteManagerModal.close();
        document.getElementById("sucessMsg").innerHTML = "<span><b style='color : #237723;'></b></span>";
        }
   
    actionOnOpen(){
        
        this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((data) => this.storeGlobally(data)));
    }
    
    
    updateColumnPrefs(ColumnPrefsModal,$event)
    {    
        this.columnPreference = [];
        this.pref;
        for (let inputField of [].slice.call($event.target.getElementsByTagName('input'))) {
          if (inputField.getAttribute('type') == "checkbox") {
              var inboxField = inputField.id;
              if(inboxField.indexOf('inbox') >= 0){
                  this.preference = new ColumnPreference();
                  this.preference.itemType=inputField.id;
                  this.preference.columnName=inputField.value;
                  this.preference.coloumnEnabled=inputField.checked;
                  
                  this.columnPreference.push(this.preference);
                 // //console.log(`Inbox ----- ${inputField.id} Checkbox vaule ${inputField.value} was ${inputField.checked ? '' : 'un'}checked\n`);
              }
            }  
            
            if (inputField.getAttribute('type') == "checkbox") {
              var sentField = inputField.id;
              if(sentField.indexOf('sent') >= 0){
                  
                  this.preference = new ColumnPreference();
                  this.preference.itemType=inputField.id;
                  this.preference.columnName=inputField.value;
                  this.preference.coloumnEnabled=inputField.checked;
                  
                  this.columnPreference.push(this.preference);
              }
            } 
            
            if (inputField.getAttribute('type') == "checkbox") {
              var archiveField = inputField.id;
              if(archiveField.indexOf('archive') >= 0){
                  
                  this.preference = new ColumnPreference();
                  this.preference.itemType=inputField.id;
                  this.preference.columnName=inputField.value;
                  this.preference.coloumnEnabled=inputField.checked;
                  
                  this.columnPreference.push(this.preference);
              }
            } 

            if (inputField.getAttribute('type') == "checkbox") {
                var browseField = inputField.id;
                if(browseField.indexOf('browse') >= 0){
                    
                    this.preference = new ColumnPreference();
                    this.preference.itemType=inputField.id;
                    this.preference.columnName=inputField.value;
                    this.preference.coloumnEnabled=inputField.checked;
                    
                    this.columnPreference.push(this.preference);
                }
              } 

              if (inputField.getAttribute('type') == "checkbox") {
                var dailyField = inputField.id;
                if(dailyField.indexOf('daily') >= 0){
                    
                    this.preference = new ColumnPreference();
                    this.preference.itemType=inputField.id;
                    this.preference.columnName=inputField.value;
                    this.preference.coloumnEnabled=inputField.checked;
                    
                    this.columnPreference.push(this.preference);
                }
              } 
        }
        //console.log("this.columnPreference  :: ",this.columnPreference)
        this.subscription.push(this.workflowService.addColumnPreference(this.columnPreference, this.current_user.employeeLogin,"workflow").subscribe(data => this.preferenceCompletionHandler(data,ColumnPrefsModal), error => this.preferenceCompletionErrorHandler(error)));
          
    }
    
     preferenceCompletionHandler(response :any,ColumnPrefsModal : any){
         ColumnPrefsModal.close();
         //console.log("this.snapshot.routeConfig.path  ::: ",this.snapshot.routeConfig.path);
        switch (this.snapshot.routeConfig.path) {
             case "inbox":
                this.router.navigate(['work-flow/inbox'], { queryParams: { random: new Date().getTime() } });
             break;
            case "sent-item":
                this.router.navigate(['work-flow/sent-item'],{ queryParams: { random: new Date().getTime() } });
            break;
            case "archive":
                this.router.navigate(['work-flow/archive'],{ queryParams: { random: new Date().getTime() } });
            break;
            case "dailydocument":
                this.router.navigate(['work-flow/dailydocument'],{ queryParams: { random: new Date().getTime() } });
            break;
            
            
        }
    }
    
    preferenceCompletionErrorHandler(response :any){
        
    }
}

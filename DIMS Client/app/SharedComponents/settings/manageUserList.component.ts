import {ViewChild,Component,Output, EventEmitter} from '@angular/core'
import {BrowseService} from './../../services/browse.service'
import {WorkflowService} from './../../services/workflowServices/workflow.service'
import {UserList} from './../../model/userList.model'
import {UserService} from './../../services/user.service'
import {BrowseSharedService} from './../../services/browseEvents.shared.service'
import {RecipientsService} from './../../services/recipients.service'

import global = require('./../../global.variables')

@Component({
  providers: [BrowseService,RecipientsService],
  selector: 'manage-user-list',
  templateUrl: 'app/SharedComponents/settings/manageUserList.component.html'
})

export class ManageUserList{
  @ViewChild('divisionsThing') divisionsThing
	@ViewChild('departmentThing') departmentThing
	@ViewChild('crossDptThing') crossDptThing
	@ViewChild('hierarchyThing') hierarchyThing
  private UserList: UserList
  private displayUserList
  private selectedUsers
  private selectedList
  private activeTab
  private currentAction
  private user
  private currentUserName
  private userSearchUrl
  public searchCrtieria : string = "";
  private currentUser
  private divisions : any = [];
  private departments : any = [];
  private currentDelegateForEmployeeLogin: string = "";
  wradio_to: any = [];
  wradio_cc: any = [];
  wradio_none: any = [];
  private knpcHierarchy : any = [];
  private crossDepartment : any =[];
  public globalSearchCrtieria: any;
  public isGlobalKNPCSearch: Boolean= false;
  private empJobTitle;
    
  constructor(private workflowService: WorkflowService, private userService:UserService,
    private sharedService:BrowseSharedService,private recipientsService: RecipientsService){
    this.displayUserList = []
    this.selectedUsers = []
    this.UserList = new UserList();
    this.UserList.listName = '';
    this.userSearchUrl = global.base_url + "/EmployeeService/getEmailIds?email=:keyword";
    this.currentUserName = this.userService.getCurrentUser().employeeLogin;
    this.currentUser = this.userService.getCurrentUser() 
      
    this.currentDelegateForEmployeeLogin = this.userService.getCurrentDelegatedForEmployeeLogin();

     if (this.currentDelegateForEmployeeLogin == "") {
       this.currentDelegateForEmployeeLogin = this.currentUser.employeeLogin;
       this.recipientsService.getDivision(this.currentUser.employeeDivision.empDivisionCode,this.currentUser.employeeLogin).subscribe(data => this.divisions = data);
       this.recipientsService.getUserDepartment(this.currentUser.employeeDepartment.departmentCode,this.currentUser.employeeLogin).subscribe(data => this.departments = data);
       if(this.userService.getDelegatedJobTitle() == ""){
            this.empJobTitle = this.userService.getDelegatedJobTitle();
        }else{
            this.empJobTitle = this.currentUser.employeeJobTitle;
        }
     }else{
       if(this.currentDelegateForEmployeeLogin)
       {
        this.userService.getUserDetails(JSON.parse(this.currentDelegateForEmployeeLogin).userLogin).subscribe(data => this.assignUserName(data));
       }
           
     }
    
  }

  assignUserName(data){
    this.currentUser = data;
    this.recipientsService.getDivision(this.currentUser.employeeDivision.empDivisionCode,this.currentUser.employeeLogin).subscribe(data => this.divisions = data);
    this.recipientsService.getUserDepartment(this.currentUser.employeeDepartment.departmentCode,this.currentUser.employeeLogin).subscribe(data => this.departments = data);
    if(this.userService.getDelegatedJobTitle() == ""){
      this.empJobTitle = this.userService.getDelegatedJobTitle();
      }else{
          this.empJobTitle = this.currentUser.employeeJobTitle;
      }
}


  

  ngOnInit(){
    this.getUserList()
    this.sharedService.emitSelectedUsers$.subscribe(users => this.selectedUsers = users)
  }

  moveDown(list, index){
    var updatedableItems = [];
    updatedableItems.push(this.displayUserList[index]);
    updatedableItems.push(this.displayUserList[index+1]);
    this.workflowService.modifyUserListPriority(updatedableItems).subscribe(data => this.getUpdatedListAndRefresh());
  }

  moveUp(list, index){
    var updatedableItems = [];
    updatedableItems.push(this.displayUserList[index]);
    updatedableItems.push(this.displayUserList[index-1]);
    this.workflowService.modifyUserListPriority(updatedableItems).subscribe(data => this.getUpdatedListAndRefresh());
  }


  getUpdatedListAndRefresh(){
    this.getUserList();
  }

  openModalForCreate(UserListModal){
    console.log("divisions :: ",this.divisions)
    console.log("divisions :: ",this.divisions[0])
    this.recipientsService.getUsersForDivision(this.divisions[0],this.currentUser.employeeLogin,"").subscribe();
    this.currentAction = "Create";
    setTimeout(() => {
      window.document.getElementById("selectedUsers").disabled = false;
      if(this.divisionsThing)
      this.divisionsThing.isOpened = true
    if(this.departmentThing)
      this.departmentThing.isOpened = false
    if(this.crossDptThing)
      this.crossDptThing.isOpened = false
    if(this.hierarchyThing)
    this.hierarchyThing.isOpened = false

			/*this.divisionsThing.isOpened = true
			this.departmentThing.isOpened = false
			this.crossDptThing.isOpened = false
			this.hierarchyThing.isOpened = false*/
		}, 100);
    UserListModal.open()
  }

  removeFromList($event:any,user:any){
    //console.log(document.getElementsByName(user)[2]);
    console.log("user.employeeLogin :: "+user);
    // if(document.getElementsByName(user.employeeLogin+"none")[2] !=null){
    //     document.getElementsByName(user.employeeLogin+"none")[2].checked = true
    // }

    this.selectedUsers.splice(this.selectedUsers.indexOf(user), 1);
    //this.wradio_cc.splice(this.wradio_cc.indexOf(user), 1)


    // if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
    //   this.wradio_none.push(user.employeeLogin);
    // }
    this.checkedAllWithSameClassName(document.getElementsByClassName(user+"none"));
    
    $event.preventDefault();
    $event.stopPropagation();
	  return false;
  }

  addUserToList(data){
    if(data != null && data.employeeLogin){
      if(this.selectedUsers.indexOf(data.employeeLogin)== -1){  
      this.selectedUsers.push(data.employeeLogin)
      this.user = ""
      }
    }
  }

  /*createOrModifyList(createUserListModal: any){
    this.UserList.listMember = this.selectedUsers
    this.UserList.loginUser = this.currentUserName
    this.UserList.listType = 'private'
    if(this.UserList.listName && this.UserList.listMember.length >= 1){
      this.UserList.listName = this.UserList.listName.trim();
      if(this.currentAction=="Create"){
        this.workflowService.createUserList(this.UserList).subscribe(data => this.closeAndRefresh(createUserListModal))   
      }else{
        this.UserList.listId = this.selectedList.listId      
        this.workflowService.modifyUserList(this.UserList).subscribe(data => this.closeAndRefresh(createUserListModal))   
      }
    }
  }*/
  public isListExisted: boolean = false;
  createOrModifyList(createUserListModal: any){
    this.isListExisted=false;
    this.UserList.listMember = this.selectedUsers
    this.UserList.loginUser = this.currentUserName
    this.UserList.listType = 'private'
    if(this.UserList.listName && this.UserList.listMember.length >= 1){
      this.UserList.listName = this.UserList.listName.trim();
      if(this.currentAction=="Create"){
        for(var index = 0; index < this.displayUserList.length; index++) {
          if(this.UserList.listName.toLowerCase() == this.displayUserList[index].listName.toLowerCase()) {
              this.isListExisted = true;
          }
      }
        if(!this.isListExisted) {
            this.workflowService.createUserList(this.UserList).subscribe(data => this.closeAndRefresh(createUserListModal))   
        }
        else {
            console.log('Same list name existed, please try again...!');
        }
        
      }else{
        this.UserList.listId = this.selectedList.listId      
        this.workflowService.modifyUserList(this.UserList).subscribe(data => this.closeAndRefresh(createUserListModal))   
      }
    }
  }

  closeAndRefresh(Modal){
    this.refreshDisplayUserList();
    this.resetFields();
    Modal.close()
  }

  resetFields(){
    this.UserList = new UserList()
    this.selectedUsers = []
    for (let inputField of [].slice.call(document.getElementsByTagName('input'))) {
          if (inputField.getAttribute('type') == "radio") {
                inputField.checked = false;
              }
    }
    this.isListExisted=false;
  }

  getUserList(){
    this.workflowService.getUserlist().subscribe(data => this.displayUserList = data)
  }


	selectAll(userList : any, recpType : any, suffix : any){
    for (var i = 0; i < userList.length; i++) { 
      
      if(recpType=="cc"){
       this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"cc"));
        if (this.wradio_cc.indexOf(userList[i].employeeLogin) == -1) {
         this.wradio_cc.push(userList[i].employeeLogin);
        }
        if(this.wradio_to.includes(userList[i].employeeLogin)) {
          this.wradio_to.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
        }
        if(this.wradio_none.includes(userList[i].employeeLogin)) {
         this.wradio_none.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
       }


       
    if (this.selectedUsers.indexOf(userList[i].employeeLogin) == -1) {
      if (this.selectedUsers.indexOf(userList[i].employeeLogin) > -1) {
        this.selectedUsers.splice(this.selectedUsers.indexOf(userList[i].employeeLogin), 1)
      } 
      this.selectedUsers.push(userList[i].employeeLogin)
    }


      } else if(recpType=="none"){
       if (this.wradio_none.indexOf(userList[i].employeeLogin) == -1) {
         this.wradio_none.push(userList[i].employeeLogin);
       }
       this.checkedAllWithSameClassName(document.getElementsByClassName(userList[i].employeeLogin+"none"));
        if(this.wradio_to.includes(userList[i].employeeLogin)) {
          this.wradio_to.splice( this.wradio_to.indexOf(userList[i].employeeLogin), 1 );
        }
        if(this.wradio_cc.includes(userList[i].employeeLogin)) {
          this.wradio_cc.splice( this.wradio_cc.indexOf(userList[i].employeeLogin), 1 );
        }

        
    
      if (this.selectedUsers.indexOf(userList[i].employeeLogin) > -1) {
        this.selectedUsers.splice(this.selectedUsers.indexOf(userList[i].employeeLogin), 1)
      } 
    

      }
   }
 }


  openModalForEdit(list,UserListModal){
    this.selectedList = list
    this.currentAction = "Save";
    this.recipientsService.getUsersForDivision(this.divisions[0],this.currentUser.employeeLogin,"").subscribe();
    this.UserList.listName = list.listName
    this.workflowService.getUsersOfList(list.listId).subscribe(data => this.showUsersOfList(data)) 
    setTimeout(() => {
      window.document.getElementById("selectedUsers").disabled = false;
      if(this.divisionsThing)
        this.divisionsThing.isOpened = true
      if(this.departmentThing)
        this.departmentThing.isOpened = false
      if(this.crossDptThing)
        this.crossDptThing.isOpened = false
      if(this.hierarchyThing)
			this.hierarchyThing.isOpened = false
		}, 100);
    UserListModal.open()
  }

  showUsersOfList(UsersOfList){
    for(let user of UsersOfList){
      this.selectedUsers.push(user.employeeLogin)
    }
  }

  confirmAction(list, confirmDeleteModal){
    this.selectedList = list
    confirmDeleteModal.open()
  }

  deleteSelectedList(confirmDeleteModal){
    this.workflowService.deleteUserList(this.selectedList.listId).subscribe(data => {
      confirmDeleteModal.close()
      this.refreshDisplayUserList()
    })
  }

  refreshDisplayUserList(){
    this.getUserList()
    console.log("displayUserList  :: ",this.displayUserList);
  }

  userlistNameEmpty() {
    if(this.UserList && this.UserList.listName && this.UserList.listName.trim().length > 0)
      return false;
    else
      return true;
  }
    
//     getDivisionUserList(division: Division){
//        this.searchCrtieria = document.getElementById("searchCriteria").value;
//        //alert(this.searchCrtieria);
//        this.loadUsersForDivision(division,this.searchCrtieria);
//    }

//    getDepartmentUserList(department: Department){
//        this.searchCrtieria = document.getElementById("deptsearchCriteria").value;
//        this.loadUsersForDepartment(department,this.searchCrtieria);
//    }  
    getDivisionUsers(){
        this.loadUsersForDivision(this.divisions[0],'');
    }
    
    loadUsersForDivision(division: any,searchCrtieria : string) {
      console.log("test division ::; ",division)
        window.document.getElementById("selectedUsers").disabled = false;
        this.userSearchUrl = global.base_url + "/EmployeeService/getDivisionUsers?division_code="+division.divisionCode+"&user_login="+this.currentUser.employeeLogin+"&searchCrtieria=:keyword";
        this.recipientsService.getUsersForDivision(division,this.currentUser.employeeLogin,searchCrtieria).subscribe(_res =>{
          this.enableUserSelection(_res);
        });
        //added by Ravi Boni on 24-12-2017
        window.document.getElementById("searchCriteria").value = '';
        //end
    } 
    
    loadDivisionsForDepartment(department: any) {
        this.recipientsService.getDivisionsForDepartment(department).subscribe();
    }

    loadUsersForDepartment_5(department: any,searchCrtieria : string) {
      window.document.getElementById("selectedUsers").disabled = false;
     this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
      this.recipientsService.getUsersForDepartment_1(department,searchCrtieria).subscribe(_res =>{
        this.enableUserSelection(_res);
      });
      //added by Ravi Boni on 24-12-2017
      window.document.getElementById("deptsearchCriteria").value = '';
      //end
    }
    
    loadUsersForDepartment(department: any,searchCrtieria : string) {
        window.document.getElementById("selectedUsers").disabled = false;
        //this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
        if(this.isGlobalKNPCSearch) {
          //added by Ravi Boni 0n 13-12-2017
          //this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
        // this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
            this.recipientsService.getUsersForDepartment(department,this.globalSearchCrtieria).subscribe((data) => {
              this.enableUserSelection(data);
          });
        }
        else {
          //added by Ravi Boni 0n 13-12-2017
          //this.userSearchUrl = global.base_url + "/EmployeeService/getDepartmentUsers?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
          this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment?dept_code="+department.departmentCode+"&searchCrtieria=:keyword";
            this.recipientsService.getUsersForDepartment(department,searchCrtieria).subscribe((data) => {
              this.enableUserSelection(data);
          });
        }
        if(document.getElementById("deptsearchCriteria")!= null){
            document.getElementById("deptsearchCriteria").value="";
        }
        if(document.getElementById("deptsearchCriteria1")!= null){
          document.getElementById("deptsearchCriteria1").value="";
        }
        window.document.getElementById("selectedUsers").value = '';
    }

    loadUsersForDepartment_1(searchCrtieria : string) {
      this.isGlobalKNPCSearch = true;
      window.document.getElementById("selectedUsers").disabled = false;
      //this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment_1?searchCrtieria=:keyword";
     // this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
      //this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => this.arrangeRequiredDept(data));
      this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => {    
          this.knpcHierarchy = data;
          this.enableUserSelection(data);
      });
      if(document.getElementById("deptsearchCriteria1")!= null) {
          document.getElementById("deptsearchCriteria1").value="";
      }
      window.document.getElementById("selectedUsers").value = '';
  }

  loadUsersForDepartment_2(searchCrtieria : string) {
    this.globalSearchCrtieria=undefined;
    this.isGlobalKNPCSearch = true;
    window.document.getElementById("selectedUsers").disabled = false;
    //this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsersForDepartment_1?searchCrtieria=:keyword";
    //this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
    //this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => this.arrangeRequiredDept(data));
    this.recipientsService.getKNPCHierarchyUsersForDepartment_1(searchCrtieria).subscribe((data) => {    
        this.knpcHierarchy = data;
        this.enableUserSelection(data);
    });
    if(document.getElementById("deptsearchCriteria1")!= null) {
        document.getElementById("deptsearchCriteria1").value="";
    }
    window.document.getElementById("selectedUsers").value = '';
  }

  
	enableUserSelection(userArr:any){
    if(this.disableOnClosed){
      window.document.getElementById("selectedUsers").disabled = true;
    }
    this.disableOnClosed = false;
		var _ref = this;
		setTimeout(function() {
			for(var i=0;i<userArr.length ; i++){
        console.log("selectedUsers :: ",_ref.selectedUsers);
				if(_ref.selectedUsers.indexOf(userArr[i].employeeLogin)>=0){
					_ref.checkedAllWithSameClassName(document.getElementsByClassName(userArr[i].employeeLogin+"cc"));
				}else if(_ref.wradio_none.indexOf(userArr[i].employeeLogin)>=0){
					_ref.checkedAllWithSameClassName(document.getElementsByClassName(userArr[i].employeeLogin+"none"));
				}
			}
		},200);
		
  }
  
  checkedAllWithSameClassName(domArr:any){
		for(var i=0;i<domArr.length ; i++){
			domArr[i].checked = true;
		}
	}
   
   /* SelectedToRadio(e: any, user: User) {
    if (this.selectedUsers.indexOf(user.employeeLogin) == -1) {
      if (this.selectedUsers.indexOf(user.employeeLogin) > -1) {
        this.selectedUsers.splice(this.selectedUsers.indexOf(user.employeeLogin), 1)
      }
      this.wradio_to.push(user.employeeLogin)
    }
  }*/
  SelectedNoneRadio(e: any, user: User) {
      /*if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
      if (this.selectedUsers.indexOf(user.employeeLogin) > -1 || this.selectedUsers.indexOf(user.employeeLogin) > -1) {
          this.selectedUsers.splice(this.selectedUsers.indexOf(user.employeeLogin), 1)
          //this.selectedUsers.splice(this.selectedUsers.indexOf(user.employeeLogin), 1)
      }
      }*/
      
   
    if(this.selectedUsers.includes(user.employeeLogin)){
      this.selectedUsers.splice( this.selectedUsers.indexOf(user.employeeLogin), 1 );
          }
        
          //  if(this.wradio_cc.includes(user.employeeLogin)){
          //   this.wradio_cc.splice( this.wradio_cc.indexOf(user.employeeLogin), 1 );
               
          //    }
             if (this.wradio_none.indexOf(user.employeeLogin) == -1) {
              this.wradio_none.push(user.employeeLogin)
             }
          
  }
  SelectedCcRadio(e: any, user: User) {
    /*if (this.selectedUsers.indexOf(user.employeeLogin) == -1) {
      if (this.selectedUsers.indexOf(user.employeeLogin) > -1) {
        this.selectedUsers.splice(this.selectedUsers.indexOf(user.employeeLogin), 1)
      }
      this.selectedUsers.push(user.employeeLogin)
    }*/

   
   

    if (this.selectedUsers.indexOf(user.employeeLogin) == -1) {
      if (this.selectedUsers.indexOf(user.employeeLogin) > -1) {
        this.selectedUsers.splice(this.selectedUsers.indexOf(user.employeeLogin), 1)
      } 
      this.selectedUsers.push(user.employeeLogin)
    }
    // if (this.wradio_cc.indexOf(user.employeeLogin) == -1) {
    //    this.wradio_cc.push(user.employeeLogin)
    //  }
  }

  
    getDivisionUserList(division){
         this.searchCrtieria = document.getElementById("searchCriteria").value;
        //alert(this.searchCrtieria);
        this.loadUsersForDivision(division,this.searchCrtieria);
    }
    
     getDepartmentUserList(department: Department){
        this.isGlobalKNPCSearch=false;
        this.searchCrtieria = document.getElementById("deptsearchCriteria").value;
        this.loadUsersForDepartment(department,this.searchCrtieria);
    } 

    getDepartmentUserList_5(department: Department){
      this.isGlobalKNPCSearch=false;
      this.searchCrtieria = document.getElementById("deptsearchCriteria").value;
      this.loadUsersForDepartment_5(department,this.searchCrtieria);
  } 
    
    getDepartmentUserList_1(){
      this.globalSearchCrtieria = document.getElementById("deptsearchCriteria1").value;
      this.loadUsersForDepartment_1(this.globalSearchCrtieria);
    }

     loadKNPCHierarchy(){
        this.isGlobalKNPCSearch=false;
        //window.document.getElementById("selectedUsers").disabled = true;
        //added by Ravi Boni
        window.document.getElementById("selectedUsers").disabled = false;
        this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
        //endcurrentUser
        console.log("this.currentUser.employeeLogin in KNPC :: ",this.currentUser.employeeLogin);
        this.recipientsService.loadKNPCHierarchy(this.currentUser.employeeLogin).subscribe((data) =>
        {
          this.knpcHierarchy = data;
          this.enableUserSelection(data);
        });
    }
    private disableOnClosed : boolean = false;
    disableSelectedUser(){
        this.userSearchUrl = "";
        this.disableOnClosed = true;
        window.document.getElementById("selectedUsers").disabled = true;
        //this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
        //window.document.getElementById("selectedUsers").disabled = false;
        window.document.getElementById("selectedUsers").value = '';
    }

    disableSelectedUser_1(){      
    //  this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
      window.document.getElementById("selectedUsers").disabled = true;
      window.document.getElementById("selectedUsers").value = '';
  }

  disableSelectedUser_2(){      
   // this.userSearchUrl = global.base_url + "/EmployeeService/getKNPCHierarchyUsers?searchCrtieria=:keyword";
    window.document.getElementById("selectedUsers").disabled = false;
    window.document.getElementById("selectedUsers").value = '';
}

disableToCC() {
  window.document.getElementById("selectedUsers").disabled = true;
  window.document.getElementById("selectedUsers").value = '';
}
    
    searchDivision(evt: any, division:any ){
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode == 13) {
           this.getDivisionUserList(division);
        }
    }
    
    searchDepartment(evt :any ,department: any ){
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode == 13) {
           this.getDepartmentUserList(department);
        }
    }

    searchDepartment_5(evt :any ,department: any ){
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode == 13) {
         this.getDepartmentUserList_5(department);
      }
    }
    
    searchCrossDepartment(evt:any){
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode == 13) {
           this.getCrossDepartmentUserList();
        }
    }
    
    searchKNPCDepartment(evt:any ,department :any){
        evt = (evt) ? evt : window.event;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode == 13) {
           //this.loadKNPCHierarchyUsersList(department);
           this.getDepartmentUserList(department);
        }
    }

    searchKNPCDepartment_1(evt:any){
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode == 13) {
         //this.loadKNPCHierarchyUsersList(department);
         this.getDepartmentUserList_1();
      }
  }

    loadCrossDepartmentUsers(searchCrtieria :any){
      //window.document.getElementById("to").disabled = false;
      //window.document.getElementById("cc").disabled = false;
      //Added by RAvi Boni on 19-12-2017
      window.document.getElementById("selectedUsers").disabled = false;
      //end
      this.userSearchUrl = global.base_url + "/EmployeeService/getCrossDepartmentUsers?searchCrtieria=:keyword";
      this.recipientsService.getCrossDepartmentUsers(searchCrtieria).subscribe((data) => {
        this.crossDepartment = data;
        this.enableUserSelection(data);
      });

      if(document.getElementById("crosssearchCriteria")!= null){
          document.getElementById("crosssearchCriteria").value="";
      }
  }
  
   getCrossDepartmentUserList(){
      this.searchCrtieria = document.getElementById("crosssearchCriteria").value;
      
      this.loadCrossDepartmentUsers(this.searchCrtieria);
  }    
    
}

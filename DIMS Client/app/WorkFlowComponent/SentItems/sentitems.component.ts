import {Component, ViewChild} from '@angular/core'
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {Router, ActivatedRoute} from '@angular/router'
import {UserService} from '../../services/user.service'
import {BrowseSharedService} from '../../services/browseEvents.shared.service'
import {EmployeeDetails} from './../model/employeeDetails.model'
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'sent-item',
  providers: [WorkflowService],
  templateUrl: 'app/WorkFlowComponent/SentItems/sentitems.component.html'

})

export class SentLayoutComponent {
  @ViewChild('p') p;
  private currentFilter: string;
  private currentFolderFilter: string;
  private filterFlag: string ;
  private current_user: EmployeeDetails;
  Array = new Array;
  public tasks: any; sentlist: any;
  public is_all_selected: boolean;
  public _subscription: any;
  sentItemSelected: boolean;
  sentitemsWorkitems: any;
  selectedtasklist: any = [];
  selectedTask: any;
//pagination variables
  private currentPage: any = 1;
  private currentPageStartCount: number = 1;
  private perPage: number = 20;
  private currentPageEndCount: number = this.perPage;
  private totalItemsCount: number;
  totalPages: any = 1;
  public message = false; error_message = false;
  private is_archive_available: boolean = false;
  private is_cc_item: boolean = false;
  private is_done_by_sub_item: boolean = false;
  private is_complete_available :boolean =false;
  private colPreference : any;
  private delegateLogin: any;
  private sortColumn : any;
  private filterType : any;
  private completeArray : boolean[];
  
  private is_tasks_of_same_status: boolean
  private ccWrkItemId: string  = '';
  private actionList: any;

  private subscription: ISubscription[] = [];

  constructor(private workflowService: WorkflowService, public _router: ActivatedRoute,
    private userService: UserService, private browseSharedService: BrowseSharedService) {
    this.is_all_selected = false
    this.sentItemSelected = false;
     if(JSON.parse(localStorage.getItem("currentSentFilter")) == null){
        this.currentFilter = 'All'
    }else{
        this.currentFilter = JSON.parse(localStorage.getItem("currentSentFilter"));
    } 
    this.currentFolderFilter = ''
    this.filterFlag = ''
    this._router = _router;
    this.subscription.push(this.userService.notifyDelegateForChange.subscribe(data => this.refresh()));
    this.current_user = this.userService.getCurrentUser();
      this.delegateLogin = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
    this.totalItemsCount = 0;
    console.log("CCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAALLLLLLLLLLLLEEEEEEEEEEEEDDDDDDDDDDDD")
    this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignColPref(colData) ));
  }

  ngOnInit() {
    // Aziz commented
    // this.refresh();
   /* var context: any = this;
    this.paramsSubscription = this._router.queryParams.subscribe(
      data => {
        context.refresh();
      })*/

     /* setTimeout(() => {
        this.refresh();     
      }, 1000); */
      //this.currentPage = 1;
      
      this.p.ngOnInit();
      var context: any = this;
      this.subscription.push(this._router.queryParams.subscribe(
        data => {
          context.refresh();
        }))


      setTimeout(() => {
        if(localStorage.getItem("sentItemCurrentPage") != null )
         {
          let pageNum = parseInt(localStorage.getItem("sentItemCurrentPage"));
          console.log("&&&&&&&&&&&&&&%%%%%%%%%%%%%$$$$$$$$$$$  ::: ",pageNum);
          this.getPage(pageNum)
          if(this.p.service.instances.DEFAULT_PAGINATION_ID)
            this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (pageNum-1);
          this.p.next();
          localStorage.removeItem('sentItemCurrentPage')
         }else{
           console.log("sentItemCurrentPage is nothing")
         }
      },1001) 




       /* setTimeout(() => {
          if(localStorage.getItem("sentItemCurrentPage") != null )
           {
            let pageNum = parseInt(localStorage.getItem("sentItemCurrentPage"));
            this.getPage(pageNum+1)
            if(this.p.service.instances.DEFAULT_PAGINATION_ID)
              this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (pageNum);
            this.p.next();
            localStorage.removeItem('sentItemCurrentPage')
           }else{
             console.log("sentItemCurrentPage is nothing")
           }
        },1001) */
  }

  isCompleteTrue(arg) {
    return arg == true;
    }
  isCompleteFalse(arg) {
    return arg == false;
    }
    
  /*selectCheckbox(e: any, task: any) {
    this.is_tasks_of_same_status = true;
    if (e.currentTarget.checked == false) {
      this.selectedtasklist.splice(this.selectedtasklist.indexOf(task), 1);
      
      if (this.selectedtasklist.length > 0) {
        for (let task_to_compare of this.selectedtasklist) {
          for (let task_with_compare of this.selectedtasklist) {
            if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
              this.is_tasks_of_same_status = false;
            }
          }
        }
        if (this.is_tasks_of_same_status == false) {
         // this.actionList = [];
        }          
        else {
           this.getActions(); 
        }
      } else {
        this.is_tasks_of_same_status = false;
      }
    } else {

      this.selectedtasklist.push(task);
      if (this.selectedtasklist.length > 0) {
        for (let task_to_compare of this.selectedtasklist) {
          for (let task_with_compare of this.selectedtasklist) {
            if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
              this.is_tasks_of_same_status = false;
            }
          }
        }
        if (this.is_tasks_of_same_status == false) {
          //this.actionList = [];
        } else {
          this.getActions();    
        }
         
      }
    }
  
  }*/
  selectCheckbox(e: any, task: any) {
    
    this.is_tasks_of_same_status = true;
    if (e.currentTarget.checked == false) {
      this.selectedtasklist.splice(this.selectedtasklist.indexOf(task), 1);
      this.is_all_selected = false;
      this.is_archive_available = false;
      this.is_done_by_sub_item = false
      this.is_cc_item = false;
      //this.is_done_available = true;
        
      if (this.selectedtasklist.length > 0) {
          
        for (let task_to_compare of this.selectedtasklist) {
          for (let task_with_compare of this.selectedtasklist) {
            if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
                this.is_tasks_of_same_status = false;
                
            }
          }
        }
        if (this.is_tasks_of_same_status == false) {
          this.is_archive_available = false
          this.is_done_by_sub_item = false
          this.is_cc_item = false
          //this.is_done_available = true;
        } else {
          this.getActions();
        }
      } else {
        this.is_tasks_of_same_status = false;
      }
    } else {

      this.selectedtasklist.push(task);
      if (this.selectedtasklist.length > 0) {
          //this.is_launch_available = false; 
        for (let task_to_compare of this.selectedtasklist) {
          for (let task_with_compare of this.selectedtasklist) {
            if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
              this.is_tasks_of_same_status = false;
            }
          }
        }
        if (this.is_tasks_of_same_status == false) {
          this.is_archive_available = false
          this.is_done_by_sub_item = false;
          this.is_cc_item = false
          //this.is_done_available = true
        } else {
          this.getActions();
        }
      }
    }
    if (this.selectedtasklist.length < 1) {
      this.is_archive_available = false
      this.is_done_by_sub_item = false;
      this.is_cc_item = false;
      //this.is_done_available = false;
      this.is_complete_available = false;  
       //this.is_launch_available = false; 
    }
  }

  getActions(){

            this.ccWrkItemId = '';
            this.actionList = [];
            for (let task of this.selectedtasklist) {
              this.ccWrkItemId = this.ccWrkItemId + task.workflowWorkItemID+',';
              if (task.workflowWorkItemType == "cc") {  
                  this.is_cc_item = true;
              }   
            }
            if(this.ccWrkItemId.length > 0){
              this.subscription.push(this.workflowService.getWorkitemActions(this.ccWrkItemId, "SENT", this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(alist => this.actionList = alist));
            }else{
                this.subscription.push(this.workflowService.getWorkitemActions(this.selectedtasklist[0].workflowWorkItemID, "SENT", this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(alist => this.validateActions(alist)));
            }
  }
  
  validateActions(alist){
  for(let action of alist){
    if(this.selectedtasklist.length > 1){
      if(action != 'Launch' && action !='Reassign' && action != 'Add Users'){
        this.actionList.push(action); 
        this.actionList = this.actionList.slice();
      }
    }else{
      this.actionList.push(action); 
        this.actionList = this.actionList.slice();
    }
    
  }
}

  completeTask(event: any) {
    if (event == "complete") {
        if(this.selectedtasklist.length >0){
           for(let selectedTask of this.selectedtasklist){
                if(selectedTask.workflowItemRootSender == this.current_user.employeeLogin){
                        this.subscription.push(this.workflowService.completeTask(selectedTask.workflowWorkItemID).subscribe(data => this.refreshCompleteAction(data), error => this.browseSharedService.emitMessageChange(error)));
                }
            } 
        }
     }
  }
    
  refreshCompleteAction(data){
    
    this.selectedtasklist = [];
    this.actionList = [];
      this.browseSharedService.emitMessageChange(data);
      
      this.getFilterdSentItems(1);
      
  }
    
  refresh() {
    this.selectedtasklist = [];
    this.actionList = [];
    this.currentPage = 1;
    this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignColPref(colData) ));
    this.getPage(this.currentPage);
    if(this.p.service.instances.DEFAULT_PAGINATION_ID)
      this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (this.currentPage-1);
    this.p.next();
    /*if(localStorage.getItem("sentItemCurrentPage") != null )
    {
        this.currentPage = parseInt(localStorage.getItem("sentItemCurrentPage"));
    }*/
    //this.getFilterdSentItems(this.currentPage);
   // console.log("CCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAALLLLLLLLLLLLEEEEEEEEEEEEDDDDDDDDDDDD  ::: refresh()")
   // this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignColPref(colData) ));
  }


  applyFilter(filters: any) {
    this.setcurrentFilter(filters)
    this.getFilterdSentItems(1);
  }

  setcurrentFilter(filters) {
    this.currentFilter = filters.primaryFilter
    if(!(this.currentFilter == "ASC" || this.currentFilter == "DESC")){
        localStorage.setItem("currentSentFilter", JSON.stringify(this.currentFilter))
    }
    this.currentFolderFilter = filters.folderFilter
    this.filterFlag = filters.flagFilter;
    if(filters.flagFilter != "sort"){
        
        if(JSON.parse(localStorage.getItem("currentSentFilter")) == null){
            this.filterType = 'All'
        }else {
            this.filterType = JSON.parse(localStorage.getItem("currentSentFilter"));
        }
        //this.filterType = filters.primaryFilter;
    }else{
       if(JSON.parse(localStorage.getItem("currentSentFilter")) == null){
            this.filterType = 'All'
        }else {
            this.filterType = JSON.parse(localStorage.getItem("currentSentFilter"));
        } 
    }
    if(filters.sortColumn){
       this.sortColumn =  filters.sortColumn;
    }else{
        this.sortColumn = "";
    }
  }

  getFilterdSentItems(page) {
    // document.getElementById("loadingSpinner").style.display = 'block';
    // document.getElementById("workitemsList").style.display = 'none';
    // document.getElementById("workitemsListIsEmpty").style.display = 'none';
    // document.getElementById("scrollWI").style.backgroundColor = '#eee !important';
    
    console.log("getFilterdSentItems ::: ",page);
    this.subscription.push(this.workflowService.getFilterdSentItems({ primaryFilter: this.currentFilter, folderFilter: this.currentFolderFilter ,flagFilter: this.filterFlag,sortColumn : this.sortColumn, filterType : this.filterType}, page, this.perPage).subscribe(data => {
    this.assignSentboxValues(data);
    }));
    this.selectedtasklist = [];
    this.sentItemSelected = false;
    if(document.getElementById("check") !=null){
        document.getElementById("check").checked = false;
    }
    this.is_cc_item = false;
    this.is_done_by_sub_item = false;
    this.is_complete_available = false;
    this.is_all_selected = false;

  }
    
  //   assignSentItemsValues(data) {
  //     console.log("CCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAALLLLLLLLLLLLEEEEEEEEEEEEDDDDDDDDDDDD")
  //   this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignSentboxValues(colData,data) ));
  // }

  loadWorkitems(workflowWorkItemID) {
    this.sentitemsWorkitems = null;
    console.log("this.DDDDDDDDDDDDDDDDDDDDDDDDDDD  ::: ",this.currentPage)
    localStorage.setItem("sentItemCurrentPage",this.currentPage);
    this.subscription.push(this.workflowService.getSentItemsWorkItems(workflowWorkItemID).subscribe(data =>this.assignSentItemWorkitem(workflowWorkItemID, data)));
  }

  assignSentItemWorkitem(workitemId, data) {
    this.sentitemsWorkitems = data;
    data = null;
  }

  assignColPref(colData){
    this.colPreference = colData;
  }

  assignSentboxValues( data) {
    //document.getElementById("loadingSpinner").style.display = 'none';
    //document.getElementById("scrollWI").style.backgroundColor = '#fff !important';
      //this.colPreference = colData;
      this.tasks = data;
      data = null;
     // colData = null;
    //for(let task of this.tasks) {
      //this.subscription.push(this.workflowService.getSentItemsWorkItems(task.workflowWorkItemID).subscribe(data =>this.sentitemsWorkitems[task.workflowWorkItemID] = data));
     // this.subscription.push(this.workflowService.getSentItemsWorkItems(task.workflowWorkItemID).subscribe(data =>this.assignSentItemWorkitem(task.workflowWorkItemID, data)));
   // }

    console.log(this.sentitemsWorkitems)
    
    this.totalItemsCount = 0;
    this.totalPages = 1;
    if (this.tasks.length > 0) {
      //document.getElementById("workitemsList").style.display = 'block';
      this.totalItemsCount = this.tasks[0].totalCount;
      if (this.totalItemsCount % this.perPage == 0) {
        this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString());
      } else {
        this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString()) + 1;
      }
    }/*else{
      document.getElementById("workitemsListIsEmpty").style.display = 'block';
    }*/
  }


  toggleSelectAll(event: any) {
      this.selectedtasklist = [];
    this.is_tasks_of_same_status = true;
    for (let task of this.tasks) {
      task.is_checked = event.currentTarget.checked
      this.selectedtasklist.push(task)
    }
    if (event.currentTarget.checked != true) {
      this.selectedtasklist = [];
      this.is_cc_item = false;
      this.is_tasks_of_same_status = false;
    } else {
      for (let task_to_compare of this.selectedtasklist) {
        for (let task_with_compare of this.selectedtasklist) {
          if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
            this.is_tasks_of_same_status = false;
          }
        }
      }
      this.getActions();
   
    }
    this.is_all_selected = event.currentTarget.checked
  }

  ngOnDestroy() {
    for(let subs of this.subscription) {
      subs.unsubscribe();
    }
    this.tasks = null;
    this.subscription = null;
    this.selectedtasklist = null;
    
    this.sentitemsWorkitems = null;
    this.actionList = null;
    this.completeArray = null;
  }


  getPage(page: any) { 
    this.selectedtasklist = [];
    this.actionList = [];
    this.currentPage = page;
    this.getFilterdSentItems(page);
    return page;
  }

    leftClicked(p : any){
        if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value==''){
            p.previous();
        }else{
            let selectedPage = document.getElementById("currentPageSelected").value;
           if(selectedPage == p.service.instances.DEFAULT_PAGINATION_ID.currentPage){
                p.previous();
            }else{
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
            }
            //localStorage.setItem("sentItemCurrentPage",selectedPage);
            //console.log(selectedPage)
        }
        
    }
    
    rightClicked(p : any){
        if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value==''){
            p.next();
        }else{
            let selectedPage = document.getElementById("currentPageSelected").value;
            
            if(selectedPage == p.service.instances.DEFAULT_PAGINATION_ID.currentPage){
                p.next();
            }else{
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
            }
            //localStorage.setItem("sentItemCurrentPage",selectedPage);
        }
        
    }
    
    onEnterKey(evt: any, p : any ){
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode == 13) {
         if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value!=''){
             let selectedPage = document.getElementById("currentPageSelected").value;
             if(selectedPage <=0){
              selectedPage = 1;
              document.getElementById("currentPageSelected").value = selectedPage;
             }else if(selectedPage > this.totalPages){
              selectedPage = this.totalPages;
              document.getElementById("currentPageSelected").value = selectedPage;
             }
             //localStorage.setItem("sentItemCurrentPage",selectedPage);
              p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
              p.next();
         }
      }
  }
}

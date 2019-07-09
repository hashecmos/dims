import {Component, Input, Output, EventEmitter, ViewChild} from '@angular/core'
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {Router, ActivatedRoute} from '@angular/router'
import { TaskItems } from './../../model/taskItems.model';
import {UserService} from '../../services/user.service'
import { BrowseSharedService} from './../../services/browseEvents.shared.service'
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'inbox-list',
  providers: [WorkflowService],
  templateUrl: 'app/WorkFlowComponent/Inbox/InboxList.component.html',
})

export class InboxList {
  @ViewChild('p') p;
  private task_items: TaskItems[]
  private selectedtasklist: TaskItems[]
  private is_archive_available: boolean
  private is_cc_item: boolean;
  private is_to_item: boolean; 
  private is_done_by_sub_item: boolean;
  private is_done_available: boolean
  private is_complete_available : boolean;
  private is_launch_available : boolean;
  private current_user: any;
  private is_tasks_of_same_status: boolean
  private is_all_selected: boolean
  public foldersuccess: boolean = false;
  private currentFilter: string
  private currentFolderFilter: string
  private currentPage: any = 1;
  //pagination variables
  private currentPageStartCount: number = 1
  private perPage: number = 20
  private currentPageEndCount: number = this.perPage
  private totalItemsCount: number
  totalPages: any = 1; 
  private filterFlag: string ;
  private paramsSubscription: ISubscription;
  selectedTask: any;
  inboxItemSelected : boolean;
  private colPreference :any;
   private delegateLogin: any;
  private sortColumn : any;
  private filterType :  any;
  private completeArray : boolean[];
  private actionList : any;
  private ccWrkItemId : string;
  private subscription: ISubscription[] = [];

  constructor(private workflowService: WorkflowService, private router: Router, public _router: ActivatedRoute,
    private userService: UserService, private sharedSerivce: BrowseSharedService) {
    this.selectedtasklist = []
    this.is_archive_available = false
    this.is_cc_item = false;
    this.is_done_by_sub_item = false
    this.is_done_available = true;
    this.current_user = userService.getCurrentUser();
    this.delegateLogin = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
    this.actionList = [];
    this.is_all_selected = false
    

    this.totalItemsCount = 0;
    if(JSON.parse(localStorage.getItem("currentFilter")) == null){
        this.currentFilter = 'All'
    }else{
        this.currentFilter = JSON.parse(localStorage.getItem("currentFilter"));
    }    
    
    
    this.currentFolderFilter = ''
    this.filterFlag = ''
    this._router = _router;
    this.subscription.push(this.userService.notifyDelegateForChange.subscribe(data => this.refresh()));
    console.log("CCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAALLLLLLLLLLLLEEEEEEEEEEEEDDDDDDDDDDDD")
    this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignColPref(colData) ));
  }

  ngOnInit() {
    /*setTimeout(() => {
      this.refresh();     
    }, 1000); */
    this.p.ngOnInit();
    var context: any = this;
    const paramsubs = this._router.queryParams.subscribe(
      data => {
        context.refresh();
      })


      setTimeout(() => {
        if(localStorage.getItem("inboxItemCurrentPage") != null )
         {
          let pageNum = parseInt(localStorage.getItem("inboxItemCurrentPage"));
          console.log("&&&&&&&&&&&&&&%%%%%%%%%%%%%$$$$$$$$$$$  ::: ",pageNum);
          this.getPage(pageNum)
          if(this.p.service.instances.DEFAULT_PAGINATION_ID)
            this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (pageNum-1);
          this.p.next();
          localStorage.removeItem('inboxItemCurrentPage')
         }else{
           console.log("inboxItemCurrentPage is nothing")
         }
      },1001) 

    //by default we should apply all filter on load.
    //this.getFilterdInboxItems(1);
    const subs = this.sharedSerivce.emitchangeEvent$.subscribe(showmessage => this.ShowMessage(showmessage));
    this.subscription.push(subs);
  }

  ngOnDestroy() {
    for(let subs of this.subscription) {
      subs.unsubscribe();
    }
    this.task_items = null;
    this.subscription = null;
    this.selectedtasklist = null;
    this.selectedTask = null;
    this.actionList = null;
    this.completeArray = null;
  }

  ShowMessage(event: any) {
    if (event.status == 200) {
      this.foldersuccess = true;
    }
  }

  removeFolderMessage() {
    this.foldersuccess = false;
  }

  //need to verify that do we really need this URL
  getFilterdInboxItems(page: any) {
    // document.getElementById("loadingSpinner").style.display = 'block';
    // document.getElementById("workitemsList").style.display = 'none';
    // document.getElementById("workitemsListIsEmpty").style.display = 'none';
    // document.getElementById("scrollWI").style.backgroundColor = '#eee !important';
    
    this.subscription.push(this.workflowService.getFilterdInboxItems({ primaryFilter: this.currentFilter, folderFilter: this.currentFolderFilter ,flagFilter: this.filterFlag ,sortColumn : this.sortColumn,filterType : this.filterType}, page, this.perPage).subscribe(data => this.getInboxItems(data)));
    this.is_archive_available = false;
    this.is_done_by_sub_item = false
    this.is_done_available = true;
    this.is_cc_item = false;
    this.is_all_selected = false;
  }

  /*assignInboxValues(data) {
      
   const subs = this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.getInboxItems(colData,data) );
    this.subscription.push(subs);
  }*/

    getInboxItems(data){
        //document.getElementById("loadingSpinner").style.display = 'none';
        //document.getElementById("scrollWI").style.backgroundColor = '#fff !important';
        this.current_user = this.userService.getCurrentUser();
        this.delegateLogin = this.userService.getCurrentDelegatedUserOrCurrentUserLogin();
        this.selectedtasklist = [];
        //this.colPreference = colData;
        this.task_items = data;
        console.log("task_items ::: ",this.task_items);
        this.totalItemsCount = 0;
        this.totalPages = 1;
        if (this.task_items.length > 0) {
          //document.getElementById("workitemsList").style.display = 'block';
          this.totalItemsCount = this.task_items[0].totalCount
          if (this.totalItemsCount % this.perPage == 0) {
            this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString());
          } else {
            this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString()) + 1;
          }
        }/*else{
          document.getElementById("workitemsListIsEmpty").style.display = 'block';
        } */ 
        data = null;
       // colData = null;                                                  
    }
    
    
  applyFilter(filters : any ,clicked : any) {
        this.deSelectall()
        this.setcurrentFilters(filters)
        this.getFilterdInboxItems(1)
      return;
  }

  setcurrentFilters(filters) {
    this.currentFilter = filters.primaryFilter
    if(!(this.currentFilter == "ASC" || this.currentFilter == "DESC")){
        localStorage.setItem("currentFilter", JSON.stringify(this.currentFilter))
    }
    this.currentFolderFilter = filters.folderFilter
    if(filters.flagFilter != "sort"){
        
        if(JSON.parse(localStorage.getItem("currentFilter")) == null){
            this.filterType = 'All'
        }else {
            this.filterType = JSON.parse(localStorage.getItem("currentFilter"));
        }
        //this.filterType = filters.primaryFilter;
    }else{
       if(JSON.parse(localStorage.getItem("currentFilter")) == null){
            this.filterType = 'All'
        }else {
            this.filterType = JSON.parse(localStorage.getItem("currentFilter"));
        } 
    }
    this.filterFlag = filters.flagFilter;
    if(filters.sortColumn){
       this.sortColumn =  filters.sortColumn;
    }else{
        this.sortColumn = "";
    }
   
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
    /*if(localStorage.getItem("inboxItemCurrentPage") != null )
    {
        this.currentPage = parseInt(localStorage.getItem("inboxItemCurrentPage"));
    }*/
    //this.getFilterdInboxItems(this.currentPage);
   // console.log("CCCCCCCCCCCCCCCCCCAAAAAAAAAAAAAAALLLLLLLLLLLLEEEEEEEEEEEEDDDDDDDDDDDD  ::: refresh()")
    //this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignColPref(colData) ));
  }
  
  assignColPref(colData){
    this.colPreference = colData;
  }
    
  isCompleteTrue(arg) {
    return arg == true;
    }
  isCompleteFalse(arg) {
    return arg == false;
    }
  selectCheckbox(e: any, task: any) {
    
    this.is_tasks_of_same_status = true;
    if (e.currentTarget.checked == false) {
      this.selectedtasklist.splice(this.selectedtasklist.indexOf(task), 1);
      this.is_all_selected = false;
      this.is_archive_available = false;
      this.is_done_by_sub_item = false
      this.is_cc_item = false;
      this.is_done_available = true;
        
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
          this.is_done_available = true;
        } else {
          this.updateAction();
        }
      } else {
        this.is_tasks_of_same_status = false;
      }
    } else {

      this.selectedtasklist.push(task);
      if (this.selectedtasklist.length > 0) {
          this.is_launch_available = false; 
        for (let task_to_compare of this.selectedtasklist) {
          for (let task_with_compare of this.selectedtasklist) {
            if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
              this.is_tasks_of_same_status = false;
            }
          }
        }
        console.log("is_tasks_of_same_status  ::: ",this.is_tasks_of_same_status)
        if (this.is_tasks_of_same_status == false) {
          this.is_archive_available = false
          this.is_done_by_sub_item = false;
          this.is_cc_item = false
          this.is_done_available = true
        } else {
          this.updateAction();
        }
      }
    }
    if (this.selectedtasklist.length < 1) {
      this.is_archive_available = false
      this.is_done_by_sub_item = false;
      this.is_cc_item = false;
      this.is_done_available = false;
      this.is_complete_available = false;  
       this.is_launch_available = false; 
    }
  }

  updateAction(){

            this.ccWrkItemId = '';
            this.actionList = [];
            for (let task of this.selectedtasklist) {
              this.ccWrkItemId = this.ccWrkItemId + task.workflowWorkItemID+',';
              if (task.workflowWorkItemType == "cc") {                
                  this.is_cc_item = true;
              }   
            }
            
            if(this.ccWrkItemId.length > 0){
              const subsc = this.workflowService.getWorkitemActions(this.ccWrkItemId, "INBOX", this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(alist => this.actionList = alist);
              this.subscription.push(subsc);  
          }else{
              const subsc =   this.workflowService.getWorkitemActions(this.selectedtasklist[0].workflowWorkItemID, "INBOX", this.userService.getCurrentDelegatedUserOrCurrentUserLogin()).subscribe(alist => this.validateActions(alist));
            this.subscription.push(subsc);   
          }
  }
validateActions(alist){
  for(let action of alist){
    if(this.selectedtasklist.length > 1){
      if(action != 'Launch' && action !='Reassign' && action!= 'Add Users'){
        this.actionList.push(action); 
        this.actionList = this.actionList.slice();
      }
    }else{
      this.actionList.push(action); 
        this.actionList = this.actionList.slice();
    }
    
  }
}

  deSelectall(){
    // undo selection of tasks
    if(this.task_items != undefined){
        for (let task of this.task_items) {
            task.is_checked = false
        }
    }
    
    this.is_all_selected = false
    this.is_archive_available = false
    this.is_done_by_sub_item = false
    this.is_cc_item = false
    this.is_tasks_of_same_status = false
    this.is_complete_available = false
  }

  archiveTasks(event: any) {
    for (let task of this.selectedtasklist) {
      const subsc = this.workflowService.archiveItem(task.workflowWorkItemID).subscribe();
      this.subscription.push(subsc);
    }
    this.deSelectall()
    this.router.navigate(['work-flow/archive'])
  }

  toggleSelectAll(event: any) {
    this.selectedtasklist = [];
    console.log("this.task_items  ::: ",this.task_items);
    this.is_tasks_of_same_status = true;
    for (let task of this.task_items) {
      task.is_checked = event.currentTarget.checked
      this.selectedtasklist.push(task)
    }
    if (event.currentTarget.checked != true) {
      this.selectedtasklist = [];
      this.is_tasks_of_same_status = false;
    } else {
      this.updateAction();
      for (let task_to_compare of this.selectedtasklist) {
        for (let task_with_compare of this.selectedtasklist) {
          if (task_to_compare.workflowWorkItemType != task_with_compare.workflowWorkItemType) {
            this.is_tasks_of_same_status = false;
            break;
          }
        }
        break;
      }
      console.log("this.is_tasks_of_same_status ::: FINAL ::: "+this.is_tasks_of_same_status);
    }
    this.is_all_selected = event.currentTarget.checked
  }


  getPage(page: any) { 
    this.selectedtasklist = [];
    this.actionList = []; 
    this.currentPage = page;
    this.getFilterdInboxItems(page);
    return page;
  }
  
    
  completeTask(event: any) {
    if (event == "complete") {
        if(this.selectedtasklist.length >0){
           for(let selectedTask of this.selectedtasklist){
                if(selectedTask.workflowItemRootSender == this.current_user.employeeLogin){
                     const subsc = this.workflowService.completeTask(selectedTask.workflowWorkItemID).subscribe(data => this.refreshCompleteAction(data), error => this.sharedSerivce.emitMessageChange(error));
                      this.subscription.push(subsc);  
              }
            } 
        }
    }
  }
    
   refreshCompleteAction(data){
    this.selectedtasklist = [];
    this.actionList = [];
      this.sharedSerivce.emitMessageChange(data)
      //this.getFilterdInboxItems(1);
      
  }

  setPageNumber(){
    console.log("this.currentPage  ::: ",this.currentPage)
   localStorage.setItem("inboxItemCurrentPage",this.currentPage);
  }
 
    leftClicked(p : any){
      console.log("PAge in left click ::; ",p)
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
            //localStorage.setItem("inboxItemCurrentPage",""+(parseInt(selectedPage)-1));
            //localStorage.setItem("inboxItemCurrentPage",selectedPage);
        }
    }
    
    rightClicked(p : any){
      console.log("PAge in right click ::; ",p)
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
            //localStorage.setItem("inboxItemCurrentPage",""+(parseInt(selectedPage)+1));
            //localStorage.setItem("inboxItemCurrentPage",selectedPage);
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
              // localStorage.setItem("inboxItemCurrentPage",selectedPage);
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
           }
        }
        //this.getPage(1);
    }

    
}

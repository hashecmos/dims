import {Component, Input,ViewChild} from '@angular/core'
import {WorkflowService} from '../../services/workflowServices/workflow.service'
import {Router, ActivatedRoute} from '@angular/router'
import {UserService} from '../../services/user.service'
import { BrowseSharedService} from './../../services/browseEvents.shared.service'
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'archive-layout',
  providers: [WorkflowService],
  templateUrl: 'app/WorkFlowComponent/Archive/archive.component.html',

})

export class ArchiveLayoutComponent {
  @ViewChild('p') p;
  public tasks: any; is_all_selected: boolean;
  private perPage: number = 20;
  private currentPage: any = 1;
  private totalItemsCount: number = 0;
  totalPages: any = 1;
  public foldersuccessmessage: boolean = false;
  private currentFolderFilter
  private currentFilter: string
  private filterFlag: string ;
  private is_archive_available: boolean= false;
  private is_tasks_of_same_status : boolean =false;
  private colPreference : any;
  private current_user : any;
  private sortColumn : any;
  private actionList :  any;
  private subscription: ISubscription[] = [];

  @Input() private is_item_selected: boolean;
  constructor(private workflowService: WorkflowService, private router: Router, public _router: ActivatedRoute,
    private userService: UserService, private sharedService: BrowseSharedService) {
    this.is_all_selected = false
    this.is_item_selected = true
    this.currentFolderFilter = ""
    this._router = _router;
    this.actionList =[];
    this.currentFilter = 'All'
    this.filterFlag = ''
    this.subscription.push(this.userService.notifyDelegateForChange.subscribe(data => this.refresh()));
      this.current_user = this.userService.getCurrentUser();
  }

  getArchiveItems(page) {
    // document.getElementById("loadingSpinner").style.display = 'block';
    // document.getElementById("workitemsList").style.display = 'none';
    // document.getElementById("workitemsListIsEmpty").style.display = 'none';
    // document.getElementById("scrollWI").style.backgroundColor = '#eee !important';
    this.subscription.push(this.workflowService.getArchiveItems({ primaryFilter: this.currentFilter ,folderFilter: this.currentFolderFilter ,flagFilter: this.filterFlag,sortColumn : this.sortColumn }, page, this.perPage).subscribe(data => this.assignArchiveItems(data)));
  }
    
    assignArchiveItems(data) {
        this.subscription.push(this.userService.getColumnPreference(this.current_user.employeeLogin).subscribe((colData) => this.assignArchiveValues(colData,data) ));
    }

  selectedtasklist: any = [];
  performAction: any = "";
    
  selectCheckbox(e: any, task: any) {
    this.performAction = "";
    if (e.currentTarget.checked == false) {
      this.selectedtasklist.splice(this.selectedtasklist.indexOf(task), 1);
       this.is_archive_available= true;
    } else {
      this.selectedtasklist.push(task);
         this.is_archive_available= true;
    }
    if (this.selectedtasklist.length == 1) {
      this.actionList = ['Forward','Launch'];
      this.performAction = "archiveAction"
      this.is_archive_available= false;
      this.is_tasks_of_same_status= true;
    }else{
      if(this.selectedtasklist.length == 0){
        this.actionList = [];
      }else{
        this.actionList =['Forward'];
      }
      
    }
  }
    

  assignArchiveValues(colData, data) {
    // document.getElementById("loadingSpinner").style.display = 'none';
    // document.getElementById("scrollWI").style.backgroundColor = '#fff !important';
      this.colPreference=colData;
    this.tasks = data;
    this.totalItemsCount = 0;
    this.totalPages = 1;
    if (this.tasks.length > 0) {
      //document.getElementById("workitemsList").style.display = 'block';
      this.totalItemsCount = this.tasks[0].totalCount
      if (this.totalItemsCount % this.perPage == 0) {
        this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString());
      } else {
        this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString()) + 1;
      }
    }/*else{
      document.getElementById("workitemsListIsEmpty").style.display = 'block';
    }*/
  }


  // ngOnInit() {
    
  //   this.p.ngOnInit();
  //   var context: any = this;
  //   const paramsubs = this._router.queryParams.subscribe(
  //     data => {
  //       context.refresh();
  //     })


  //     setTimeout(() => {
  //       if(localStorage.getItem("inboxItemCurrentPage") != null )
  //        {
  //         let pageNum = parseInt(localStorage.getItem("inboxItemCurrentPage"));
  //         console.log("&&&&&&&&&&&&&&%%%%%%%%%%%%%$$$$$$$$$$$  ::: ",pageNum);
  //         this.getPage(pageNum)
  //         if(this.p.service.instances.DEFAULT_PAGINATION_ID)
  //           this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (pageNum-1);
  //         this.p.next();
  //         localStorage.removeItem('inboxItemCurrentPage')
  //        }else{
  //          console.log("inboxItemCurrentPage is nothing")
  //        }
  //     },1001) 

  //   const subs = this.sharedSerivce.emitchangeEvent$.subscribe(showmessage => this.ShowMessage(showmessage));
  //   this.subscription.push(subs);
  // }

  ngOnInit() {
   /* this.getArchiveItems(1);
    this.subscription.push(this.sharedService.emitFolderfilterForArchive$.subscribe(folderFilter => {
      this.setCurrentFilters(folderFilter)
      this.getArchiveItems(1);
    }))
    this.subscription.push(this.sharedService.emitchangeEvent$.subscribe(showmessage => this.ShowMessage(showmessage)));
   
    this.p.ngOnInit();
    var context: any = this;
    this.subscription.push(this._router.queryParams.subscribe(
      data => {
        context.refresh();
      }))*/

      this.p.ngOnInit();
      var context: any = this;
      const paramsubs = this._router.queryParams.subscribe(
        data => {
          context.refresh();
        })
  
        setTimeout(() => {
                if(localStorage.getItem("archiveItemCurrentPage") != null )
                 {
                  let pageNum = parseInt(localStorage.getItem("archiveItemCurrentPage"));
                  console.log("&&&&&&&&&&&&&&%%%%%%%%%%%%%$$$$$$$$$$$  ::: ",pageNum);
                  this.getPage(pageNum)
                  if(this.p.service.instances.DEFAULT_PAGINATION_ID)
                    this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (pageNum-1);
                  this.p.next();
                  localStorage.removeItem('archiveItemCurrentPage')
                 }else{
                   console.log("archiveItemCurrentPage is nothing")
                 }
              },1001) 

      /*setTimeout(() => {
        if(localStorage.getItem("archiveItemCurrentPage") != null )
         {
          let pageNum = parseInt(localStorage.getItem("archiveItemCurrentPage"));
          this.getPage(pageNum+1)
          if(this.p.service.instances.DEFAULT_PAGINATION_ID)
            this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (pageNum);
          this.p.next();
          localStorage.removeItem('archiveItemCurrentPage')
         }else{
           console.log("archiveItemCurrentPage is nothing")
         }
      },1001) */

      this.subscription.push(this.sharedService.emitchangeEvent$.subscribe(showmessage => this.ShowMessage(showmessage)));
      //this.subscription.push(subs);
  }


  setPageNumber(){
    console.log("this.currentPage  ::: ",this.currentPage)
   localStorage.setItem("archiveItemCurrentPage",this.currentPage);
  }

  ShowMessage(event: any) {
    if (event.status == 200) {
      this.foldersuccessmessage = true;
    }
  }

  removeFolderMessage() {
    this.foldersuccessmessage = false;
  }

  ngOnDestroy() {
    for(let subs of this.subscription) {
      subs.unsubscribe();
    }
    this.tasks = null;
    this.subscription = [];
  }

  setCurrentFilters(folderFilter) {
    this.currentFolderFilter = folderFilter
  }

  toggleSelectAll(event: any) {
     this.selectedtasklist = [];
     for (let task of this.tasks) {
          task.is_checked = event.currentTarget.checked;
            this.selectedtasklist.push(task);
     }
     if (event.currentTarget.checked != true) {
      this.selectedtasklist = [];
      this.is_tasks_of_same_status = false;
     } else {
        this.is_tasks_of_same_status= true;
        //this.is_all_selected = event.currentTarget.checked
     }
     this.actionList =['Forward'];
     this.is_all_selected = event.currentTarget.checked
  }
  refresh() {
   
    this.clearList();
    this.currentPage = 1;
    this.getPage(this.currentPage);
    if(this.p.service.instances.DEFAULT_PAGINATION_ID)
      this.p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (this.currentPage-1);
    this.p.next();
    /*if(localStorage.getItem("archiveItemCurrentPage") != null )
    {
        this.currentPage = parseInt(localStorage.getItem("archiveItemCurrentPage"));
    }*/
    this.getArchiveItems(this.currentPage);
  }
  
  clearList(){
    this.selectedtasklist =[];
    this.tasks = [];
    this.is_all_selected = false;
    this.is_item_selected = false;
    this.is_tasks_of_same_status =false;
  }
  getPage(page: any) { 
    this.currentPage = page;
    this.getArchiveItems(page);
    return page;
  }
    
    applyFilter(filters) {
        this.deSelectall()
        this.setcurrentFilters(filters)
        this.getArchiveItems(1)
      }
        
      setcurrentFilters(filters) {
        this.currentFilter = filters.primaryFilter
        this.currentFolderFilter = filters.folderFilter
        this.filterFlag = filters.flagFilter;
        if(filters.sortColumn){
           this.sortColumn =  filters.sortColumn;
        }else{
            this.sortColumn = "";
        }
      }
    
      deSelectall() {
        // undo selection of tasks
        for (let task of this.tasks) {
          task.is_checked = false
        }
        this.is_all_selected = false
        this.is_item_selected = false
      }
    
    leftClicked(p : any){
      this.clearList();
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
            //localStorage.setItem("archiveItemCurrentPage",selectedPage);
        }
        
    }
    
    rightClicked(p : any){
      this.clearList();
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
            
            //localStorage.setItem("archiveItemCurrentPage",selectedPage);
            
        }
        
    }
    
     onEnterKey(evt: any, p : any ){
      this.clearList();
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
               //localStorage.setItem("archiveItemCurrentPage",selectedPage);
                p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
                p.next();
           }
        }
    }
    emptySelectedList(){
      this.selectedtasklist = [];
      this.actionList =[];
    }
}

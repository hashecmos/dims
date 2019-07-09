import {Component} from '@angular/core'
import {Router} from '@angular/router'
import {WorkflowService} from './../services/workflowServices/workflow.service'
import {SharedService} from './../services/advanceSearchTasks.service'
import { TaskItems } from './../model/taskItems.model';
import { SearchService } from '../services/search.service';
import { OnDestroy } from "@angular/core";
import { ISubscription } from "rxjs/Subscription";

@Component({
  selector: 'task-search',
  providers: [WorkflowService,SearchService],
  templateUrl: 'app/SharedComponents/task_search.component.html'
})

export class TaskSearch {
  public documents_items: any; multiple_select: any; archivelist: any; index: any; task_items = new Array<TaskItems>();
  public searchfilter: any;
  public previous_selection: any;
  //pagination variables
  private currentPage: any = 1;  
  private currentPageStartCount: number = 1
  private perPage: number = 20
  private currentPageEndCount: number = this.perPage
  private totalItemsCount: number
  totalPages: any = 1;
  public folder_id: any;
  public sub_folders: any;
  public sub_folder_active: string;
  public is_all_selected: boolean; checkin: boolean;
  public searchType = ""
  public resultText = "";
  sentitemsWorkitems: any;  
  private subscription: ISubscription[] = [];

  /*constructor(public _router: Router, public workflowService: WorkflowService, private sharedService: SharedService) {
    this.sharedService.emitSearchedTasks$.subscribe(data => {
      this.task_items = data;console.log(data)
      this.resultText = "No Results Found";
      if(localStorage.getItem('workflowSearchType')) {
        this.searchType = localStorage.getItem('workflowSearchType');
      }
    });    
   }
  ngOnInit() {
    if(localStorage.getItem('workflowSearchType')) {
      this.searchType = localStorage.getItem('workflowSearchType');
    }
    this.resultText = "Searching Workflows";
  }*/

  constructor(	private searchService: SearchService,public _router: Router, public workflowService: WorkflowService, private sharedService: SharedService) {
    //if(localStorage.getItem('workflowSearchType')) {
    //this.searchType = localStorage.getItem('workflowSearchType');
      //  console.log('get local storage    :::::::::::::!', this.searchType);
  //}

}
ngOnInit() {
  this.subscription.push(this.sharedService.emitSearchedTasks$.subscribe(data => {
  this.task_items = data;console.log(data)
  this.totalPages = 1;
  if (this.task_items.length > 0) {
    this.totalItemsCount = this.task_items[0].totalCount    
    if (this.totalItemsCount % this.perPage == 0) {
      this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString());
    } else {
      this.totalPages = parseInt((this.totalItemsCount / this.perPage).toString()) + 1;
    }
    this.resultText = "";
  }else{
    this.resultText = "No Results Found";
  }    
    this.searchType = localStorage.getItem('workflowSearchType');
  /*if(this.searchType == 'Sent') {
    for(let task of this.task_items) {
      this.workflowService.getSentItemsWorkItems(task.workflowWorkItemID).subscribe(data =>this.sentitemsWorkitems[task.workflowWorkItemID] = data);
    }
  } */
  document.getElementById("displayLoader").style.display="none";     
  document.getElementById("errortext_search").style.display="none"; 
})); 
//if(localStorage.getItem('workflowSearchType')) {
//  this.searchType = localStorage.getItem('workflowSearchType');
//}

}

ngOnDestroy() {
    for(let subs of this.subscription) {
      subs.unsubscribe();
    }
    this.subscription = null;
    this.sentitemsWorkitems = null;
  }

getPage(page: number) {
  this.currentPage = page;  
  let workflowSearch = JSON.parse(localStorage.getItem('searchedWorkflowsP'))
  workflowSearch.pageNo = page;
  this.subscription.push(this.searchService.getSearchTaskItems(workflowSearch).subscribe(data => this.sharedService.emitTasks(data)));
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
      
      
      
  }
  
}

onEnterKey(evt: any, p : any ){
  evt = (evt) ? evt : window.event;
  var charCode = (evt.which) ? evt.which : evt.keyCode;
  if (charCode == 13) {
     if(document.getElementById("currentPageSelected")!= null && document.getElementById("currentPageSelected").value!=''){
         let selectedPage = document.getElementById("currentPageSelected").value;
          p.service.instances.DEFAULT_PAGINATION_ID.currentPage = (selectedPage-1);
          p.next();
     }
  }
}
  deSelectall() {
    for (let folder of this.sub_folders) {
      folder.is_checked = false
    }
    for (let doc of this.documents_items) {
      doc.is_checked = false
    }
    this.is_all_selected = false
  }

  selectDoc(e: any, doc: any) {
    if (!this.previous_selection) {
      this.previous_selection = doc.m_type
      this.multiple_select = 'false'
    }
    else {
      this.multiple_select = this.previous_selection
      this.previous_selection = doc.m_type

    }

    if (e.currentTarget.checked == true) {
      this.archivelist.push(doc)
      doc.visible = true
    }
    else {
      doc.visible = false
      this.previous_selection = ''
      this.index = this.archivelist.indexOf(doc)
      if (this.index > -1) {
        this.archivelist.splice(this.index, 1);
      }
      this.is_all_selected = false

    }
  }

  checkoutDoc(doc: any) {
    if (doc.checkin == false) {
      doc.checkin = true
      this.checkin = true
    }
    else {
      doc.checkin = false
      this.checkin = false
    }
  }
  checkout_actn() {
    for (let entry of this.archivelist) {
      entry.checkin = true;
    }
  }

  applyFilter(event: any) {
    if (event == 'all') {
      this.searchfilter = { subject: 'd' }
    } else {
      this.searchfilter = { m_type: event, folder_id: this.folder_id };
    }
    this.deSelectall()
  }

  pageChange(pageNum: any) {
    this.currentPageEndCount = (pageNum * this.perPage) >= this.task_items.length ? this.task_items.length : pageNum * this.perPage;
    this.currentPageStartCount = pageNum == 1 ? 1 : (pageNum * this.perPage) - 4;
    return pageNum
  }

  toggleSelectAll(event: any) {
    for (let folder of this.sub_folders) {
      folder.is_checked = event.currentTarget.checked
    }
    for (let doc of this.documents_items) {
      doc.is_checked = event.currentTarget.checked
    }
    this.is_all_selected = event.currentTarget.checked
  }

  opennewWindow(event: any) {
    window.open("");
    event.stopPropagation();
  }
  
    
  getSearchTasks(){
      
  }

  loadWorkitems(workflowWorkItemID) {
    this.sentitemsWorkitems = null;
    this.subscription.push(this.workflowService.getSentItemsWorkItems(workflowWorkItemID).subscribe(data =>this.assignSentItemWorkitem(workflowWorkItemID, data)));
  }

  assignSentItemWorkitem(workitemId, data) {
    this.sentitemsWorkitems = data;
    data = null;
  }

}

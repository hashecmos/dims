import {Component,Output, EventEmitter} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseSharedService} from './../services/browseEvents.shared.service'

@Component({
  selector: 'sort-btn',
  templateUrl: 'app/SharedComponents/sortButton.component.html'
})

export class SortButton{
    
  private filterButtonText:string;
  private filterOptions: string [];
  private currentFilter: string;
  private currentFolderFilter: string;
  private folderFilterSubscription;
  private selected :any;
  private selectedindex : any;
  private sortColumn: string;

  constructor(private route : ActivatedRoute,private sharedService: BrowseSharedService){
    var snapshot = route.snapshot;
    this.currentFilter = 'DESC'
    this.currentFolderFilter = ''
    this.sortColumn = ''
    this.filterButtonText = 'Sort'

    switch (snapshot.routeConfig.path) {
    case "inbox":
      this.filterOptions = ['Subject','Sender Name','Received On','Workflow Deadline']
      this.folderFilterSubscription = this.sharedService.emitFolderfilterForInbox$.subscribe(data => this.applyFolderFilter(data))
      break;
    case "sent-item":
      this.filterOptions = ['Subject','Sent On','Workflow Deadline']
      this.folderFilterSubscription =  this.sharedService.emitFolderfilterForSent$.subscribe(data => this.applyFolderFilter(data))
      break;
    case "archive":
      this.filterOptions = ['Workflow Name','Sender Name','Workflow Deadline']
      this.folderFilterSubscription =  this.sharedService.emitFolderfilterForArchive$.subscribe(data => this.applyFolderFilter(data))
      break;
    case "dailydocument":
      this.filterOptions = ['Document Title','Id','Reference Number','Created By', 'Correspondence Type', 'Date Created']
      this.folderFilterSubscription =  this.sharedService.emitFolderfilterForDailyDocument$.subscribe(data => this.applyFolderFilter(data))
      break;
    }
  }
  @Output() notifyParenttofilter: EventEmitter<any> = new EventEmitter();

  applyFolderFilter(folderFilter){
    this.currentFolderFilter = folderFilter
    this.applyFilter(this.currentFilter)
  }


  applyFilter(opt :any,sortselected:any ,index: any){
    this.selected = sortselected; 
    this.selectedindex = index;
    if(sortselected == "Ascending"){
       this.currentFilter ="ASC"
    }else if(sortselected == "Descending"){
        this.currentFilter ="DESC"
    }
    this.sortColumn = opt;
    
    this.notifyParenttofilter.emit({primaryFilter: this.currentFilter,folderFilter:this.currentFolderFilter,flagFilter: "sort",sortColumn:this.sortColumn,test:"cc"});
  }

  ngOnDestroy(){
        this.folderFilterSubscription.unsubscribe()
  }
    
    select(sortselected:any ,index: any) {
        this.selected = sortselected; 
        this.selectedindex = index;
    }
    isActiveAscending(Ascending : any ,index: any,option:any) {
        
        if(this.selectedindex==index){
            return this.selected === Ascending;
        }
       
    }
      isActiveDescending(Descending :any,index: any,option:any){
        if(this.selectedindex==index){
        return this.selected === Descending;
     }
    }
    
     hideDiv(){
        if(document.getElementsByClassName("errormessage") != undefined){
           document.getElementsByClassName("errormessage")[0].style.display = "none";
          }
    }
}

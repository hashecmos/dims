import {Component,Output, EventEmitter} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseSharedService} from './../services/browseEvents.shared.service'

@Component({
  selector: 'all-btn',
  templateUrl: 'app/SharedComponents/filterButton.component.html'
})

export class FilterButton{
  private filterButtonText:string;
  private filterOptions: string [];
  private currentFilter: string;
  private currentFolderFilter: string;
  private folderFilterSubscription;
  private snapshot : any;

  constructor(private route:ActivatedRoute,private sharedService: BrowseSharedService){
    this.snapshot = route.snapshot;
    this.currentFolderFilter = '';
      
    
    

    switch (this.snapshot.routeConfig.path) {
    case "inbox":
          if(JSON.parse(localStorage.getItem("currentFilter")) == null){
            this.currentFilter = 'All'
        }else {
            this.currentFilter = JSON.parse(localStorage.getItem("currentFilter"));
        } 
        if(JSON.parse(localStorage.getItem("currentFilter")) == null){
           this.filterButtonText = 'All'
        }else {
            this.filterButtonText = JSON.parse(localStorage.getItem("currentFilter"));
        }
      this.filterOptions = ['All','New','Active','CC','Done By Sub','Overdue']
      this.folderFilterSubscription = this.sharedService.emitFolderfilterForInbox$.subscribe(data => this.applyFolderFilter(data))
      break;
    case "sent-item":
       if(JSON.parse(localStorage.getItem("currentSentFilter")) == null){
        this.currentFilter = 'All'
        }else {
            this.currentFilter = JSON.parse(localStorage.getItem("currentSentFilter"));
        } 
        if(JSON.parse(localStorage.getItem("currentSentFilter")) == null){
           this.filterButtonText = 'All'
        }else {
            this.filterButtonText = JSON.parse(localStorage.getItem("currentSentFilter"));
        }
      this.filterOptions = ['All','Done By Me', 'Active','CC']
      this.folderFilterSubscription =  this.sharedService.emitFolderfilterForSent$.subscribe(data => this.applyFolderFilter(data))
      break;
    }
  }
  @Output() notifyParenttofilter: EventEmitter<any> = new EventEmitter();

  applyFolderFilter(folderFilter){
    this.currentFolderFilter = folderFilter
    this.applyFilter(this.currentFilter)
  }


  applyFilter(opt :any){
    if(opt == 'sub'){
      this.filterButtonText = 'Done by sub'
    }else{
      this.currentFilter = opt
      this.filterButtonText = opt
    }
    this.notifyParenttofilter.emit({primaryFilter: this.currentFilter,folderFilter:this.currentFolderFilter,flagFilter: "All"});
  }

  ngOnDestroy(){
        this.folderFilterSubscription.unsubscribe()
  }
}

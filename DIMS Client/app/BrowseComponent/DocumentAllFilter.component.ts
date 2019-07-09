import {Component, Input, Output, EventEmitter} from '@angular/core'
import {Router, ActivatedRoute} from '@angular/router'
import {Subscription } from 'rxjs';

@Component({
  selector: 'all-btn-document',
  templateUrl: 'app/BrowseComponent/DocumentAllFilter.component.html'

})

export class AllBtnDocument {
  public filter_type: string;
  @Output() notifyfiltertoParent: EventEmitter<any> = new EventEmitter();
  constructor(){
    this.filter_type = 'All'
  }
  apply_filter(opt: any) {
    console.log("opt  :: ",opt);
    this.notifyfiltertoParent.emit(opt);
    this.filter_type = opt
  }
}

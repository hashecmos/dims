import {Component, Input, Output, EventEmitter} from '@angular/core'

@Component({
  selector: 'multiple-selection-filter',
  templateUrl: 'app/WorkFlowComponent/Inbox/multiple-selectionFilter.component.html'
})

export class MultipleSelectionFilter{
  @Output() notifyParent: EventEmitter<any> = new EventEmitter();
  @Input() is_all_selected:boolean;
  toggleSelectAll(event:any){
    this.notifyParent.emit(event)
  }

}

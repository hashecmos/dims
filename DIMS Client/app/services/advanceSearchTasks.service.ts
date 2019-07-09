import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class SharedService {

    // Observable string sources
    public emitSearchedTasks:Subject<any> = new Subject<any>();
    public emitSearchedDocuments:Subject<any> = new Subject<any>();
    // Observable string streams
	
	//Added By Rameshwar
	 documentSet : any;


    emitSearchedTasks$ = this.emitSearchedTasks.asObservable();
    emitSearchedDocuments$ = this.emitSearchedDocuments.asObservable();

    // Service message commands
    emitTasks(change: any) {
      this.emitSearchedTasks.next(change);
    }

    emitDocuments(change: any) {
      this.emitSearchedDocuments.next(change);
    }
	//Added By Rameshwar
    setSearchedDocuments(data){
      this.documentSet = data;
    }
//Added By Rameshwar
    getSearchedDocuments(){
      return this.documentSet;
    }

}

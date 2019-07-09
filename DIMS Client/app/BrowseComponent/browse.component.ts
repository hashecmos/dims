//libraries
import {Component, EventEmitter, Output, Input} from '@angular/core'
import {Router, ActivatedRoute } from '@angular/router'
//models
import {Containees} from '../model/containees.model'
//services
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import {BrowseService} from '../services/browse.service'
import {SharedService} from '../services/advanceSearchTasks.service'

@Component({
  selector: 'browse',
  providers: [BrowseService],
  templateUrl: 'app/BrowseComponent/browse.component.html'
})

export class Browse {
  ticks: any;
  private nodes: Node[];
  private selectNode : Node;
  private currentFolderId: string = "";
  private containees: Containees;
  private documents: any = [];
  private paramsSubscription: any;
    
  @Output() selectedNode : EventEmitter<any> = new EventEmitter();
  @Output() changedNode: EventEmitter<any> = new EventEmitter();

  constructor(private browseService: BrowseService, private browseSharedService: BrowseSharedService,
    private activatedRoute: ActivatedRoute, private router: Router,public _router: ActivatedRoute, private sharedSerivce: SharedService) {
    this._router = _router;
    //this.browseService.getfolderTree().subscribe(data => this.construct_tree(data));
  }

  initializeNavigationTree() {
    console.log("initializeNavigationTree");
    //this.browseService.getfolderTree().subscribe(data => this.construct_tree(data))
  }

  refreshAfterCreateFolder(folderId) {
    console.log("refreshAfterCreateFolder");
    //this.browseService.getfolderTree().subscribe(data => this.refreshAfterCreateFolderEmit(data, folderId))
  }

  refreshAfterCreateFolderEmit(data, folderId) {
    console.log("refreshAfterCreateFolderEmit");
    //this.construct_tree(data);
    this.browseSharedService.refreshAfterCreateFolderEmit(folderId)
  }

  setSelectedNode(node) {
    this.selectNode = node;
    localStorage.setItem("SelectedNode", this.selectNode.id);
    console.log("setSelectedNode");
  }

  ngOnChanges() {
    console.log("ng on changes");
  }

  ngOnInit() {
    console.log("ngOninit");
    var context: any = this;
    this.paramsSubscription = this._router.queryParams.subscribe(
      data => {
        context.refresh();
      })

    //this.initializeNavigationTree()
    //this.browseSharedService.changedEvent$.subscribe(folderId => this.updateAndReinitializeNodes(folderId));
   // this.sharedSerivce.emitSearchedDocuments$.subscribe(searchedDocs => this.repelChange(searchedDocs))
    //this.activatedRoute.params.subscribe((param) => this.updateFolderContainees(param))

   //AMZ Changes
    var url = this.router.url
    if (url == '/browse/document-search') {
      this.selectNode = null;
    } else {
    //  localStorage.removeItem('searchTerm')
    } // End AMZ Changes
  }

  repelChange(data) {
    console.log("repel changes");
    this.containees = new Containees()
    this.containees.documents = data
  
  }

 /* updateAndReinitializeNodes(folderId) {
    console.log("update and reinitialize nodes");
    this.initializeNavigationTree();
    this.router.navigate(['/browse/documents', folderId])
    //this.refreshAfterCreateFolder(folderId)
  }*/

  updateFolderContainees(param) {
    console.log("updateFolderContainees");
    this.currentFolderId = param.id
    if (this.currentFolderId != "blank" && this.currentFolderId != null) {
      this.loadFolderContainees()
    }
  }
    
   tickerFunc(tick){
        this.ticks = tick
   }
  loadFolderContainees() {
     console.log("loadFolderContainees");
     if(this.selectNode && this.selectNode.data){
      document.getElementById("displayLoader").style.display="block";
      this.browseService.getContainees(this.selectNode.data).subscribe(data =>{ this.addTreeNodes(data);
        document.getElementById("displayLoader").style.display="none";
      },error=>{
        this.checkerror(error);
        document.getElementById("displayLoader").style.display="none";
      })
     }
    
          
  }
    
  checkerror(error){
      document.getElementById("displayLoader").style.display="none";
  }
  addTreeNodes(data){
    console.log("addTreeNodes");
     this.containees = data;
     //this.constructSubTree(this.containees.folders);
  }
    

    collapseNode(node){
        this.selectNode = node
    }
    
    refresh(){
        
        }
    getContaineesOnNodeSelected(selectedNode){
    this.selectNode = selectedNode;
      document.getElementById("displayLoader").style.display="block";
        this.browseService.getContainees(selectedNode.data).subscribe(data => {
          this.containees = data;
          document.getElementById("displayLoader").style.display="none";
        } ,error=>this.checkerror(error))
    }
  
}

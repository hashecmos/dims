import {Component, Input, Output, OnChanges, EventEmitter, ViewChild} from '@angular/core'
import {Router} from '@angular/router';
//import {TreeComponent} from 'angular2-tree-component';
import {BrowseSharedService} from '../services/browseEvents.shared.service'
import {Node} from '../model/node.model'
import {TreeModule} from 'primeng/primeng';
import { TreeNode } from 'primeng/primeng';
import {MenuModule,MenuItem} from 'primeng/primeng';
import {BrowseService} from '../services/browse.service'
import global = require('./../global.variables')
@Component({
  selector: 'folder-list',
  providers: [],
  templateUrl: 'app/BrowseComponent/FolderList.component.html'
})

export class FolderList {
  @Input() nodes = [];
  @Input() defaultSelected;
  @Output() onNodeChange: EventEmitter<any> = new EventEmitter();
  public active: boolean;
  public selectedNode: Node;
  public selectedContextMemuNode: Node;
  name: any;
  foldername: any;
  items: MenuItem[];
  nodesList = [];
  @Output() onNodeSelected: EventEmitter<any> = new EventEmitter();
  //@ViewChild(expandingTree)
  //@ViewChild(TreeComponent)
  //private tree: TreeComponent;
  constructor(private router: Router,private browseService: BrowseService, private sharedService: BrowseSharedService) {
    //this.nodes = [];
  }

  ngOnInit() {
      this.browseService.getfolderTree('').subscribe(data => this.nodesList= this.getTreeNodes(data,false))
      this.items = [
            {label: 'Create Folder', icon: 'fa-folder', command: (event) => this.viewFile(this.selectedNode)}
        ];

   // this.sharedService.refreshAfterCreateFolderEmitChange$.subscribe(folderId => this.tree.treeModel.getNodeById(folderId).toggleActivated());
  }

  selectDefault(event) {
    console.log("select Default");
    var url = this.router.url
    if (url == '/browse/document-search') {

    } else {
      //this.tree.treeModel.getFirstRoot().toggleActivated()
    }
  }
    viewFile(selectedNode){
        console.log(selectedNode);
    $('#newfolder').modal('show');
   }
    setContextMenuSelectedFolder(event){
        this.selectedNode = event.node;
    }
    setSelectedFolderForContextMenu(event) {

        this.selectedContextMemuNode = event.node
      }
  setSelectedFolder(event) {
    localStorage.removeItem('searchedDocuments') //abdulmalek Change
    console.log("setSelectedFolder start");
    this.selectedNode = event.node
    this.onNodeSelected.emit(this.selectedNode)
    //this.router.navigate(['/browse/documents', this.selectedNode.id])
    console.log("setSelectedFolder end");
    //this.nodes.refreshNodes();
  }

  refreshNodes() {
    //this.tree.treeModel.update()
  }
  /*ngOnChanges() {
    this.refreshNodes()
  }*/
  
  
  getTreeNodes( objs: any, isChildren: boolean): TreeNode[] {
          let _nodes: TreeNode[] = [];
          for ( let i = 0; i < objs.length; i++ ) {
              let tn: TreeNode = {};
              let obj = objs[i];
              if(isChildren){
                  tn.label = obj.path.substr(obj.path.lastIndexOf("/")+1, obj.path.length-1)
                  tn.ParentPath = obj.path.substr(0,obj.path.lastIndexOf("/") )
              }else{
                  tn.label = obj.path.substr(1, obj.path.length-1)
                  tn.ParentPath = global.os_name;
              }
              tn.data = obj.id; //"Documents Folder";
              tn.path = obj.path;
              tn.expandedIcon = "fa-folder-open";
              tn.collapsedIcon = "fa-folder";
              //tn.selectable = false;
              tn.leaf = false;
              tn.children = [];
              if ( obj.documents ) {
                  for ( let i = 0; i < obj.documents.length; i++ ) {
                      let child: TreeNode = {};
                      child.label = obj.documents[i].documentName;
                      child.data = obj.documents[i].documentId; //"Documents Folder";
                      child.icon = "fa-file-word-o";
                      tn.children.push( child );
                  }
              }
              _nodes.push( tn );
          }
          return _nodes;
      }
  loadNode( event ) {
      if ( event.node ) {
          this.browseService.getfolderTree( encodeURIComponent(event.node.path) ).subscribe( result => {
              event.node.children = this.getTreeNodes( result, true );
          } );
      }
  }
  
  
  
  actionNewFolder(folderName: any) {
      var parentfolderId = this.selectedContextMemuNode.data;
      if(parentfolderId == undefined){
          document.getElementById("folderError").style.display = "block";
          return;
      }
      console.log("folderName :: ",folderName);
      //var $input = $(folderName.currentTarget).find('input[type=text]')
       
      /*if (!$input.val().match(/^[0-9a-zA-Z$-)+-.;=~@#!^_`{}\[\]\s]*$/)){
       document.getElementById("errorAlert").style.display = "block";
       //alert('A folder name cannot contain any of the following characters: \/:*?"<>|);
       return;
      }*/
     /* if (!$input.val().match(/^[\u0600-\u06ff]|[\u0750-\u077f]|[\ufb50-\ufc3f]|[\ufe70-\ufefc]*$/)){
        document.getElementById("errorAlert").style.display = "block";
        //alert('A folder name cannot contain any of the following characters: \/:*?"<>|);
        return;
       }  */
       var $input = $(folderName.currentTarget).find('input[type=text]')
       console.log("$input.val() ::: ",$input.val());
       console.log(!$input.val().match(/^[0-9a-zA-Z$-)+-.;=~@#!^_`{}\[\]\s]*$/));
      if (!$input.val().match(/^[0-9a-zA-Z$-)+-.;=~@#!^_`{}\[\]\s]|[\ufb50-\ufc3f]|[\ufe70-\ufefc]*$/)){
       document.getElementById("errorAlert").style.display = "block";
       //alert('A folder name cannot contain any of the following characters: \/:*?"<>|);
       return;
      }  
      this.browseService.newFolder(encodeURIComponent($input.val()), parentfolderId).subscribe(data =>{
         // this.moveToNewFolder(data);
          var event ={};
          event.node = this.selectedContextMemuNode;
         this.loadNode(event);
          
      }, error => this.sharedService.emitMessageChange(error));
      $input.val('');
      if(document.getElementById("errorAlert")){
        document.getElementById("errorAlert").style.display = "none";
        }
      $('#newfolder').modal('hide');
    }
  clearAlert(){
      if(document.getElementById("errorAlert")!=null){
          document.getElementById("errorAlert").style.display = "none";
      }
      document.getElementById("folderError").style.display = "none";
  }
}

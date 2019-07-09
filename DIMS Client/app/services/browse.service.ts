import { Injectable, EventEmitter } from '@angular/core';
import { Http, Headers,Response} from '@angular/http';
import{DocumentResult} from './../model/documentResult.model'
import {DocumentInFolder} from '../model/docFolderContainee.model'
import { Http, Headers, ResponseContentType } from '@angular/http';

import global = require('./../global.variables')


@Injectable()
export class BrowseService {
  tasks_items: any = [];
  base_url: string;
  os_name: string;
  nodes: any = [];
  constructor(private http: Http) {
    this.base_url = global.base_url;
    this.os_name = global.os_name;
  }
  getfolderTree(folderPath) {
    //console.log("NEW in folderPath :: folderPath :: ",folderPath);
    folderPath = folderPath.replace(/&/g, "\~~~"); 
    //folderPath = folderPath.replace(/\\/g, "|")
    //folderPath = folderPath.replace(/\//g, '#');
    //console.log("NEW in folderPath  AFTER:: folderPath :: ",folderPath);
    return this.http.get(`${this.base_url}/FilenetService/getfolderTree?folderPath=${folderPath}/&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  getCurrentFolderDocs(folderPath){
    return this.http.get(`${this.base_url}/FilenetService/getDocumentsInFolder?folderPath=${folderPath}&objectStore=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }


  getContainees(folderId:string,pageNo:string,pageSize:string){
  
    return this.http.get(`${this.base_url}/FilenetService/getContainees?osName=${this.os_name}&folderId=${folderId}&pageNo=${pageNo}&pageSize=${pageSize}&random=${new Date().getTime()}`).map(res => res.json());
  }


  getNodes() {
    return this.nodes;
  }

  nodesChange: EventEmitter<string> = new EventEmitter<string>();

  addNode(nodeName: any) {
    var _newNode: any;
    var _newId: any;
    _newId = this.totalFoldersCount(this.nodes) + 1;
    _newNode = {
      id: _newId,
      name: nodeName,
      link: 'folder/' + _newId,
      children: []
    }
    this.nodes.push(_newNode)
    this.nodesChange.next(_newNode);
    return _newNode;
  }

  moveNode(nodeId: any, targetNode: any) {
    this.pluckNodeFromList(this.nodes, nodeId, targetNode);
    this.nodesChange.next(targetNode);
  }

  pluckNodeFromList(list: any, nodeId: any, targetNode: any) {
    for (let node of list) {
      if (node.id + '' == nodeId + '') {
        node.folder_id = targetNode.id
        targetNode.children.push(node)
        list.splice(list.indexOf(node), 1)
        break
      }
      else if (node.children.length > 0) {
        this.pluckNodeFromList(node.children, nodeId, targetNode)
      }
    }
  }

  getChildrenForNode(nodeId: any, list: any) {
    var children: any;
    for (let node of list) {
      if (node.id + '' == nodeId + '') {
        children = node.children;
        break
      }
      else if (node.children.length > 0) {
        children = this.getChildrenForNode(nodeId, node.children)
        if (children != null) {
          break;
        }
      }
    }
    return children;
  }

  totalFoldersCount(list) {
    var count = 0;
    for (let node of list) {
      count = count + 1;
      if (node.children.length > 0) {
        count = count + this.totalFoldersCount(node.children)
      }
    }
    return count;
  }

  findNode(nodeId: any, list: any) {
    var nodeFromList: any;
    for (let node of list) {
      if (node.id + '' == nodeId + '') {
        nodeFromList = node;
        break
      }
      else if (node.children.length > 0) {
        nodeFromList = this.findNode(nodeId, node.children)
        if (nodeFromList != null) {
          break;
        }
      }
    }
    return nodeFromList;
  }

  documents = [
    { is_checked: false, class_1: '1234', content: "Correspondence", deadline: '20 mins ago', id: 1, doc_title: 'DIMS DOC 1', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'active', visible: false, folder_id: 1, checkin: false },
    { is_checked: false, class_1: '123', content: "Correspondence", deadline: '20 mins ago', id: 2, doc_title: 'DIMS DOC 2', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'cc', visible: false, folder_id: 4, checkin: false },
    { is_checked: false, class_1: '1234', content: "Correspondence", deadline: '20 mins ago', id: 3, doc_title: 'DIMS DOC 3', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'sub', visible: false, folder_id: 1, checkin: false },
    { is_checked: false, class_1: '1235', content: "Correspondence", deadline: '20 mins ago', id: 4, doc_title: 'DIMS DOC 4', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'overdue', visible: false, folder_id: 1, checkin: false },
    { is_checked: false, class_1: '1235', content: "Correspondence", deadline: '20 mins ago', id: 5, doc_title: 'DIMS DOC 5', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'active', visible: false, folder_id: 1, checkin: false },
    { is_checked: false, class_1: '1233', content: "Correspondence", deadline: '20 mins ago', id: 6, doc_title: 'DIMS DOC 6', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'cc', visible: false, folder_id: 4, checkin: false },
    { is_checked: false, class_1: '123', content: "Correspondence", deadline: '20 mins ago', id: 7, doc_title: 'DIMS DOC 7', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'sub', visible: false, folder_id: 4, checkin: false },
    { is_checked: false, class_1: '123', content: "Correspondence", deadline: '20 mins ago', id: 8, doc_title: 'DIMS DOC 8', subject: 'Correspondence', document_id: 'd', dept: 'd', name: '', secondary_subject: 'f', to: 'f', cc: 'f', comment: 'f', m_type: 'inbox', m_status: 'overdue', visible: false, folder_id: 4, checkin: false }
  ]

  getDocuments() {
    return this.documents;
  }

  moveDocument(documentId: any, destinationFolderId: any, currentFolderId:any) {
    return this.http.get(`${this.base_url}/FilenetService/moveDocument?docId=${documentId}&osName=${this.os_name}&destinationFolderId=${destinationFolderId}&currentFolderId=${currentFolderId}&random=${new Date().getTime()}`)
  }



  launchExistingDocument(doc: any) {
    if ((localStorage.getItem("sent_items_1") == null) || (localStorage.getItem("sent_items_1").length == 0)) {
      this.tasks_items.push(doc);
    } else {
      this.tasks_items = JSON.parse(localStorage.getItem("sent_items_1"));
      this.tasks_items.push(doc);
    }
    localStorage.setItem("sent_items_1", JSON.stringify(this.tasks_items));
  }


  getDocVersions() {
    return [{ version: "1.0", user: "Mohmmed Mazda", dateTime: "10-July-2016 11:34 AM", documentName: "Initial correspondence.docx" }];
  }
  getDocFolders() {
    return [{ folderPath: "\DIMS\Jan 2016\Correspondences", filedByUser: "Mohmmed Mazda", filedDateTime: "10-July-2016 11:34 AM" }]
  }
  getDocHistory() {
    return [{ id: '1', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '2', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '3', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '4', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '5', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '6', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '7', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '8', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '9', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '10', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] },
      { id: '11', subject: 'Correspondence', originator: 'Mohammed Nazir', launchDate: '10 Aug 2016 2:15', deadLine: '10 Aug 2016 2:15', senderDetail: [{ sender: 'Hussam Ras', recipient: 'Syed Imran', timeStamp: '17 Aug 2016 1:25PM', actions: 'Read', type: 'Active', actionTaken: 'Syed Imran' }] }

    ];

  }

  SearchDocuments(searchParams) {
    return this.http.post(`${this.base_url}/FilenetSearchService/simpleSearch`,searchParams).map(res => res.json()["resultMapList"].map(function(r) {
      return new DocumentInFolder(r);
    }));
  }

  AdvanceSearchDocuments(searchParams) {
    return this.http.post(`${this.base_url}/FilenetSearchService/simpleSearch`,searchParams).map(res => res.json()["resultMapList"].map(function(r) {
      return new DocumentInFolder(r);
    }));
  }

  docCheckIn(checkInWorkItem: any) {
    return this.http.post(`${this.base_url}/FilenetService/checkInDocument`,checkInWorkItem);
  }

  docCheckOut(workItemID: any) {
    return this.http.get(`${this.base_url}/FilenetService/checkoutDocument?docId=${workItemID}&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  cancelCheckout(documentId: any){
    return this.http.get(`${this.base_url}/FilenetService/cancelCheckoutDocument?docId=${documentId}&osName=${this.os_name}&random=${new Date().getTime()}`).map(res => res.json());
  }

  newFolder(folderName: any,parentfolderId:any) {
   // console.log("NEW in Service :: folderName :: ",folderName);
   // console.log("NEW in Service :: parentfolderId :: ",parentfolderId);
    folderName = folderName.replace(/&/g, "\~~~"); 
   // folderName = folderName.replace(/\\/g, "backslash")
    //folderName = folderName.replace(/\//g, 'forwordslash');
    parentfolderId = parentfolderId.replace(/&/g, "\~~~"); 
  //  parentfolderId = parentfolderId.replace(/\\/g, "|")
   // parentfolderId = parentfolderId.replace(/\//g, '#');
    
   // console.log("NEW in Service :: AFTER ::  folderName :: ",folderName);
   // console.log("NEW in Service :: AFTER ::  parentfolderId :: ",parentfolderId);
    return this.http.get(`${this.base_url}/FilenetService/addFolder?parentPath=${parentfolderId}&folderName=${folderName}&osName=${this.os_name}&random=${new Date().getTime()}`);
  }

  sendDocumentMail(mailData: any){
    var headers = new Headers();
    headers.append("Content-Type", "application/json");
    return this.http.post(`${this.base_url}/FilenetService/emailDocument`,mailData,{ headers: headers });
  }

  loadDocumentsFileProperties(documentIds: any){
    return this.http.post(`${this.base_url}/FilenetService/getDocumentMimeType`,{
      documentIds: documentIds,
      osName: this.os_name
    }).map(res => res.json());
  }
  
  removeDailyDocument(removeDocumentId: any){
      return this.http.post(`${this.base_url}/FilenetService/updateDailyDocument`,{
        itemIds : removeDocumentId,
        osName: this.os_name
        });
  }
    
    downloadMultiDocument(multiDocIds: any){
      return this.http.post(`${this.base_url}/FilenetService/downloadMultiDocuments`,{
        itemIds : multiDocIds,
        osName: this.os_name
        });
  }

  getOutlookAttachmentDocument(documentId: any, osname: any) {
    var url = `${global.base_url}/FilenetService/getOutlookAttachment?docIds=${documentId}&osName=${osname}&random=${new Date().getTime()}`;
    return this.http.get(url);
  }

}

import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class BrowseSharedService {

  // source Subjects
  public openDocSearchModel: Subject<any> = new Subject<any>();
  public emitChangeSource: Subject<any> = new Subject<any>();
  public emitChangeActiveNode: Subject<any> = new Subject<any>();
  public emitBrowseLaunchPopup: Subject<any> = new Subject<any>();
  public emitSideNavReload: Subject<any> = new Subject<any>();
  public emitAddedUsers: Subject<any> = new Subject<any>();
  public emitFolderfilterForInbox: Subject<any> = new Subject<any>();
  public emitFolderfilterForSent: Subject<any> = new Subject<any>();
  public emitFolderfilterForArchive: Subject<any> = new Subject<any>();
  public refreshAfterCreateFolderEmitSource: Subject<any> = new Subject<any>();
  public emitSearchedDocsFromRepo: Subject<any> = new Subject<any>();
  public emitMessage: Subject<any> = new Subject<any>();
  public emitSortfilterForInboxItems: Subject<any> = new Subject<any>();
    public emitFolderfilterForDailyDocument: Subject<any> = new Subject<any>();
  // Observable streams
  openDocSearchModel$ = this.openDocSearchModel.asObservable();
  refreshAfterCreateFolderEmitChange$ = this.refreshAfterCreateFolderEmitSource.asObservable();
  changedEvent$ = this.emitChangeSource.asObservable();
  emitBrowseLaunchPopup$ = this.emitBrowseLaunchPopup.asObservable();
  emitSideNavReload$ = this.emitSideNavReload.asObservable();
  emitSelectedUsers$ = this.emitAddedUsers.asObservable();
  emitFolderfilterForInbox$ = this.emitFolderfilterForInbox.asObservable();
  emitFolderfilterForSent$ = this.emitFolderfilterForSent.asObservable();
  emitFolderfilterForArchive$ = this.emitFolderfilterForArchive.asObservable();
  emitSearchedDocsFromRepo$ = this.emitSearchedDocsFromRepo.asObservable();
  emitchangeEvent$ = this.emitMessage.asObservable();
  emitSortfilterForInboxItems$ = this.emitSortfilterForInboxItems.asObservable();
    emitFolderfilterForDailyDocument$ = this.emitFolderfilterForDailyDocument.asObservable();
  // Service Methods
  emitSearchedDocsFromRepository(docs) {
    this.emitSearchedDocsFromRepo.next(docs)
  }

  emitMessageChange(change: any) {
    this.emitMessage.next(change);
  }

  openDocSearchModelPopup(msg: any) {
    this.openDocSearchModel.next(msg);
  }
  refreshAfterCreateFolderEmit(folderId) {
    this.refreshAfterCreateFolderEmitSource.next(folderId);
  }

  emitChange(change: any) {
    this.emitChangeSource.next(change);
  }

  emitBrowseLaunchPopupOpen(doc: any) {
    this.emitBrowseLaunchPopup.next(doc);
  }

  emitSideNavigationReload(folderFor) {
    this.emitSideNavReload.next(folderFor);
  }

  emitSelectedUsers(users) {
    this.emitAddedUsers.next(users);
  }

  emitFolderFilterForInboxItems(folderFilter) {
    this.emitFolderfilterForInbox.next(folderFilter);
  }

  emitFolderFilterForSentItems(folderFilter) {
    this.emitFolderfilterForSent.next(folderFilter);
  }

  emitFolderFilterForArchiveItems(folderFilter) {
    this.emitFolderfilterForArchive.next(folderFilter);
  }
    
  emitSortFilterForInboxItems(folderFilter){
      this.emitSortfilterForInboxItems.next(folderFilter);
  }
    emitFolderFilterForDailyDocumentItems(folderFilter){
      this.emitFolderfilterForDailyDocument.next(folderFilter);
  }
}

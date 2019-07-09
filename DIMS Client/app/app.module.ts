import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpModule } from '@angular/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {Ng2PaginationModule} from 'ng2-pagination';
import { AppComponent }  from './app.component';
import {HighlightPipe} from './pipes/highlight.pipe'
import {AuthComponent} from './loginComponent/auth.component';
import {WorkflowComponent} from './WorkFlowComponent/work_flow.component';
import {Header} from './SharedComponents/headerComponent/header.component'
import {ModuleSelection} from './SharedComponents/module_selection.component'
import {DocumentInfo} from './SharedComponents/document_info.component'
import {DocumentLaunchInfo} from './SharedComponents/documentLaunchinfo.component'
import {DocumentInfoforactionbutton} from './SharedComponents/document_info_for_actionbutton.component'
import {DocumentMailer} from './SharedComponents/document_mailer.component'
import {DocumentLink} from './SharedComponents/documentLink.component'
import {PrintReport} from './SharedComponents/printReport.component'
import {DropdownModule} from 'primeng/primeng';
import {MultipleSelectionFilter} from './WorkFlowComponent/Inbox/multiple-selection-filter.component'
import {SupportBtn} from './SharedComponents/footerComponent/support_btn.component'
import {SideNavigation} from './WorkFlowComponent/SideNavigation.component'
import {DelegateUser} from './SharedComponents/delegatedUser.component'
import {InboxList} from './WorkFlowComponent/Inbox/InboxList.component'
import {Reload} from './WorkFlowComponent/Inbox/Reload.component'
import {ActionsButton} from './SharedComponents/actionsButton.component'
import {ActionBtnArchive} from './WorkFlowComponent/Archive/ArchiveActionFilter.component'
import {AdvanceDocSearchModal} from './SharedComponents/advanceDocSearchModal.component'
import {Report} from './ReportComponent/report.component'
import {ReportSideBar} from './ReportComponent/reportSideBar.component'
import {PendingWorkFlows} from './ReportComponent/pendingWorkFlows.component'
import {PendingWorkFlowSpecificHistory} from './ReportComponent/pendingWorkFlowSpecificHistory.component'
import {PendingWorkFlowFullHistory} from './ReportComponent/pendingWorkFlowFullHistory.component'
import {DocumentsScanned} from './ReportComponent/documentsScanned.component'
import {ManageDelegateUserList} from './SharedComponents/settings/manageDelegateUserList.component'
import {FilterButton} from './SharedComponents/filterButton.component'
import {SortButton} from './SharedComponents/sortButton.component'
import {ManageUserList} from './SharedComponents/settings/manageUserList.component'
import {NewFolder} from './SharedComponents/newFolder.component'
import {AuthGuard} from './loginComponent/canactivate.component'
import {SentLayoutComponent} from './WorkFlowComponent/SentItems/sentitems.component'
import {ArchiveLayoutComponent} from './WorkFlowComponent/Archive/archive.component'
import {DailyDocumentLayoutComponent} from './WorkFlowComponent/DailyDocument/dailydocument.component'
import {TaskDetails} from './WorkFlowComponent/Inbox/taskDetails.component'
import {ModalModule} from "ng2-modal";

import {NgbModule} from '@ng-bootstrap/ng-bootstrap'
import { PopoverModule } from 'ng2-popover';
import {Browse} from './BrowseComponent/browse.component'
import {FolderList} from './BrowseComponent/FolderList.component'
import {Documents} from './BrowseComponent/documents.component'
import {ActionBtnDocument} from './BrowseComponent/DocumentActionFilter.component'
import {ActionBtnDailyDocument} from './WorkFlowComponent/DailyDocument/DailyDocumentActionFilter.component'

import {LaunchDocument} from './BrowseComponent/LaunchDocument.component'
//import { TreeModule } from 'angular2-tree-component';
import {AllBtnDocument} from './BrowseComponent/DocumentAllFilter.component'
import {SettingsButton} from './SharedComponents/settings/settingsButton.component'

import {TaskSearch} from './SharedComponents/task_search.component'
import {DirectorateAccordion} from './SharedComponents/directorateAccordion.component'
import {UserSelect} from './SharedComponents/userSelection.component'
import {unAutherizedAccess} from './SharedComponents/unAutherizedAccess.component'



import { Ng2AutoCompleteModule } from 'ng2-auto-complete'
import {AccordionModule} from "ng2-accordion";
import { MyDatePickerModule } from 'mydatepicker';
import { MyDateRangePickerModule } from 'mydaterangepicker';
import {TreeModule,TreeNode,EditorModule,SharedModule} from 'primeng/primeng';
import {SharedService} from './services/advanceSearchTasks.service'
import {BrowseSharedService} from './services/browseEvents.shared.service'
import {UserService} from './services/user.service'
import {MultiSelectModule,ChartModule} from 'primeng/primeng';
import {ContextMenuModule,MenuModule,ChipsModule} from 'primeng/primeng';

@NgModule({
  providers: [AuthGuard, SharedService, BrowseSharedService, UserService],
  imports: [BrowserModule,
    AccordionModule,
    Ng2PaginationModule,
    HttpModule,
    FormsModule,
    ModalModule,
    NgbModule.forRoot(),
    ReactiveFormsModule,
    PopoverModule, Ng2AutoCompleteModule,TreeModule,EditorModule,
    SharedModule,MultiSelectModule,ChartModule,ContextMenuModule,MenuModule,ChipsModule,
    MyDatePickerModule,MyDateRangePickerModule,DropdownModule,
    RouterModule.forRoot([
      { path: 'my-login', component: AuthComponent },
      { path: 'access-denied', component: unAutherizedAccess },
      { path: '', component: AuthComponent },
      {
        path: 'work-flow', component: WorkflowComponent, canActivate: [AuthGuard],
        children: [
          {
            path: 'inbox',
            component: InboxList

          },
          {
            path: 'reload',
            component: Reload

          },
          {
            path: 'sent-item',
            component: SentLayoutComponent
          },
          {
            path: 'archive',
            component: ArchiveLayoutComponent
          },
          {
            path: 'dailydocument',
            component: DailyDocumentLayoutComponent
          },
          {
            path: 'task-search/:id',
            component: TaskSearch
          },
          {
            path: 'inbox/task-details/:id',
            component: TaskDetails
          },
          {
            path: 'task-search',
            component: TaskSearch
          },
          {
            path: 'sent-item/task-details/:id',
            component: TaskDetails
          },
          {
            path: 'archive/task-details/:id',
            component: TaskDetails
          },
        ]
      },

      { path: 'browse/documents/:id', component: Browse, canActivate: [AuthGuard] },
      { path: 'browse/document-search', component: Browse, canActivate: [AuthGuard] },
      { path: 'settings/user-list', component: ManageUserList },
      { path: 'settings/delegate-user-list', component: ManageDelegateUserList },
      { path: 'reports/report-list', component: Report },
      { path: 'reports/pending-work-flow-specific-history', component: PendingWorkFlowSpecificHistory },
      { path: 'reports/pending-work-flow-full-history', component: PendingWorkFlowFullHistory },
      { path: 'reports/document-scanned', component: DocumentsScanned },
      { path: 'reports/pending-work-flow', component: PendingWorkFlows }
    ], { useHash: true })],

  declarations: [AppComponent, HighlightPipe, AuthComponent, WorkflowComponent, Header, ManageUserList, ManageDelegateUserList, Report, ReportSideBar,
    ModuleSelection, MultipleSelectionFilter, SupportBtn, DelegateUser, SideNavigation, PendingWorkFlows, PendingWorkFlowSpecificHistory, PendingWorkFlowFullHistory,
    InboxList, Reload, FilterButton, SortButton, ActionsButton, SentLayoutComponent, ArchiveLayoutComponent,DailyDocumentLayoutComponent, TaskDetails, DocumentsScanned,
    ActionBtnArchive, Browse, Documents, FolderList, ActionBtnDocument,ActionBtnDailyDocument, NewFolder, DirectorateAccordion,
    LaunchDocument, AllBtnDocument, TaskSearch, UserSelect,unAutherizedAccess, SettingsButton,
    DocumentInfo, DocumentLaunchInfo, DocumentInfoforactionbutton, AdvanceDocSearchModal, DocumentMailer, DocumentLink, PrintReport],

  bootstrap: [AppComponent]
})
export class AppModule { }

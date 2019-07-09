import {Component, Output, EventEmitter} from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import {BrowseService} from '../services/browse.service'
import {ReportsService} from '../services/reports.service'
import {WorkflowStatistics} from '../model/workFlowStatistics.model'
import {IMyOptions, IMyDateModel, IMyDate, MyDatePicker} from 'mydatepicker';

import global = require('../global.variables')

@Component({
  selector: 'print-report',
  providers: [BrowseService, ReportsService],
  templateUrl: 'app/SharedComponents/printReport.component.html'
})

export class PrintReport {
  printReportDoc(printSectionId: string) {
    let popupWindow
    let innerContents = document.getElementById(printSectionId).innerHTML;
    popupWindow = window.open('', '_blank', 'width=800,height=1000,scrollbars=no,menubar=no,toolbar=no,location=no,status=no,titlebar=no');
    popupWindow.document.open();
    popupWindow.document.write('<html><head>  <meta charset="UTF-8">  <meta name="viewport" content="width=device-width, initial-scale=1"><link rel="stylesheet" type="text/css" href="../../styles.css" /><link rel="stylesheet" href="../../bootstrap/css/bootstrap.css"><link rel="stylesheet" href="../../css/font-awesome.min.css">  <script src="../../bootstrap/js/bootstrap.min.js" type="text/javascript"></script> <script src="../../bootstrap/js/bootstrap.min.js" type="text/javascript"></script> </head><body onload="window.print()">' + innerContents + '</body></html>');
    popupWindow.document.close();
  }

}

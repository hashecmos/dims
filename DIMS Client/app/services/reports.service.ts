import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import { UserService } from './user.service';
import { ReportStatistics, StatisticsList } from '../model/reportStatistics.model';
import {Observable} from 'rxjs/Rx';


import global = require('./../global.variables');

@Injectable()
export class ReportsService {
  reportStatistics: any;
  statisticsList: any;
  base_url: string;
  constructor(private http: Http) {
    this.base_url = global.base_url
  }
  getStatisticsReportList() {
    this.reportStatistics = new ReportStatistics();
    this.reportStatistics.statisticsList = [];
    this.reportStatistics.totalWorkFlows = 40;
    this.reportStatistics.newWorkFlows = 40;
    this.reportStatistics.activeWorkFlows = 40;
    this.reportStatistics.overdueWorkFlows = 40;
    this.statisticsList = new StatisticsList();
    this.statisticsList.division = "Enterprice Projects"
    this.statisticsList.all = 1232;
    this.statisticsList.newWorks = 4234;
    this.statisticsList.active = 542;
    this.statisticsList.overdue = 1231;
    this.reportStatistics.statisticsList.push(this.statisticsList)
    this.statisticsList = new StatisticsList();
    this.statisticsList.division = "Aasim Azmi"
    this.statisticsList.all = 1232;
    this.statisticsList.newWorks = 4234;
    this.statisticsList.active = 542;
    this.statisticsList.overdue = 1231;
    this.reportStatistics.statisticsList.push(this.statisticsList)
    this.statisticsList = new StatisticsList();
    this.statisticsList.division = "Abdul Malik - Allapichai"
    this.statisticsList.all = 1232;
    this.statisticsList.newWorks = 4234;
    this.statisticsList.active = 542;
    this.statisticsList.overdue = 1231;
    this.reportStatistics.statisticsList.push(this.statisticsList)
    this.statisticsList = new StatisticsList();
    this.statisticsList.division = "Ajith Kumar"
    this.statisticsList.all = 1232;
    this.statisticsList.newWorks = 4234;
    this.statisticsList.active = 542;
    this.statisticsList.overdue = 1231;
    this.reportStatistics.statisticsList.push(this.statisticsList)
    this.statisticsList = new StatisticsList();
    this.statisticsList.division = "Bassam"
    this.statisticsList.all = 1232;
    this.statisticsList.newWorks = 4234;
    this.statisticsList.active = 542;
    this.statisticsList.overdue = 1231;
    this.reportStatistics.statisticsList.push(this.statisticsList)
    return this.reportStatistics;
  }
      getWorkflowStatistics(loginName:any, dept:any, div:any, from:any, to:any){
        return this.http.get(`${this.base_url}/WorkflowSearchService/workflowStatistics?userLogin=${loginName}&dpt=${dept}&div=${div}&from=${from}&to=${to}`).map(res => res.json());
        //return this.http.post(`${this.base_url}/WorkflowSearchService/workflowStatistics?userLogin=${id}`,report).map(res => res.json());
    }
    
    getPendingWorkflowsFullHistory(sender:any, status:any, recipient:any, fromDate:any, toDate:any) {
        return this.http.get(`${this.base_url}/WorkflowSearchService/pendingWorkflowsFullHistory?sender=${sender}&status=${status}&recipient=${recipient}&from=${fromDate}&to=${toDate}`).map(res => res.json());
        //return this.http.post(`${this.base_url}/WorkflowSearchService/pendingWorkflowsFullHistory?workitemsFullHistory=${workitemsFullHistory}`).map(res => res.json());
    }
    
    getPendingWorkflowsSpecificHistory(sender:any, status:any, recipient:any, fromDate:any, toDate:any) {
        return this.http.get(`${this.base_url}/WorkflowSearchService/pendingWorkflowsSpecificHistory?sender=${sender}&status=${status}&recipient=${recipient}&from=${fromDate}&to=${toDate}`).map(res => res.json());
    }
    getPendingWorkflows(sender:any, status:any, overdue:any, recipient:any, fromDate:any, toDate:any) {
        //console.log(sender,status, overdue, recipient,fromDate, toDate);
        return this.http.get(`${this.base_url}/WorkflowSearchService/pendingWorkflows?sender=${sender}&status=${status}&overdue=${overdue}&recipient=${recipient}&from=${fromDate}&to=${toDate}`).map(res => res.json());
    }
    getdocumentsScanned(dept:any, div:any, user:any, from:any, to:any) {
        
        return this.http.get(`${this.base_url}/FilenetService/documentsScanned?dpt=${dept}&div=${div}&user=${user}&from=${from}&to=${to}`).map(res => res.json());
    
    }
    exportWorkflowStatistics(loginName:any, dept:any, div:any, from1:any, to:any,selectedOption : any){
        window.location.assign(this.base_url+"/WorkflowSearchService/exporWorkflowStatistics?userLogin="+loginName+"&dpt="+dept+"&div="+div+"&from="+from1+"&to="+to+"&exportType="+selectedOption);
         //return this.http.get(`${this.base_url}/WorkflowSearchService/exporWorkflowStatistics?userLogin=${loginName}&dpt=${dept}&div=${div}&from=${from}&to=${to}&exportType=${selectedOption}`);
    }
    //return this.http.post(`${this.base_url}/WorkflowSearchService/canvasImage1`, documentData).map(res => res);
    canvasImage(documentData: any, selectedOption : any): Observable<Object[]> {
        return Observable.create(observer => {

            let xhr = new XMLHttpRequest();

            xhr.open('POST', this.base_url+'/WorkflowSearchService/canvasImage1', true);
            xhr.responseType='arraybuffer';
            
            xhr.send(documentData);

            xhr.onload = function () {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                         var contentType = '';
                        if(selectedOption == 'PDF'){
                            contentType = 'application/pdf';
                        }else if (selectedOption == 'Excel'){
                            contentType = 'application/vnd.ms-excel';
                        }
                       
                        //var contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
                        //var contentType = 'application/pdf';
                        var blob = new Blob([xhr.response], { type: contentType });
                        observer.next(blob);
                        observer.complete();
                    } else {
                        observer.error(xhr.response);
                    }
                }
            }
        });
    }
}

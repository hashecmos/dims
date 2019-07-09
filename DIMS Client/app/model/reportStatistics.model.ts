export class ReportStatistics {
  public totalWorkFlows: string;
  public newWorkFlows: string;
  public activeWorkFlows: string;
  public overdueWorkFlows: string;
  public statisticsList: StatisticsList[];
}

export class StatisticsList {
  public division: string;
  public all: string;
  public newWorks: string;
  public active: string;
  public overdue: string;
}

export class Directorate{
  public employeeDirectorateCode: string;
  public employeeDirectorate: string;
  public users: any = [];
  public departments: any = [];
  constructor(data){
  	this.employeeDirectorateCode = data.employeeDirectorateCode;
  	this.employeeDirectorate = data.employeeDirectorate;
  }
}

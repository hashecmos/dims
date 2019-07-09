export class User {
	public admin: boolean;
  public employeeID: number;
  public employeeName: string;
  public employeeLogin: string;
  public employeeEmail: string;
  public employeeDesignation: string
  constructor(data){
  	this.admin = data.admin;
	  this.employeeID = data.employeeID;
	  this.employeeName = data.employeeName;
	  this.employeeLogin = data.employeeLogin;
	  this.employeeEmail = data.employeeEmail;
	  this.employeeDesignation = data.employeeDesignation;
  }
}

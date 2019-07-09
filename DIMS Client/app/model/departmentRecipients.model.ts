
export class Department {
	public departmentCode: number;
	public department: string;
  public users: any = [];
  public divisions: any = [];
  constructor(data){
  	this.departmentCode = data.departmentCode;
		this.department = data.department;
  }
}

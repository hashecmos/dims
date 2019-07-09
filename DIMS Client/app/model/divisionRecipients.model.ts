export class Division {
	public divisionCode: number;
	public division: string;
  public users: any = [];
  constructor(data){
  	this.divisionCode = data.empDivisionCode;
		this.division = data.empDivision;
  }
}

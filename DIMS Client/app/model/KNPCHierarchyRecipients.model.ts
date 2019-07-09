
export class KNPCHierarchy {

    public departmentCode: string;
    public department: string;
    public oldDimsDeptCode : string;
    public users: any = [];
    public departments: any = [];
    
    constructor(data){
        this.oldDimsDeptCode = data.oldDimsDeptCode;
        this.departmentCode = data.departmentCode;
        this.department = data.department;
    }
}

export class FolderFiledIn {
	public createdBy: string;
	public dateCreated: string;
	public folderPath: string;
	public id: string;
	public path: string;
	public symbolicName: string;
	public type: string;
	constructor(attrs: any){
		for(let attr in attrs){
			this[attr] = attrs[attr];
		}
	}
}

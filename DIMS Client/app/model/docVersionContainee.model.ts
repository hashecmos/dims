

export class DocumentVersion {
	public createdBy: string;
	public dateCreated: string;
	public hasLinks: boolean;
	public hasMultipleAttachments: boolean;
	public id: string;
	public majorVersion: number;
	public reserved: boolean;
	public size: number;
	constructor(attrs: any){
		for(let attr in attrs){
			this[attr] = attrs[attr];
		}
	}
}

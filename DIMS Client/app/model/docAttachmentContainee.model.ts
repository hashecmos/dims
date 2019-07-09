
export class DocumentAttachment{
	public elementSequenceNo: string;
	public fileName: string;
	public mimeType: string;
	constructor(attrs: any){
		for(let attr in attrs){
			this[attr] = attrs[attr];
		}
	}
}

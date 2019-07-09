export class DocCheckIn {
  public attributes: any = [];
  constructor(data: any) {
    for (let attr in data) {
      var prop = {};
      prop[data[attr].propertyName] = data[attr].propertyValue;
      this.attributes.push(prop);
    }
  }

  getPropertyForIndex(index: number) {
    return (<any> Object).keys(this.attributes[index])
  }

  getValueForIndex(index: number) {
    return this.attributes[index][this.getPropertyForIndex(index)]
  }
}

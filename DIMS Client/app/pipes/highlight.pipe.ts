import {PipeTransform, Pipe} from '@angular/core';

@Pipe({ name: 'highlight' })

export class HighlightPipe implements PipeTransform {
    
  transform(text: string, searchTerm): string {
  if(!text)    
    return "";
 //console.log('Inside pipe transform ::text:',text);
 //console.log('Inside pipe transform :searchTerm:',searchTerm);
     // console.log(decodeURIComponent(searchTerm));
   //   var search = searchTerm.replace(/%20/g, " ");
         var search = decodeURIComponent(searchTerm);
  //console.log('Inside pipe transform :replaced:',search);          
    var pattern = search.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
 //console.log('Inside pipe transform :pattern:',pattern);     
    pattern = pattern.split(' ').filter((t) => {
      return t.length > 0;
    }).join('|');
 //console.log('Inside pipe transform :modified:pattern:',pattern); 
     // pattern = '11/24'; 
  //console.log('Inside pipe transform :222:',pattern);    
    var regex = new RegExp(pattern, 'gi');
//console.log('Inside pipe transform :::',regex);
  //var re = new RegExp(search, 'gi');  
  //console.log('re ::::',re);   
//return search ? text.replace(new RegExp(search, 'i'), `<span class="highlight">${search}</span>`) : text;      
 return search ? text.replace(regex, (match) => `<span class="fontHightLight">${match}</span>`) : text;
  }
}
import { Injectable, EventEmitter } from '@angular/core';
import { Http, Headers, Response} from '@angular/http';
import global = require('./../global.variables')

@Injectable()
export class FooterService {
  base_url: string;

  constructor(private http: Http, ) {
    this.base_url = global.base_url;

  }
  getUserManual(): any {
    return this.http.get(`${this.base_url}/FooterService/getUserManual`).map(res => res.json());
  }

  getPresentations(): any {
    return this.http.get(`${this.base_url}/FooterService/getPresentations`).map(res => res.json());
  }

  getVideos(): any {
    return this.http.get(`${this.base_url}/FooterService/getVideos`).map(res => res.json());
  }

  getAudios(): any {
    return this.http.get(`${this.base_url}/FooterService/getAudios`).map(res => res.json());
  }

  getToCc(): any {
    return this.http.get(`${this.base_url}/FooterService/getToCc`).map(res => res.json());
  }

}

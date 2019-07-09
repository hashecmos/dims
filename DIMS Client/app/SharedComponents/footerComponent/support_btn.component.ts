import {Component} from '@angular/core'
import {FooterService} from './../../services/footer.service'
@Component({
  selector: 'misc-support-buttons',
  providers: [FooterService],
  templateUrl: 'app/SharedComponents/footerComponent/support_btn.component.html'
})

export class SupportBtn {
  private usermanual: any;
  private presentation: any;
  private videos: any;
  private audios: any;
  private tocc: any;

  constructor(private FooterService: FooterService) {

  }

  getUserManual() {
    this.FooterService.getUserManual().subscribe(data => this.usermanual = data);
  }

  getPresentations() {
    this.FooterService.getPresentations().subscribe(data => this.presentation = data);
  }

  getVideos() {
    this.FooterService.getVideos().subscribe(data => this.videos = data);
  }

  getAudios() {
    this.FooterService.getAudios().subscribe(data => this.audios = data);
  }

  getToCc() {
    this.FooterService.getToCc().subscribe(data => this.tocc = data);
  }

}

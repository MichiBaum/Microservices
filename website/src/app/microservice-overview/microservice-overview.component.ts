import { Component } from '@angular/core';
import {environment} from "../../environments/environment";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-microservice-overview',
  standalone: true,
  imports: [],
  templateUrl: './microservice-overview.component.html',
  styleUrl: './microservice-overview.component.scss'
})
export class MicroserviceOverviewComponent {

  public adminServiceUrl: SafeResourceUrl;

  constructor(
    private readonly sanitizer: DomSanitizer
  ) {
    this.adminServiceUrl = this.sanitizeUrl(environment.adminService);
  }

  private sanitizeUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}

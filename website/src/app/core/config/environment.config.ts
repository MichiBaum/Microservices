import {inject, Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {DOCUMENT} from "@angular/common";

@Injectable({ providedIn: 'root' })
export class EnvironmentConfig {
  private readonly document = inject(DOCUMENT);
  hostname: string;

  constructor() {
    this.hostname = this.document.location.hostname;
  }

  baseUrl(): string {
    return environment.base_url(this.hostname);
  }

  fe_images(): string {
    return environment.fe_images(this.hostname);
  }

  authenticationService(): string {
    return environment.authenticationService(this.hostname);
  }

  chessService(): string {
    return environment.chessService(this.hostname);
  }

  adminService(): string {
    return environment.adminService(this.hostname);
  }

  fitnessService(): string {
    return environment.fitnessService(this.hostname);
  }

  musicService(): string {
    return environment.musicService(this.hostname);
  }

}

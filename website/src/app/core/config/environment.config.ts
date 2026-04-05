import {inject, Injectable, DOCUMENT} from "@angular/core";
import {environment} from "../../../environments/environment";


@Injectable({ providedIn: 'root' })
export class EnvironmentConfig {
  private readonly document = inject(DOCUMENT);

  hostname: string;
  protocol: string;

  constructor() {
    this.hostname = this.document.location.hostname;
    this.protocol = this.document.location.protocol;
  }

  baseUrl(): string {
    return environment.base_url(this.protocol, this.hostname);
  }

  fe_images(): string {
    return environment.fe_images(this.protocol, this.hostname);
  }

  authenticationService(): string {
    return environment.authenticationService(this.protocol, this.hostname);
  }

  chessService(): string {
    return environment.chessService(this.protocol, this.hostname);
  }

  adminService(): string {
    return environment.adminService(this.protocol, this.hostname);
  }

  fitnessService(): string {
    return environment.fitnessService(this.protocol, this.hostname);
  }

  musicService(): string {
    return environment.musicService(this.protocol, this.hostname);
  }

  jaeger(): string {
      return environment.jaegerService(this.protocol, this.hostname);
  }

  grafana(): string {
      return environment.grafanaService(this.protocol, this.hostname);
  }

  prometheus(): string {
      return environment.prometheusService(this.protocol, this.hostname);
  }

}

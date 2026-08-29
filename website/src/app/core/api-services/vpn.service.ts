import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {EnvironmentConfig} from "../config/environment.config";
import {DeploymentDto} from "../models/vpn/vpn.model";

@Injectable({providedIn: 'root'})
export class VpnService {
  private readonly http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);

  createWireguard(): Observable<DeploymentDto> {
    return this.http.post<DeploymentDto>(`${this.environment.vpnService()}/wireguard`, {});
  }

  getWireguardConfig(): Observable<string> {
    return this.http.get(`${this.environment.vpnService()}/wireguard/config`, {responseType: 'text'});
  }

  deleteWireguard(): Observable<void> {
    return this.http.delete<void>(`${this.environment.vpnService()}/wireguard`);
  }
}

export { VpnService as WireguardService };

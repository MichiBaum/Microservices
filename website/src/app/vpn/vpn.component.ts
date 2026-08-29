import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from "@angular/common";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {Tag} from "primeng/tag";
import {Textarea} from "primeng/textarea";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {
  faCheck,
  faCopy,
  faDownload,
  faPlus,
  faShieldHalved,
  faTrash,
  faFileCode
} from "@fortawesome/free-solid-svg-icons";
import {VpnService} from "../core/api-services/vpn.service";
import {DeploymentDto} from "../core/models/vpn/vpn.model";
import {UserInfoService} from "../core/services/user-info.service";
import {UserConfirmationService} from "../core/services/user-confirmation.service";
import {rxResource, toObservable, toSignal} from "@angular/core/rxjs-interop";
import {of, switchMap} from "rxjs";

@Component({
  selector: 'app-vpn',
  imports: [
    CommonModule,
    TranslateModule,
    Button,
    Card,
    Tag,
    Textarea,
    FaIconComponent
  ],
  templateUrl: './vpn.component.html',
  styleUrl: './vpn.component.css'
})
export class VpnComponent {
  private readonly vpnService = inject(VpnService);
  private readonly userInfoService = inject(UserInfoService);
  private readonly userConfirmationService = inject(UserConfirmationService);
  private readonly translateService = inject(TranslateService);

  deployment = rxResource({
    stream: () => this.vpnService.getWireguard(),
    defaultValue: undefined
  })
  private readonly config$ = computed(() => {
    const dep = this.deployment.value();
    if (!dep) {
      return of('');
    }
    return this.vpnService.getWireguardConfig();
  });

  config = toSignal(
    toObservable(this.config$).pipe(
      switchMap(obs => obs)
    ),
    { initialValue: '' }
  );


  protected readonly faPlus = faPlus;
  protected readonly faTrash = faTrash;
  protected readonly faDownload = faDownload;
  protected readonly faCopy = faCopy;
  protected readonly faShieldHalved = faShieldHalved;

  createWireguardService(): void {
    this.vpnService.createWireguard().subscribe({
      next: (dto) => {
        this.deployment.reload();
        if (this.deployment.value !== undefined) {
          this.userInfoService.info(
            this.translateService.instant("vpn.create.success"),
            dto.name ? `${dto.name} (${dto.namespace})` : ""
          );
        }
      },
      error: () => {
        this.userInfoService.error(
          this.translateService.instant("vpn.create.error"),
          ""
        );
      }
    });
  }

  confirmDeleteWireguardService(): void {
    this.userConfirmationService.deleteConfirm({
      header: this.translateService.instant("vpn.delete.confirm-title"),
      message: this.translateService.instant("vpn.delete.confirm-message"),
      accept: () => {
        this.deleteWireguardService();
      }
    });
  }

  deleteWireguardService(): void {
    this.vpnService.deleteWireguard().subscribe({
      next: () => {
        this.deployment.reload();
        this.userInfoService.info(
          this.translateService.instant("vpn.delete.success"),
          ""
        );
      },
      error: () => {
        this.userInfoService.error(
          this.translateService.instant("vpn.delete.error"),
          ""
        );
      }
    });
  }

  downloadConfig(): void {
    const text = this.config();
    if (text) {
      const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'wireguard.conf';
      link.click();
      URL.revokeObjectURL(url);
    }
  }
}

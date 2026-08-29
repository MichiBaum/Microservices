import {Component, inject, signal} from '@angular/core';
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

  readonly deployment = signal<DeploymentDto | null>(null);
  readonly config = signal<string | null>(null);
  readonly isLoadingCreate = signal<boolean>(false);
  readonly isLoadingDelete = signal<boolean>(false);
  readonly isLoadingConfig = signal<boolean>(false);
  readonly isCopied = signal<boolean>(false);

  protected readonly faPlus = faPlus;
  protected readonly faTrash = faTrash;
  protected readonly faDownload = faDownload;
  protected readonly faCopy = faCopy;
  protected readonly faCheck = faCheck;
  protected readonly faShieldHalved = faShieldHalved;
  protected readonly faFileCode = faFileCode;

  createWireguardService(): void {
    this.isLoadingCreate.set(true);
    this.vpnService.createWireguard().subscribe({
      next: (dto) => {
        this.deployment.set(dto);
        this.isLoadingCreate.set(false);
        this.userInfoService.info(
          this.translateService.instant("vpn.create.success"),
          dto.name ? `${dto.name} (${dto.namespace})` : ""
        );
      },
      error: () => {
        this.isLoadingCreate.set(false);
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
    this.isLoadingDelete.set(true);
    this.vpnService.deleteWireguard().subscribe({
      next: () => {
        this.deployment.set(null);
        this.config.set(null);
        this.isLoadingDelete.set(false);
        this.userInfoService.info(
          this.translateService.instant("vpn.delete.success"),
          ""
        );
      },
      error: () => {
        this.isLoadingDelete.set(false);
        this.userInfoService.error(
          this.translateService.instant("vpn.delete.error"),
          ""
        );
      }
    });
  }

  getConfig(): void {
    this.isLoadingConfig.set(true);
    this.vpnService.getWireguardConfig().subscribe({
      next: (cfg) => {
        this.config.set(cfg);
        this.isLoadingConfig.set(false);
      },
      error: () => {
        this.isLoadingConfig.set(false);
        this.userInfoService.error(
          this.translateService.instant("vpn.config.error"),
          ""
        );
      }
    });
  }

  copyConfig(): void {
    const text = this.config();
    if (text) {
      navigator.clipboard.writeText(text).then(() => {
        this.isCopied.set(true);
        this.userInfoService.info(
          this.translateService.instant("vpn.config.copied"),
          ""
        );
        setTimeout(() => this.isCopied.set(false), 3000);
      });
    }
  }

  downloadConfig(): void {
    const text = this.config();
    if (text) {
      const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'wg0.conf';
      link.click();
      URL.revokeObjectURL(url);
    }
  }
}

import { Component, OnInit, inject } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {HeaderComponent} from "./header/header.component";
import {LightDarkModeService} from "./core/services/light-dark-mode.service";
import {ConfirmationService, MessageService} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {ToastModule} from "primeng/toast";
import {UserInfoService} from "./core/services/user-info.service";
import {SwUpdate} from "@angular/service-worker";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {PrimeNG} from "primeng/config";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, ToastModule, ConfirmDialogModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class AppComponent implements OnInit {
  private readonly lightDarkModeService = inject(LightDarkModeService);
  private readonly primengConfig = inject(PrimeNG);
  private readonly translateService = inject(TranslateService);
  private readonly messageService = inject(MessageService);
  private readonly userInfoService = inject(UserInfoService);
  private readonly swUpdate = inject(SwUpdate);
  private readonly confirmationService = inject(ConfirmationService);

  ngOnInit(): void {
    this.translateService.onLangChange.subscribe(() => {
      this.translateService.get('primeng').subscribe(res => this.primengConfig.setTranslation(res));
    });

    this.lightDarkModeService.init(document)

    this.userInfoService.messageEmitter.subscribe(message => this.messageService.add(message))

    if(this.swUpdate.isEnabled){
      this.swUpdate.checkForUpdate().then((updateAvailable) => {
        if(updateAvailable){
          this.updateConfirmDialog(this.swUpdate)
        }
      })
    }
  }

  updateConfirmDialog(swUpdate: SwUpdate){
    this.confirmationService.confirm({
      header: this.translateService.instant('sw-update.update-available'),
      message: this.translateService.instant('sw-update.update-available-message'),
      icon: 'pi pi-spin pi-cog',
      rejectButtonStyleClass:"p-button-text",
      acceptLabel: this.translateService.instant('sw-update.update'),
      rejectVisible: false,
      accept: () => {
        swUpdate.activateUpdate().then(() => window.location.reload());
      },
    })
  }

}

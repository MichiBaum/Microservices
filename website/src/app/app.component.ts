import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HeaderComponent} from "./header/header.component";
import {LightDarkMode, LightDarkModeService} from "./core/services/light-dark-mode.service";
import {ConfirmationService, MessageService, PrimeNGConfig} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {ToastModule} from "primeng/toast";
import {UserInfoService} from "./core/services/user-info.service";
import {SwUpdate} from "@angular/service-worker";
import {ConfirmDialogModule} from "primeng/confirmdialog";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, ToastModule, ConfirmDialogModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [MessageService, ConfirmationService]
})
export class AppComponent implements OnInit {

  constructor(
    private lightDarkModeService: LightDarkModeService,
    private primengConfig: PrimeNGConfig,
    private translateService: TranslateService,
    private messageService: MessageService,
    private userInfoService: UserInfoService,
    private swUpdate: SwUpdate,
    private confirmationService: ConfirmationService
  ) {
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;

    this.translateService.onLangChange.subscribe(() => {
      this.translateService.get('primeng').subscribe(res => this.primengConfig.setTranslation(res));
    });

    this.lightDarkModeService.init(document)

    this.userInfoService.emitter.subscribe(message => this.messageService.add(message))

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
      rejectLabel: this.translateService.instant('sw-update.later'),
      accept: () => {
        swUpdate.activateUpdate().then(() => window.location.reload());
      },
      reject: () => {

      }
    })
  }

}

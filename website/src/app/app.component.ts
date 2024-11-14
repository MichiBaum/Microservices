import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {HeaderComponent} from "./header/header.component";
import {LightDarkModeService} from "./core/services/light-dark-mode.service";
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
    private readonly lightDarkModeService: LightDarkModeService,
    private readonly primengConfig: PrimeNGConfig,
    private readonly translateService: TranslateService,
    private readonly messageService: MessageService,
    private readonly userInfoService: UserInfoService,
    private readonly swUpdate: SwUpdate,
    private readonly confirmationService: ConfirmationService
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

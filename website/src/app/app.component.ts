import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HeaderComponent} from "./header/header.component";
import {LightDarkMode, LightDarkModeService} from "./core/services/light-dark-mode.service";
import {MessageService, PrimeNGConfig} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {ToastModule} from "primeng/toast";
import {UserInfoService} from "./core/services/user-info.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, ToastModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [MessageService]
})
export class AppComponent implements OnInit {

  constructor(
    private lightDarkModeService: LightDarkModeService,
    private primengConfig: PrimeNGConfig,
    private translateService: TranslateService,
    private messageService: MessageService,
    private userInfoService: UserInfoService
  ) {
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;

    this.translateService.onLangChange.subscribe(() => {
      this.translateService.get('primeng').subscribe(res => this.primengConfig.setTranslation(res));
    });

    this.lightDarkModeService.init(document)

    this.userInfoService.emitter.subscribe(message => this.messageService.add(message))

  }

}

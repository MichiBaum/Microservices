import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HeaderComponent} from "./header/header.component";
import {LightDarkMode, LightDarkModeService} from "./core/services/light-dark-mode.service";
import {PrimeNGConfig} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  constructor(
    private lightDarkModeService: LightDarkModeService,
    private primengConfig: PrimeNGConfig,
    private translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;

    this.translateService.onLangChange.subscribe(() => {
      this.translateService.get('primeng').subscribe(res => this.primengConfig.setTranslation(res));
    });

    if(this.lightDarkModeService.isSystemDark()){
      this.lightDarkModeService.changeModeTo(document, LightDarkMode.dark)
    }

  }

}

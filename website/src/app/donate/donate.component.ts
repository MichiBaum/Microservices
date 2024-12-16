import {Component} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {PrimeTemplate} from "primeng/api";
import {TranslateModule} from "@ngx-translate/core";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-donate',
  standalone: true,
  imports: [
    Button,
    CardModule,
    PrimeTemplate,
    TranslateModule,
    NgOptimizedImage
  ],
  templateUrl: './donate.component.html',
  styleUrl: './donate.component.scss'
})
export class DonateComponent {

  constructor(
    private readonly headerService: HeaderService,
    protected router: RouterNavigationService
  ) {
    this.headerService.changeTitle(Sides.donate.translationKey)
  }

}

import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {TranslateModule} from "@ngx-translate/core";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-donate',
  imports: [
    Button,
    CardModule,
    TranslateModule,
    NgOptimizedImage
  ],
  templateUrl: './donate.component.html',
  styleUrl: './donate.component.scss'
})
export class DonateComponent implements OnInit{
  private readonly headerService = inject(HeaderService);
  protected router = inject(RouterNavigationService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.donate.translationKey)
  }

}

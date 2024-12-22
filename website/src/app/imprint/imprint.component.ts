import {Component, inject, OnInit} from '@angular/core';
import {CardModule} from "primeng/card";
import {TranslateModule} from "@ngx-translate/core";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";

@Component({
  selector: 'app-imprint',
  imports: [
    CardModule,
    TranslateModule
  ],
  templateUrl: './imprint.component.html',
  styleUrl: './imprint.component.scss'
})
export class ImprintComponent implements OnInit{
  private readonly headerService = inject(HeaderService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.imprint.translationKey)
  }

}

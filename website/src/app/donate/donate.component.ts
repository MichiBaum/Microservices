import {Component, inject} from '@angular/core';
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
  styleUrl: './donate.component.css'
})
export class DonateComponent {
  protected router = inject(RouterNavigationService);

}



import {Component, inject} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {TranslateModule} from "@ngx-translate/core";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgOptimizedImage} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCoffee, faHeart, faServer} from "@fortawesome/free-solid-svg-icons";
import {faGithub} from "@fortawesome/free-brands-svg-icons";

@Component({
  selector: 'app-donate',
  imports: [
    Button,
    CardModule,
    TranslateModule,
    NgOptimizedImage,
    FaIconComponent
  ],
  templateUrl: './donate.component.html',
  styleUrl: './donate.component.css'
})
export class DonateComponent {
  protected router = inject(RouterNavigationService);

  protected readonly faCoffee = faCoffee;
  protected readonly faGithub = faGithub;
  protected readonly faServer = faServer;
  protected readonly faHeart = faHeart;

}



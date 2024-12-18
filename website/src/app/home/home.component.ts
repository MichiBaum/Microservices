import {Component} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgIf, NgOptimizedImage} from "@angular/common";
import {PermissionService} from "../core/services/permission.service";
import {TranslateModule} from "@ngx-translate/core";
import {ScrollTopModule} from "primeng/scrolltop";

@Component({
  selector: 'app-home',
  imports: [
    Button,
    CardModule,
    NgIf,
    TranslateModule,
    ScrollTopModule,
    NgOptimizedImage
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(
    private readonly headerService: HeaderService,
    protected routerNavigationService: RouterNavigationService,
    private readonly permissionService: PermissionService
  ) {
    this.headerService.changeTitle(Sides.home.translationKey)
  }


  canActivateChess() {
    return Sides.chess.canActivate(this.permissionService)
  }

  canLogin() {
    return Sides.login.canActivate(this.permissionService)
  }

  canActivateFitness() {
    return Sides.fitness.canActivate(this.permissionService)
  }

  canActivateMusic() {
    return Sides.music.canActivate(this.permissionService)
  }
}

import { Component } from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgIf} from "@angular/common";
import {PermissionService} from "../core/services/permission.service";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    Button,
    CardModule,
    NgIf,
    TranslateModule
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
}

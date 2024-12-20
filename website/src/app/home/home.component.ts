import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgIf} from "@angular/common";
import {PermissionService} from "../core/services/permission.service";
import {TranslateModule} from "@ngx-translate/core";
import {ScrollTopModule} from "primeng/scrolltop";
import {HomeCardComponent} from "./home-card/home-card.component";
import {Skeleton} from "primeng/skeleton";

@Component({
  selector: 'app-home',
  imports: [
    NgIf,
    TranslateModule,
    ScrollTopModule,
    HomeCardComponent,
    Skeleton
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit{
  private readonly headerService = inject(HeaderService);
  protected routerNavigationService = inject(RouterNavigationService);
  private readonly permissionService = inject(PermissionService);


  ngOnInit(): void {
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

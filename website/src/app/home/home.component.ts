import {Component, inject} from '@angular/core';
import {Sides} from "../core/config/sides";
import {RouterNavigationService} from "../core/services/router-navigation.service";

import {PermissionService} from "../core/services/permission.service";
import {TranslateModule} from "@ngx-translate/core";
import {ScrollTopModule} from "primeng/scrolltop";
import {HomeCardComponent} from "./home-card/home-card.component";
import {DeferPlaceholderComponent} from "../shared/defer-placeholder/defer-placeholder.component";

@Component({
  selector: 'app-home',
  imports: [
    TranslateModule,
    ScrollTopModule,
    HomeCardComponent,
    DeferPlaceholderComponent
],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  protected routerNavigationService = inject(RouterNavigationService);
  private readonly permissionService = inject(PermissionService);


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



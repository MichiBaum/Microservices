import {Component, inject} from '@angular/core';
import {Sides} from "../core/config/sides";
import {RouterNavigationService} from "../core/services/router-navigation.service";

import {PermissionService} from "../core/services/permission.service";
import {TranslateModule} from "@ngx-translate/core";
import {ScrollTopModule} from "primeng/scrolltop";
import {HomeCardComponent} from "./home-card/home-card.component";
import {DeferPlaceholderComponent} from "../shared/defer-placeholder/defer-placeholder.component";
import {faChess, faDumbbell, faHeart, faMusic, faSignInAlt, faUser} from "@fortawesome/free-solid-svg-icons";
import {IfAuthenticatedElsePipe} from "../core/pipes/if-authenticated-else.pipe";
import {CanActivateElsePipe} from "../core/pipes/can-activate-else.pipe";

@Component({
  selector: 'app-home',
  imports: [
    TranslateModule,
    ScrollTopModule,
    HomeCardComponent,
    DeferPlaceholderComponent,
    IfAuthenticatedElsePipe,
    CanActivateElsePipe
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  protected routerNavigationService = inject(RouterNavigationService);
  private readonly permissionService = inject(PermissionService);

  protected readonly faSignInAlt = faSignInAlt;
  protected readonly faChess = faChess;
  protected readonly faDumbbell = faDumbbell;
  protected readonly faMusic = faMusic;
  protected readonly faUser = faUser;
  protected readonly faHeart = faHeart;


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



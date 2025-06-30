import {Component, inject} from '@angular/core';
import {Sides} from "../core/config/sides";
import {RouterNavigationService} from "../core/services/router-navigation.service";

import {PermissionService} from "../core/services/permission.service";
import {TranslateModule} from "@ngx-translate/core";
import {ScrollTopModule} from "primeng/scrolltop";
import {HomeCardComponent} from "./home-card/home-card.component";
import {DeferPlaceholderComponent} from "../shared/defer-placeholder/defer-placeholder.component";
import {IfAuthenticatedElsePipe} from "../core/pipes/if-authenticated-else.pipe";

@Component({
  selector: 'app-home',
  imports: [
    TranslateModule,
    ScrollTopModule,
    HomeCardComponent,
    DeferPlaceholderComponent,
    IfAuthenticatedElsePipe
],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
    protected routerNavigationService = inject(RouterNavigationService);
    private readonly permissionService = inject(PermissionService);

    canLogin() {
        return Sides.login.canActivate(this.permissionService)
    }

    isAuthenticated() {
        return this.permissionService.isAuthenticated()
    }

    goToNotes() {
        if (!this.isAuthenticated()) {
            return this.routerNavigationService.login()
        }
        return this.routerNavigationService.notes()
    }

    goToFitness(){
        if (!this.isAuthenticated()) {
            return this.routerNavigationService.login()
        }
        return this.routerNavigationService.fitness()
    }

    goToMusic(){
        if (!this.isAuthenticated()) {
            return this.routerNavigationService.login()
        }
        return this.routerNavigationService.music()
    }

}



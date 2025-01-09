import {Component, inject, OnDestroy, signal} from '@angular/core';
import {faArrowRightFromBracket} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {AuthService} from "../core/api-services/auth.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgIf} from "@angular/common";
import {Button, ButtonDirective} from "primeng/button";
import {Ripple} from "primeng/ripple";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-logout',
  imports: [
    FaIconComponent,
    NgIf,
    Button,
    ButtonDirective,
    Ripple
  ],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.scss'
})
export class LogoutComponent implements OnDestroy{
  private readonly authService = inject(AuthService);
  private readonly routerNavigation = inject(RouterNavigationService);

  protected readonly buttonIcon = faArrowRightFromBracket;
  visible = signal(this.authService.isAuthenticated())

  private successLoginSubscription: Subscription = this.authService.successLoginEmitter.subscribe(() => {
    this.visible.set(true)
  });
  private logoutSubscription: Subscription = this.authService.logoutEmitter.subscribe(() => {
    this.visible.set(false)
  });

  ngOnDestroy(): void {
    this.successLoginSubscription.unsubscribe()
    this.logoutSubscription.unsubscribe()
  }

  logout() {
    this.authService.logout()
    this.routerNavigation.login()
  }
}

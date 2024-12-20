import { Component, OnInit, inject } from '@angular/core';
import {faArrowRightFromBracket} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {AuthService} from "../core/services/auth.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgIf} from "@angular/common";
import {Button, ButtonDirective} from "primeng/button";
import {Ripple} from "primeng/ripple";

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
export class LogoutComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly routerNavigation = inject(RouterNavigationService);

  protected readonly buttonIcon = faArrowRightFromBracket;
  visible = true


  ngOnInit(): void {
    this.visible = this.authService.isAuthenticated()
    this.authService.successLoginEmitter.subscribe(() => {
      this.visible = true
    })
    this.authService.logoutEmitter.subscribe(() => {
      this.visible = false
    })
  }

  logout() {
    this.authService.logout()
    this.routerNavigation.login()
  }
}

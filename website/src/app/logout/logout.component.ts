import {Component, OnInit} from '@angular/core';
import {faArrowRightFromBracket} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {AuthService} from "../core/services/auth.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-logout',
  standalone: true,
  imports: [
    FaIconComponent,
    NgIf
  ],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.scss'
})
export class LogoutComponent implements OnInit{

  protected readonly faArrowRightFromBracket = faArrowRightFromBracket;
  visible = true

  constructor(private authService: AuthService, private routerNavigation: RouterNavigationService) {

  }

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

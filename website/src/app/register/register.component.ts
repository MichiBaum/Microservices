import {Component, OnInit} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {FloatLabelModule} from "primeng/floatlabel";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {PasswordModule} from "primeng/password";
import {PrimeTemplate} from "primeng/api";
import {AuthService} from "../core/services/auth.service";
import {HeaderService} from "../core/services/header.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {Sides} from "../core/config/sides";
import {UserInfoService} from "../core/services/user-info.service";
import {TranslateModule} from "@ngx-translate/core";
import {FocusTrapModule} from "primeng/focustrap";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    Button,
    CardModule,
    FloatLabelModule,
    InputTextModule,
    PaginatorModule,
    PasswordModule,
    PrimeTemplate,
    TranslateModule,
    FocusTrapModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit{
  username: string = "";
  password: string = "";
  passwordRepeat: string = "";

  constructor(
    private readonly authService: AuthService,
    private readonly headerService: HeaderService,
    private readonly router: RouterNavigationService,
    private readonly userInfoService: UserInfoService
  ) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.register.translationKey)
  }

  login() {
    this.router.login()
  }

  register() {
    this.userInfoService.info('Not implemented', 'This functionality is not implemented yet')
  }
}

import {Component, OnInit, inject, signal} from '@angular/core';
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {FloatLabelModule} from "primeng/floatlabel";
import {InputTextModule} from "primeng/inputtext";
import {PaginatorModule} from "primeng/paginator";
import {PasswordModule} from "primeng/password";
import {AuthService} from "../core/services/auth.service";
import {HeaderService} from "../core/services/header.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {Sides} from "../core/config/sides";
import {UserInfoService} from "../core/services/user-info.service";
import {TranslateModule} from "@ngx-translate/core";
import {FocusTrapModule} from "primeng/focustrap";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-register',
  imports: [
    Button,
    CardModule,
    FloatLabelModule,
    InputTextModule,
    PaginatorModule,
    PasswordModule,
    TranslateModule,
    FocusTrapModule,
    FormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly headerService = inject(HeaderService);
  private readonly router = inject(RouterNavigationService);
  private readonly userInfoService = inject(UserInfoService);

  username = signal("");
  password = signal("");
  passwordRepeat = signal("");


  ngOnInit(): void {
    this.headerService.changeTitle(Sides.register.translationKey)
  }

  login() {
    this.router.login()
  }

  register() {
    this.userInfoService.info('Not implemented', 'This functionality is not implemented yet')
    return;

    // TODO
    if(this.username() == '' || this.password() == '' || this.passwordRepeat() == '')
      return;
    if(this.password() !== this.passwordRepeat())
      return;
  }
}

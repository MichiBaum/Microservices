import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PasswordModule} from "primeng/password";
import {FloatLabelModule} from "primeng/floatlabel";
import {AuthService} from "../core/services/auth.service";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {CardModule} from "primeng/card";
import {TranslateModule} from "@ngx-translate/core";
import {RouterNavigationService} from "../core/services/router-navigation.service";

@Component({
  selector: 'app-authentication',
  standalone: true,
    imports: [
        ReactiveFormsModule,
        Button,
        InputTextModule,
        PasswordModule,
        FloatLabelModule,
        FormsModule,
        CardModule,
        TranslateModule
    ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit{

  username: string = "";
  password: string = "";

  constructor(
    private readonly authService: AuthService,
    private readonly headerService: HeaderService,
    private readonly router: RouterNavigationService
  ) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.login.translationKey)
  }

  login(){
    if(this.username == '' || this.password == '')
      return;
    this.authService.login(this.username ?? '', this.password ?? '')
  }

  register() {
    this.router.register()
  }
}

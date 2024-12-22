import {Component, OnInit, inject, signal} from '@angular/core';
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
import {FocusTrapModule} from "primeng/focustrap";

@Component({
  selector: 'app-authentication',
  imports: [
    ReactiveFormsModule,
    Button,
    InputTextModule,
    PasswordModule,
    FloatLabelModule,
    FormsModule,
    CardModule,
    TranslateModule,
    FocusTrapModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly headerService = inject(HeaderService);
  private readonly router = inject(RouterNavigationService);

  protected username = signal("")
  protected password = signal("")


  ngOnInit(): void {
    this.headerService.changeTitle(Sides.login.translationKey)
  }

  login(){
    if(this.username() == '' || this.password() == '')
      return;
    this.authService.login(this.username(), this.password())
  }

  register() {
    this.router.register()
  }
}

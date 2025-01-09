import {Component, inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PasswordModule} from "primeng/password";
import {FloatLabelModule} from "primeng/floatlabel";
import {AuthService} from "../core/api-services/auth.service";
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

  loginForm: FormGroup = new FormGroup({
    username: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    password: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ])
  })

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.login.translationKey)
  }

  login(){
    if(!this.loginForm.valid)
      return;

    const username = this.loginForm.controls['username'].value ?? "";
    const password = this.loginForm.controls['password'].value ?? "";

    if(username == "" || password == "")
      return;

    this.authService.logout();
    this.authService.login(username, password)
  }

  register() {
    this.router.register()
  }
}

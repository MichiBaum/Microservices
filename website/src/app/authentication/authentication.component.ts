import { Component } from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {PasswordModule} from "primeng/password";
import {FloatLabelModule} from "primeng/floatlabel";
import {AuthService} from "../core/services/auth.service";
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";

@Component({
  selector: 'app-authentication',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    Button,
    InputTextModule,
    PasswordModule,
    FloatLabelModule,
    FormsModule
  ],
  templateUrl: './authentication.component.html',
  styleUrl: './authentication.component.scss'
})
export class AuthenticationComponent {

  loginFormGroup = new FormGroup(
    {
      username: new FormControl('', [
        Validators.required
      ]),
      password: new FormControl('', [
        Validators.required
      ]),
    }
  );

  constructor(private authService: AuthService, private headerService: HeaderService) {
    this.headerService.changeTitle(Sides.login.translationKey)
  }

  login(){
    if(!this.loginFormGroup.valid)
      return;
    var values = this.loginFormGroup.value
    this.authService.login(values.username ?? '', values.password ?? '')
  }

}

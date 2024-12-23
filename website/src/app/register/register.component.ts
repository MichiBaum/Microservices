import {Component, OnInit, inject} from '@angular/core';
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
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {FocusTrapModule} from "primeng/focustrap";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Register, RegisterState} from "../core/models/authentication.model";
import {CustomErrorMatching} from "../core/config/http-error-handler.service";
import {HttpErrorResponse} from "@angular/common/http";

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
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit{
  private readonly authService = inject(AuthService);
  private readonly headerService = inject(HeaderService);
  private readonly router = inject(RouterNavigationService);
  private readonly userInfoService = inject(UserInfoService);
  private readonly translate = inject(TranslateService);

  registerForm: FormGroup = new FormGroup({
    username: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    email: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
      Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")
    ]),
    password: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ]),
    passwordRepeat: new FormControl<string>({
      value: '',
      disabled: false
    }, [
      Validators.required,
    ])
  });


  ngOnInit(): void {
    this.headerService.changeTitle(Sides.register.translationKey)
  }

  login() {
    this.router.login()
  }

  register() {
    if(!this.registerForm.valid)
      return;
    let password = this.registerForm.controls['password'].value ?? "";
    let passwordRepeat = this.registerForm.controls['passwordRepeat'].value ?? "";
    if(password == "" || passwordRepeat == "")
      return;
    if(password != passwordRepeat) {
      this.registerForm.controls['password'].setErrors({})
      this.registerForm.controls['passwordRepeat'].setErrors({})
      return;
    }

    const registerUser: Register = {
      username: this.registerForm.controls['username'].value ?? "",
      email: this.registerForm.controls['email'].value ?? "",
      password: this.registerForm.controls['password'].value ?? ""
    }

    const customErrorMatching: CustomErrorMatching =  {
      matcher: (error: HttpErrorResponse) => error.status == 409,
      summary: this.translate.instant("register.error.summary"),
      details: this.translate.instant("register.error.details")
    }
    this.authService.register(registerUser, customErrorMatching)
      .subscribe(value => {

      const state = RegisterState[value.state]
      // @ts-ignore
      if(state == RegisterState.SUCCESS){
        this.userInfoService.info(this.translate.instant("register.success.summary"), this.translate.instant("register.success.details"), 10000)
        this.router.login()
        return;
      }
    })


  }
}

import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {tap} from 'rxjs/internal/operators/tap';
import {AuthService} from '../core/services/auth.service';
import {RouternavigationService} from '../core/services/routernavigation.service';
import {LoginService} from './login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnChanges {

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private routernavigationService: RouternavigationService,
    private loginService: LoginService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  loginForm: FormGroup;

  @Input() loginName = '';

  @Input() loginNameDisabled = false;

  @Input() onLoginSuccess = (data: any) => {
    this.authService.setSession(data);
    this.loginService.emitLogin();
    this.routernavigationService.homeNavigate();
  }

  ngOnInit(): void {}

  login = () => {
    this.enableUsernameIfDisabled();
    this.sendLoginRequest();
    this.disableUsernameIfDisabledFlagTrue();
  }

  private sendLoginRequest() {
    this.authService.login(
      this.loginForm.value.username,
      this.loginForm.value.password
    ).subscribe(
      (data) => {
        if (data !== null && !Array.isArray(data)) {
          this.onLoginSuccess(data);
        }
        this.loginForm.reset();
      }
    );
  }

  private disableUsernameIfDisabledFlagTrue() {
    if (this.loginNameDisabled) {
      this.loginForm.controls.username.disable();
    }
  }

  private enableUsernameIfDisabled() {
    if (this.loginForm.controls.username.disabled) {
      this.loginForm.controls.username.enable();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.loginName?.currentValue) {
      this.loginForm.setValue({
        username: changes.loginName.currentValue,
        password: ''
      });
    }
    if (changes?.loginNameDisabled?.currentValue) {
      this.loginForm.controls.username.disable(
        changes?.loginNameDisabled?.currentValue
      );
    }
  }

}

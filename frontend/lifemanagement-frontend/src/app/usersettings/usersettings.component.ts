import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {PermissionEnum} from '../core/models/enum/permission.enum';
import {IPermission} from '../core/models/permission.model';
import {IPrimeNgBase} from '../core/models/primeng-base.model';
import {IUpdateUser, IUser} from '../core/models/user.model';
import {AuthService} from '../core/services/auth.service';
import {LoginService} from '../login/login.service';
import {ToastMessageService} from '../toast-message/toast-message.service';
import {UserService} from './user.service';

@Component({
  selector: 'app-usersettings',
  templateUrl: './usersettings.component.html',
  styleUrls: ['./usersettings.component.scss']
})
export class UsersettingsComponent implements OnInit {

  hasPermissionUserManagement = false;
  users: IPrimeNgBase[] = [];
  selectedUser: IUser;
  myUserId: number;
  availablePermissions: IPermission[] = [];

  changeableUser: IUser;
  newPassword = '';

  loginModalVisible = false;
  loginDialogWidth = '50vw';
  onLoginSuccess = () => {this.loginService.emitLogin(); };

  constructor(
    public authService: AuthService,
    private userService: UserService,
    private loginService: LoginService,
    private toastMessageService: ToastMessageService,
    private translate: TranslateService
  ) {
    this.loginService.loginEmitter.subscribe(() => {
      this.loginModalVisible = false;
    });
  }

  ngOnInit(): void {
    if (window.innerWidth < 800) { this.loginDialogWidth = '80vw'; }
    this.hasPermissionUserManagement = this.authService.hasAnyPermission([PermissionEnum.USER_MANAGEMENT]);
    this.loadUsers();
  }

  private loadUsers = () => {
    const observableMe = this.userService.getMe();
    observableMe.subscribe((value) => this.myUserId = value.id);
    if (this.hasPermissionUserManagement) {
      this.userService.getAll().subscribe( (users: IUser[]) => {
        users.forEach((user: IUser) => {
          this.users.push({label: user.name, field: user.name, value: user} as IPrimeNgBase);
        });
      });
      return;
    }
    observableMe.subscribe((value) =>
      this.users.push({label: value.name, field: value.name, value} as IPrimeNgBase)
    );
  }

  userChanged(event: any) {
    const user: IUser = event.value as IUser;
    this.changeableUser = JSON.parse(JSON.stringify(user));
    this.newPassword = '';
    if (this.hasPermissionUserManagement) {
      this.userService.getAllPermissions().subscribe(value => {
        this.availablePermissions = value.filter((el) => !user.permissions.map(value1 => value1.id).includes(el.id));
      });
    }
  }

  saveUser(changeableUser: IUser) {
    this.userService.save(this.toExportUser(changeableUser)).subscribe(
      (user: IUser) => {
        if (this.needsLogin()) {
          this.loginModalVisible = true;
        }
        this.replaceUserInUsers(user);
        this.successMessage();
      },
      (error) => {
        this.errorMessage();
      });
  }

  private replaceUserInUsers(user: IUser) {
    const index = this.users.findIndex((primeNgBase) => primeNgBase.value.id === user.id);
    if (index !== -1) {
      this.users[index] = {label: user.name, field: user.name, value: user} as IPrimeNgBase;
      this.selectedUser = this.users[index].value;
      this.newPassword = '';
    }
  }

  private toExportUser = (user: IUser): IUpdateUser => {
    return {
      id: user.id,
      name: user.name,
      emailAddress: user.emailAddress,
      password: this.newPassword,
      enabled: user.enabled,
      permissions: user.permissions.map( (permission) => permission.id )
    } as IUpdateUser;
  }

  private needsLogin = (): boolean => {
    if (this.changeableUser.id === this.myUserId) {
      return this.changeableUser.name !== this.authService.getUsername()
      || this.newPassword.length !== 0
      || this.changeableUser.enabled !== this.selectedUser.enabled;
    }
    return false;
  }

  private successMessage() {
    this.toastMessageService.emitSuccess(
      this.translate.instant('user.updateSuccessfully'),
      this.translate.instant('user.updateSuccessfully')
    );
  }

  private errorMessage() {
    this.toastMessageService.emitError(
      this.translate.instant('user.updateFailed'),
      this.translate.instant('user.updateFailed')
    );
  }

  savePermissions() {
    this.userService.savePermissions(this.toExportUser(this.selectedUser)).subscribe();
    this.successMessage();
  }

}

import { Component, OnInit } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {DateFormat} from '../core/models/enum/date-format.enum';
import {PrimeNgBase} from '../core/models/primeng-base.model';
import {ExportUser, User} from '../core/models/user.model';
import {Permission} from '../core/security/permission.enum';
import {AuthService} from '../core/services/auth.service';
import {DateService} from '../core/services/date.service';
import {LoginService} from '../login/login.service';
import {ToastMessageService} from '../toast-message/toast-message.service';
import {UserService} from './user.service';

@Component({
  selector: 'app-usersettings',
  templateUrl: './usersettings.component.html',
  styleUrls: ['./usersettings.component.scss']
})
export class UsersettingsComponent implements OnInit {

  dateFormats: PrimeNgBase[];
  selectedDateFormat: PrimeNgBase;

  hasPermissionUserManagement = false;
  users: PrimeNgBase[] = [];
  selectedUser: User;
  myUserId: number;

  changeableUser: User;
  newPassword = '';

  loginModalVisible = false;
  onLoginSuccess = () => {this.loginService.emitLogin(); };

  constructor(
    private dateService: DateService,
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
    this.dateFormats = this.initDateFormats();
    this.selectedDateFormat = this.initSelectedDateFormat();
    this.hasPermissionUserManagement = this.authService.hasAnyPermission([Permission.USER_MANAGEMENT]);
    this.loadUsers();
  }

  private initDateFormats = (): PrimeNgBase[] => {
    const dateFormats: PrimeNgBase[] = [];
    for (const dateFormat of Object.keys(DateFormat)) {
      dateFormats.push(
        {
          field: dateFormat,
          label: 'dateFormat.' + dateFormat,
          value: dateFormat
        } as PrimeNgBase);
    }
    return dateFormats;
  }

  changeDateFormat = (event: any) => {
    this.dateService.setFormat(event.value as DateFormat);
  }

  private initSelectedDateFormat = () => {
    const localStorageDateFormat = this.dateService.getFormat();
    if (localStorageDateFormat) {
      return this.selectedDateFormat = {
        field: localStorageDateFormat,
        label: 'dateFormat.' + localStorageDateFormat,
        value: localStorageDateFormat
      } as PrimeNgBase;
    }
  }

  private loadUsers = () => {
    const observableMe = this.userService.getMe();
    observableMe.subscribe((value) => this.myUserId = value.id);
    if (this.hasPermissionUserManagement) {
      this.userService.getAll().subscribe( (users: User[]) => {
        users.forEach((user: User) => {
          this.users.push({label: user.name, field: user.name, value: user} as PrimeNgBase);
        });
      });
      return;
    }
    observableMe.subscribe((value) => this.users.push({label: value.name, field: value.name, value} as PrimeNgBase));
  }

  userChanged(event: any) {
    const user: User = event.value as User;
    this.changeableUser = JSON.parse(JSON.stringify(user));
    this.newPassword = '';
  }

  saveUser() {
    this.userService.save(this.toExportUser(this.changeableUser)).subscribe(
      (user) => {
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

  private replaceUserInUsers(user: User) {
    const index = this.users.findIndex((primeNgBase) => primeNgBase.value.id === user.id);
    if (index !== -1) {
      this.users[index] = {label: user.name, field: user.name, value: user} as PrimeNgBase;
      this.selectedUser = this.users[index].value;
      this.newPassword = '';
    }
  }

  private toExportUser = (user: User): ExportUser => {
    return {
      id: user.id,
      name: user.name,
      emailAddress: user.emailAddress,
      password: this.newPassword,
      enabled: user.enabled,
      lastLogin: user.lastLogin,
      permissions: user.permissions.map( (permission) => permission.id )
    } as ExportUser;
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

}

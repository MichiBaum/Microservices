import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {IUser} from '../../core/models/user.model';

@Component({
  selector: 'app-userdetails',
  templateUrl: './userdetails.component.html',
  styleUrls: ['./userdetails.component.scss']
})
export class UserdetailsComponent implements OnInit {

  @Input() selectedUser: IUser;
  @Input() changeableUser: IUser;
  @Output() savedUser: EventEmitter<IUser> = new EventEmitter<IUser>();

  hasPermissionUserManagement = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  saveUser() {
    this.savedUser.emit(this.changeableUser);
  }
}

import {EventEmitter, Injectable, Output} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  @Output() loginEmitter: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  emitLogin = () => {
    this.loginEmitter.emit();
  }

}

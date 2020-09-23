import {EventEmitter, Injectable, Output} from '@angular/core';
import {Message} from 'primeng';
import {ToastMessageSeverity} from '../core/models/enum/toast-message-severity.enum';

@Injectable({
  providedIn: 'root'
})
export class ToastMessageService {

  @Output() toastMessageEmitter: EventEmitter<Message[]> = new EventEmitter<Message[]>();

  constructor() { }

  emit = (toastMessages: Message[]) => {
    this.toastMessageEmitter.emit(toastMessages);
  }

  emitSuccess = (summary: string, detail: string) => {
    this.toastMessageEmitter.emit(
      [{
        severity: ToastMessageSeverity.SUCCESS,
        summary,
        detail,
        life: 10000
      } as Message]
    );
  }

  emitError = (summary: string, detail: string) => {
    this.toastMessageEmitter.emit(
      [{
        severity: ToastMessageSeverity.ERROR,
        summary,
        detail,
        life: 10000
      } as Message]
    );
  }

}

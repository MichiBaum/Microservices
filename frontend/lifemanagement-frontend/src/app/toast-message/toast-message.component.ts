import {Component, OnDestroy, OnInit} from '@angular/core';
import {MessageService} from 'primeng';
import {ToastMessageService} from './toast-message.service';

@Component({
  selector: 'app-message-service',
  templateUrl: './toast-message.component.html',
  styleUrls: ['./toast-message.component.scss']
})
export class ToastMessageComponent implements OnInit, OnDestroy {

  constructor(
    private toastMessageService: ToastMessageService,
    private messageService: MessageService
  ) {
    toastMessageService.toastMessageEmitter.subscribe((messages) => {
      messageService.addAll(messages);
    });
  }

  ngOnInit(): void { }

  ngOnDestroy(): void {
    this.toastMessageService.toastMessageEmitter.unsubscribe();
  }

}

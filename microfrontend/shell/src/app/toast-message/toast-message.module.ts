import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {ToastModule} from 'primeng/toast';
import {ToastMessageComponent} from './toast-message.component';

@NgModule({
  declarations: [
    ToastMessageComponent
  ],
  exports: [
    ToastMessageComponent
  ],
  imports: [
    CommonModule,
    ToastModule
  ]
})
export class ToastMessageModule { }

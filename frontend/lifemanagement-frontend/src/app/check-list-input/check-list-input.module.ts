import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {InputTextareaModule, InputTextModule} from 'primeng';
import {PipeModule} from '../core/pipes/pipe.module';
import {CheckListInputComponent} from './check-list-input.component';

@NgModule({
  declarations: [
    CheckListInputComponent
  ],
  exports: [
    CheckListInputComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    PipeModule,
    InputTextModule,
    InputTextareaModule
  ]
})
export class CheckListInputModule { }

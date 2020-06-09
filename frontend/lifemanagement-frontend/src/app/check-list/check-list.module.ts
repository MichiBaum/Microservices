import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {SlideMenuModule} from 'primeng';
import {CheckListInputModule} from '../check-list-input/check-list-input.module';
import {CheckListComponent} from './check-list.component';

@NgModule({
  declarations: [
    CheckListComponent
  ],
  imports: [
    CommonModule,
    SlideMenuModule,
    CheckListInputModule
  ],
  exports: [
    CheckListComponent
  ]
})
export class CheckListModule { }

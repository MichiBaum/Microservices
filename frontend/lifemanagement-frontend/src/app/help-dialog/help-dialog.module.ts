import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {ButtonModule, DialogModule} from 'primeng';
import {HelpDialogComponent} from './help-dialog.component';

@NgModule({
  declarations: [
    HelpDialogComponent
  ],
  exports: [
    HelpDialogComponent
  ],
  imports: [
    CommonModule,
    DialogModule,
    TranslateModule,
    ButtonModule
  ]
})
export class HelpDialogModule { }

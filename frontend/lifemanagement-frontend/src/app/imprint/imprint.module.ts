import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import { ImprintComponent } from './imprint.component';

@NgModule({
  declarations: [
    ImprintComponent
  ],
  imports: [
    CommonModule,
    TranslateModule
  ],
  exports: [
    ImprintComponent
  ]
})
export class ImprintModule { }

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import { ImprintComponent } from './imprint.component';
import {CardModule} from "primeng";

@NgModule({
  declarations: [
    ImprintComponent
  ],
  imports: [
    CommonModule,
    TranslateModule,
    CardModule
  ],
  exports: [
    ImprintComponent
  ]
})
export class ImprintModule { }

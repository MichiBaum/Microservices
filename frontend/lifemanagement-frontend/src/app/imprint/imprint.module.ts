import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ImprintComponent } from './imprint.component';
import {TranslateModule} from "@ngx-translate/core";

@NgModule({
  declarations: [ImprintComponent],
	imports: [
		CommonModule,
		TranslateModule
	],
  exports: [ImprintComponent]
})
export class ImprintModule { }

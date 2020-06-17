import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {PrivacyPolicyComponent} from './privacy-policy.component';
import {CardModule} from "primeng";

@NgModule({
  declarations: [PrivacyPolicyComponent],
	imports: [
		CommonModule,
		TranslateModule,
		CardModule
	],
  exports: [PrivacyPolicyComponent]
})
export class PrivacyPolicyModule { }

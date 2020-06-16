import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TranslateModule} from '@ngx-translate/core';
import {PrivacyPolicyComponent} from './privacy-policy.component';



@NgModule({
  declarations: [PrivacyPolicyComponent],
  imports: [
    CommonModule,
    TranslateModule
  ],
  exports: [PrivacyPolicyComponent]
})
export class PrivacyPolicyModule { }

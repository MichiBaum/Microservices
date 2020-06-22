import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {TranslateModule} from '@ngx-translate/core';
import {CardModule} from 'primeng';
import {PrivacyPolicyComponent} from './privacy-policy.component';

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

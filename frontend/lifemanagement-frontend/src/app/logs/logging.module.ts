import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {ContextMenuModule, DialogModule, FieldsetModule, InputSwitchModule, MultiSelectModule, TableModule, TooltipModule} from 'primeng';
import {ButtonModule} from 'primeng/button';
import {PipeModule} from '../core/pipes/pipe.module';
import { LoggingComponent } from './logging.component';

@NgModule({
  declarations: [
    LoggingComponent,
  ],
  exports: [
  ],
  imports: [
    CommonModule,
    ButtonModule,
    TableModule,
    TranslateModule,
    MultiSelectModule,
    FormsModule,
    InputSwitchModule,
    ContextMenuModule,
    DialogModule,
    FieldsetModule,
    TooltipModule,
    PipeModule,
  ]
})
export class LoggingModule { }

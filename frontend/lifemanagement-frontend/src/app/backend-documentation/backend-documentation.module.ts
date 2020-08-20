import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {PipeModule} from '../core/pipes/pipe.module';
import { BackendDocumentationComponent } from './backend-documentation.component';

@NgModule({
  declarations: [BackendDocumentationComponent],
  imports: [
    CommonModule,
    PipeModule
  ],
  exports: [BackendDocumentationComponent]
})
export class BackendDocumentationModule { }

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {FrontendDocumentationComponent} from './frontend-documentation.component';

@NgModule({
  declarations: [FrontendDocumentationComponent],
  imports: [
    CommonModule
  ],
  exports: [
    FrontendDocumentationComponent
  ]
})
export class FrontendDocumentationModule { }

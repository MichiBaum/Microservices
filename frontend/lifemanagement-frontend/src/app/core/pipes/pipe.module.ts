import {NgModule} from '@angular/core';
import {CustomDatePipe} from './CustomDate';
import {SafePipe} from './SafePipe';

@NgModule({
  declarations: [CustomDatePipe, SafePipe],
  exports: [CustomDatePipe, SafePipe]
})
export class PipeModule {

}

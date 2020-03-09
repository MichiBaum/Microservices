import {NgModule} from '@angular/core';
import {CustomDatePipe} from './CustomDate';


@NgModule({
  declarations: [CustomDatePipe],
  exports: [CustomDatePipe]
})
export class PipeModule {

}

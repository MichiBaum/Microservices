import { Pipe, PipeTransform } from '@angular/core';
import {Status} from "../../core/models/admin/admin.model";
import {faCircleCheck, faCircleExclamation, faCircleQuestion, faCircleXmark} from "@fortawesome/free-solid-svg-icons";

@Pipe({
  name: 'msStatusStyle'
})
export class MsStatusStylePipe implements PipeTransform {

  transform(value: String | undefined, ...args: unknown[]): String {
    if (value === undefined) {
      return "color: yellow"
    }

    if (value === Status.UP) {
      return "color: green"
    }

    if (value === Status.DOWN) {
      return "color: red"
    }

    if (value === Status.UNKNOWN) {
      return "color: yellow"
    }
    if (value === Status.OUT_OF_SERVICE){
      return "color: orange"
    }

    return "color: yellow"
  }

}

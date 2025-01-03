import {Pipe, PipeTransform} from '@angular/core';
import {IconDefinition} from "@fortawesome/angular-fontawesome";
import {faCircleCheck, faCircleExclamation, faCircleQuestion, faCircleXmark} from "@fortawesome/free-solid-svg-icons";
import {Status} from "../../core/models/admin/admin.model";

@Pipe({
  name: 'msStatusIcon'
})
export class MsStatusIconPipe implements PipeTransform {

  transform(value: String | undefined, ...args: unknown[]): IconDefinition {
    if (value === undefined) {
      return faCircleQuestion
    }

    if (value === Status.UP) {
      return faCircleCheck
    }

    if (value === Status.DOWN) {
      return faCircleXmark
    }

    if (value === Status.UNKNOWN) {
      return faCircleQuestion
    }
    if (value === Status.OUT_OF_SERVICE){
      return faCircleExclamation
    }

    return faCircleQuestion
  }

}

import {inject, Injectable} from "@angular/core";
import {Subject} from "rxjs";
import {Confirmation, ToastMessageOptions} from "primeng/api";
import {faExclamation, faQuestion, faSave} from "@fortawesome/free-solid-svg-icons";
import {IconDefinition} from "@fortawesome/angular-fontawesome";
import {TranslateService} from "@ngx-translate/core";

@Injectable({ providedIn: 'root' })
export class UserConfirmationService {

  private readonly translateService = inject(TranslateService);

  private _confirmEmitter = new Subject<ToastMessageOptions>();

  constructor() {
  }

  /**
   * Gets the confirm emitter subject.
   *
   * @return {Subject<ToastMessageOptions>} The current instance of the message emitter subject.
   */
  get confirmEmitter(): Subject<ToastMessageOptions> {
    return this._confirmEmitter;
  }


  confirm(toConfirm: CustomConfirmation) {
    toConfirm.faIcon ??= faQuestion;
    toConfirm.acceptLabel ??= this.translateService.instant("confirm_dialog.confirm.accept");
    toConfirm.rejectLabel ??= this.translateService.instant("confirm_dialog.confirm.reject");
    this._confirmEmitter.next(toConfirm)
  }

  saveConfirm(toConfirm: CustomConfirmation) {
    toConfirm.faIcon = faSave
    toConfirm.acceptLabel = this.translateService.instant("confirm_dialog.save.accept");
    toConfirm.rejectLabel = this.translateService.instant("confirm_dialog.save.reject");
    this._confirmEmitter.next(toConfirm);
  }

  deleteConfirm(toConfirm: CustomConfirmation) {
    toConfirm.faIcon = faExclamation;
    toConfirm.acceptLabel = this.translateService.instant("confirm_dialog.delete.accept");
    toConfirm.rejectLabel = this.translateService.instant("confirm_dialog.delete.reject");
    this._confirmEmitter.next(toConfirm)
  }

}

export interface CustomConfirmation extends Confirmation {
  faIcon?: IconDefinition;
}

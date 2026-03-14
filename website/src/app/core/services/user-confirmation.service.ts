import {Injectable} from "@angular/core";
import {Subject} from "rxjs";
import {Confirmation, ToastMessageOptions} from "primeng/api";

@Injectable({ providedIn: 'root' })
export class UserConfirmationService {

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

  confirm(toConfirm: Confirmation) {
    this._confirmEmitter.next(toConfirm)
  }
}

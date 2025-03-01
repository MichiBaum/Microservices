import {Injectable} from "@angular/core";
import {Subject} from "rxjs";
import {InterpolationParameters} from "@ngx-translate/core";

/**
 * Service to manage and emit changes to the header title.
 */
@Injectable({providedIn: 'root'})
export class HeaderService{

  /**
   * Emitter that notifies subscribers when the title changes.
   *
   * It is a Subject that emits the new title as a string whenever the title is updated.
   * Subscribers can subscribe to this emitter to receive real-time updates of title changes.
   */
  private _titleChangeEmitter = new Subject<TranslationHolder>();

  /**
   * Gets the title change emitter subject.
   *
   * @return {Subject<string>} The subject that emits title change events.
   */
  get titleChangeEmitter(): Subject<TranslationHolder> {
    return this._titleChangeEmitter;
  }

  /**
   * Changes the title and emits the updated title information.
   *
   * @param {string} title - The new title to set.
   * @param {InterpolationParameters | undefined} [params] - Optional parameters for title interpolation.
   * @return {void} This method does not return a value.
   */
  changeTitle(title: string, params: InterpolationParameters | undefined = undefined): void {
      const x = {
          key: title,
          params: params
      }
    this._titleChangeEmitter.next(x)
  }

}

export interface TranslationHolder{
    key: string;
    params: InterpolationParameters | undefined;
}

import {Injectable} from "@angular/core";
import {Subject} from "rxjs";

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
  private _titleChangeEmitter = new Subject<string>();

  /**
   * Gets the title change emitter subject.
   *
   * @return {Subject<string>} The subject that emits title change events.
   */
  get titleChangeEmitter(): Subject<string> {
    return this._titleChangeEmitter;
  }

  /**
   * Changes the title and emits an event with the new title.
   *
   * @param {string} title - The new title to be set.
   * @return {void}
   */
  changeTitle(title: string){
    this._titleChangeEmitter.next(title)
  }

}

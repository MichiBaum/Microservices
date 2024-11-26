import {Injectable} from "@angular/core";
import {Message} from "primeng/api";
import {Subject} from "rxjs";

/**
 * Service for managing user information messages.
 *
 * This service provides methods for emitting informational and error messages.
 */
@Injectable({ providedIn: 'root' })
export class UserInfoService {

  private _messageEmitter = new Subject<Message>();

  constructor() {
  }

  /**
   * Gets the message emitter subject.
   *
   * @return {Subject<Message>} The current instance of the message emitter subject.
   */
  get messageEmitter(): Subject<Message> {
    return this._messageEmitter;
  }

  /**
   * Emits an informational message with the specified summary and details.
   *
   * @param {string} summary - A brief summary of the informational message.
   * @param {string} details - A detailed description of the informational message.
   *
   * @return {void}
   */
  info(summary: string, details: string): void {
    this._messageEmitter.next({severity: "info", summary: summary, detail: details})
  }

  /**
   * Emits an error message with the provided summary and details.
   *
   * @param {string} summary - A brief summary of the error.
   * @param {string} details - A detailed description of the error.
   * @return {void}
   */
  error(summary: string, details: string): void {
    this._messageEmitter.next({severity: "error", summary: summary, detail: details})
  }

}

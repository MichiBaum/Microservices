import {Injectable} from "@angular/core";
import {ToastMessageOptions} from "primeng/api";
import {Subject} from "rxjs";

/**
 * Service for managing user information messages.
 *
 * This service provides methods for emitting informational and error messages.
 */
@Injectable({ providedIn: 'root' })
export class UserInfoService {

  private _messageEmitter = new Subject<ToastMessageOptions>();

  constructor() {
  }

  /**
   * Gets the message emitter subject.
   *
   * @return {Subject<ToastMessageOptions>} The current instance of the message emitter subject.
   */
  get messageEmitter(): Subject<ToastMessageOptions> {
    return this._messageEmitter;
  }

  /**
   * Emits an informational message with the specified summary and details.
   *
   * @param {string} summary - A brief summary of the informational message.
   * @param {string} details - A detailed description of the informational message.
   *
   * @param durationMs
   * @return {void}
   */
  info(summary: string, details: string, durationMs: number = 5000): void {
    this._messageEmitter.next({severity: "info", summary: summary, detail: details, life: durationMs})
  }

  /**
   * Emits an error message with the provided summary and details.
   *
   * @param {string} summary - A brief summary of the error.
   * @param {string} details - A detailed description of the error.
   * @param durationMs
   * @return {void}
   */
  error(summary: string, details: string, durationMs: number = 5000): void {
    this._messageEmitter.next({severity: "error", summary: summary, detail: details, life: durationMs})
  }

}

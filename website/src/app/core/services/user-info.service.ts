import {Injectable} from "@angular/core";
import {Message, MessageService} from "primeng/api";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class UserInfoService {

  emitter = new Subject<Message>();

  constructor() {
  }

  info(summary: string, details: string){
    this.emitter.next({severity: "info", summary: summary, detail: details})
  }

  error(summary: string, details: string){
    this.emitter.next({severity: "error", summary: summary, detail: details})
  }

}

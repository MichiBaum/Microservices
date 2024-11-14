import {Injectable} from "@angular/core";
import {Subject} from "rxjs";

@Injectable({providedIn: 'root'})
export class HeaderService{

  titleChangeEmitter = new Subject<string>();

  changeTitle(title: string){
    this.titleChangeEmitter.next(title)
  }

}

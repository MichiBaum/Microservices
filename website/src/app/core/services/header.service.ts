import {EventEmitter, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class HeaderService{

  titleChangeEmitter = new EventEmitter<string>();

  changeTitle(title: string){
    this.titleChangeEmitter.emit(title)
  }

}

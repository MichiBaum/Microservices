import {EventEmitter, Injectable, OnInit, Output} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WindowResizeListenerService implements OnInit {

  @Output() screenWidthEmitter: EventEmitter<number> = new EventEmitter<number>();
  @Output() screenHeightEmitter: EventEmitter<number> = new EventEmitter<number>();

  constructor() {
  }

  ngOnInit(): void {
  }

}

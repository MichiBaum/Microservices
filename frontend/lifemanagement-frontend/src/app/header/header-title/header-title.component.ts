import {animate, state, style, transition, trigger} from '@angular/animations';
import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-header-title',
  templateUrl: './header-title.component.html',
  styleUrls: ['./header-title.component.scss'],
  animations: [
    trigger('slideLeftMiddleRight', [
      state('left', style({transform: 'translateX(-50%)'})),
      state('middle', style({transform: 'translateX(0%)'})),
      state('right', style({transform: 'translateX(50%)'})),
      transition('left => middle', animate('500ms ease-in')),
      transition('middle => right', animate('500ms ease-in')),
      transition('right => left', animate('500ms ease-in'))
    ])
  ]
})
export class HeaderTitleComponent implements OnInit {

  headerAnimationState = 'middle';

  constructor() { }

  ngOnInit(): void {
  }

  toggleHeaderAnimationState() {
    if (this.headerAnimationState === 'left') {
      this.headerAnimationState = 'middle';
    } else if (this.headerAnimationState === 'middle') {
      this.headerAnimationState = 'right';
    } else if (this.headerAnimationState === 'right') {
      this.headerAnimationState = 'left';
    }
  }
}

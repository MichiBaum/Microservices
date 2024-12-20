import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Card} from "primeng/card";
import {Button} from "primeng/button";
import {NgIf, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-home-card',
  imports: [
    Card,
    Button,
    NgOptimizedImage,
    NgIf
  ],
  templateUrl: './home-card.component.html',
  styleUrl: './home-card.component.scss'
})
export class HomeCardComponent {

  @Input() headerImage: string | undefined;
  @Input() titleText: string | undefined;
  @Input() content: string | undefined;
  @Input() buttonLabel: string | undefined;

  @Output() navigate = new EventEmitter<void>();

  constructor(

  ) {}

  navigateClicked() {
    this.navigate.emit();
  }
}

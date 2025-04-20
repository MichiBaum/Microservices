import {Component, input, output} from '@angular/core';
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
  styleUrl: './home-card.component.css'
})
export class HomeCardComponent {

  readonly headerImage = input<string>();
  readonly titleText = input<string>();
  readonly content = input<string>();
  readonly buttonLabel = input<string>();

  readonly navigate = output<void>();

  constructor() {}

}



import {Component, input, output} from '@angular/core';
import {Card} from "primeng/card";
import {Button} from "primeng/button";
import { NgOptimizedImage } from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {IconDefinition} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-home-card',
  imports: [
    Card,
    Button,
    NgOptimizedImage,
    FaIconComponent
],
  templateUrl: './home-card.component.html',
  styleUrl: './home-card.component.css'
})
export class HomeCardComponent {

  readonly priority = input<boolean>(false);
  readonly headerImage = input<string>();
  readonly titleText = input<string>();
  readonly icon = input<IconDefinition>();
  readonly content = input<string>();
  readonly buttonLabel = input<string>();

  readonly navigate = output<void>();

  constructor() {}

}



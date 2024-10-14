import { Component } from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    Button,
    CardModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(private headerService: HeaderService) {
    this.headerService.changeTitle(Sides.home.translationKey)
  }

}

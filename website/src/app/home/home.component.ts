import { Component } from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {Button} from "primeng/button";
import {CardModule} from "primeng/card";
import {RouterNavigationService} from "../core/services/router-navigation.service";

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

  constructor(private headerService: HeaderService, protected routerNavigationService: RouterNavigationService) {
    this.headerService.changeTitle(Sides.home.translationKey)
  }

}

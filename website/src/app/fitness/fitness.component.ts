import { Component } from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {FitnessService} from "../core/services/fitness.service";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../core/services/router-navigation.service";

@Component({
  selector: 'app-fitness',
  standalone: true,
  imports: [
    Button
  ],
  templateUrl: './fitness.component.html',
  styleUrl: './fitness.component.scss'
})
export class FitnessComponent {

  constructor(private headerService: HeaderService, private fitnessService: FitnessService, private router: RouterNavigationService) {
    this.headerService.changeTitle(Sides.fitness.translationKey)
  }

  getTokenUrl(){
    this.fitnessService.getToken().subscribe(token => this.router.open(token.url));
  }

}

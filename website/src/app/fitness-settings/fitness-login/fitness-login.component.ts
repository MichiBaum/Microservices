import { Component } from '@angular/core';
import {Button} from "primeng/button";
import {FitnessService} from "../../core/services/fitness.service";
import {RouterNavigationService} from "../../core/services/router-navigation.service";

@Component({
  selector: 'app-fitness-login',
  standalone: true,
  imports: [
    Button
  ],
  templateUrl: './fitness-login.component.html',
  styleUrl: './fitness-login.component.scss'
})
export class FitnessLoginComponent {

  constructor(private readonly fitnessService: FitnessService, private readonly router: RouterNavigationService) { }

  getTokenUrl(){
    this.fitnessService.getToken().subscribe(token => this.router.openPopup(token.url.replace(/ /g, "")));
  }

}
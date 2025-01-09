import {Component, inject} from '@angular/core';
import {Button} from "primeng/button";
import {FitnessService} from "../../core/api-services/fitness.service";
import {RouterNavigationService} from "../../core/services/router-navigation.service";

@Component({
  selector: 'app-fitness-login',
  imports: [
    Button
  ],
  templateUrl: './fitness-login.component.html',
  styleUrl: './fitness-login.component.scss'
})
export class FitnessLoginComponent {
  private readonly fitnessService = inject(FitnessService);
  private readonly router = inject(RouterNavigationService);


  getTokenUrl(){
    this.fitnessService.getToken().subscribe(token => this.router.openPopup(token.url.replace(/ /g, "")));
  }

}

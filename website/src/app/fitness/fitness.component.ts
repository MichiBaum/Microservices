import {Component, Inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {FitnessService} from "../core/services/fitness.service";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {ChartModule} from "primeng/chart";
import {Weight} from "../core/models/fitness/weight.model";
import {DOCUMENT} from "@angular/common";
import {FitnessWeightComponent} from "./fitness-weight/fitness-weight.component";
import {FitnessProfileComponent} from "./fitness-profile/fitness-profile.component";
import {FieldsetModule} from "primeng/fieldset";
import {SelectButtonModule} from "primeng/selectbutton";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-fitness',
  standalone: true,
  imports: [
    Button,
    ChartModule,
    FitnessWeightComponent,
    FitnessProfileComponent,
    FieldsetModule,
    SelectButtonModule,
    FormsModule
  ],
  templateUrl: './fitness.component.html',
  styleUrl: './fitness.component.scss'
})
export class FitnessComponent implements OnInit{
  settingsCollapsed: boolean = true;
  cols: string = "col-6";

  screenSizeOptions: any[] = [{ label: 'Small', value: 'col-12' },{ label: 'Big', value: 'col-6' }];
  screenSize: string = 'col-12';

  constructor(private headerService: HeaderService, private fitnessService: FitnessService, private router: RouterNavigationService) {
  }

  getTokenUrl(){
    this.fitnessService.getToken().subscribe(token => this.router.openPopup(token.url.replace(/ /g, "")));
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.fitness.translationKey)
  }

}

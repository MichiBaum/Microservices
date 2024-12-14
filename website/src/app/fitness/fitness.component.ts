import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {ChartModule} from "primeng/chart";
import {FitnessWeightComponent} from "./fitness-weight/fitness-weight.component";
import {FitnessProfileComponent} from "./fitness-profile/fitness-profile.component";
import {FieldsetModule} from "primeng/fieldset";
import {SelectButtonModule} from "primeng/selectbutton";
import {FormsModule} from "@angular/forms";
import {FitnessSleepComponent} from "./fitness-sleep/fitness-sleep.component";

@Component({
  selector: 'app-fitness',
  standalone: true,
  imports: [
    ChartModule,
    FitnessWeightComponent,
    FitnessProfileComponent,
    FieldsetModule,
    SelectButtonModule,
    FormsModule,
    FitnessSleepComponent
  ],
  templateUrl: './fitness.component.html',
  styleUrl: './fitness.component.scss'
})
export class FitnessComponent implements OnInit{
  settingsCollapsed: boolean = true;

  screenSizeOptions: any[] = [{ label: 'Small', value: 'col-span-12' },{ label: 'Big', value: 'col-span-6' }];
  screenSize: string = 'col-span-12';

  constructor(
    private readonly headerService: HeaderService,
  ) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.fitness.translationKey)
  }

}

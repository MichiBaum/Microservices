import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {FitnessWeightComponent} from "./fitness-weight/fitness-weight.component";
import {FitnessProfileComponent} from "./fitness-profile/fitness-profile.component";
import {FieldsetModule} from "primeng/fieldset";
import {SelectButtonModule} from "primeng/selectbutton";
import {FormsModule} from "@angular/forms";
import {FitnessSleepComponent} from "./fitness-sleep/fitness-sleep.component";
import {DeferPlaceholderComponent} from "../shared/defer-placeholder/defer-placeholder.component";

@Component({
  selector: 'app-fitness',
  imports: [
    FitnessWeightComponent,
    FitnessProfileComponent,
    FieldsetModule,
    SelectButtonModule,
    FormsModule,
    FitnessSleepComponent,
    DeferPlaceholderComponent
  ],
  templateUrl: './fitness.component.html',
  styleUrl: './fitness.component.css'
})
export class FitnessComponent implements OnInit{
  private readonly headerService = inject(HeaderService);

  settingsCollapsed: boolean = true;

  screenSizeOptions: any[] = [{ label: 'Small', value: 'col-span-12' },{ label: 'Big', value: 'col-span-6' }];
  screenSize: string = 'col-span-12';


  ngOnInit(): void {
    this.headerService.changeTitle(Sides.fitness.translationKey)
  }

}



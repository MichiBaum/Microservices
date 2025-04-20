import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {FitnessLoginComponent} from "./fitness-login/fitness-login.component";

@Component({
  selector: 'app-fitness-settings',
  imports: [
    FitnessLoginComponent
  ],
  templateUrl: './fitness-settings.component.html',
  styleUrl: './fitness-settings.component.css'
})
export class FitnessSettingsComponent implements OnInit{
  private readonly headerService = inject(HeaderService);


  ngOnInit(): void {
    this.headerService.changeTitle(Sides.fitness_settings.translationKey)
  }

}



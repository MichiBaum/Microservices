import { Component, OnInit, inject } from '@angular/core';
import {FitnessService} from "../../core/services/fitness.service";
import {Profile} from "../../core/models/fitness/profile.model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-fitness-profile',
  imports: [
    NgIf
  ],
  templateUrl: './fitness-profile.component.html',
  styleUrl: './fitness-profile.component.scss'
})
export class FitnessProfileComponent implements OnInit{
  private readonly fitnessService = inject(FitnessService);

  protected profile: Profile | undefined;


  ngOnInit(): void {
    this.fitnessService.getProfile().subscribe(value => this.profile = value)
  }

}

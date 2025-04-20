import {Component, inject, OnInit} from '@angular/core';
import {FitnessService} from "../../core/api-services/fitness.service";
import {Profile} from "../../core/models/fitness/profile.model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-fitness-profile',
  imports: [
    NgIf
  ],
  templateUrl: './fitness-profile.component.html',
  styleUrl: './fitness-profile.component.css'
})
export class FitnessProfileComponent implements OnInit{
  private readonly fitnessService = inject(FitnessService);

  protected profile: Profile | undefined;


  ngOnInit(): void {
    this.fitnessService.getProfile().subscribe(value => this.profile = value)
  }

}



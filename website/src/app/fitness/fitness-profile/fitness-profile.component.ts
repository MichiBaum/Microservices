import {Component, OnInit} from '@angular/core';
import {FitnessService} from "../../core/services/fitness.service";
import {Profile} from "../../core/models/fitness/profile.model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-fitness-profile',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './fitness-profile.component.html',
  styleUrl: './fitness-profile.component.scss'
})
export class FitnessProfileComponent implements OnInit{
  protected profile: Profile | undefined;

  constructor(
    private readonly fitnessService: FitnessService
  ) {
  }

  ngOnInit(): void {
    this.fitnessService.getProfile().subscribe(value => this.profile = value)
  }

}

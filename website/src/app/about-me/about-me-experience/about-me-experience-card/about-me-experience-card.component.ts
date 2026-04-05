import {Component, input} from '@angular/core';
import {Card} from "primeng/card";
import {Rating} from "primeng/rating";
import {FormsModule} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-about-me-experience-card',
  imports: [
    Card,
    FormsModule,
  ],
  templateUrl: './about-me-experience-card.component.html',
  styleUrl: './about-me-experience-card.component.css',
})
export class AboutMeExperienceCardComponent {

  title = input.required<string>();
  subtitle = input<string>();
  description = input<string>();

}

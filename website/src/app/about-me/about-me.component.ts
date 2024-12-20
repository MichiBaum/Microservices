import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {CardModule} from "primeng/card";
import {TimelineModule} from "primeng/timeline";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {TranslateModule} from "@ngx-translate/core";
import {ImageModule} from "primeng/image";
import {TableModule} from "primeng/table";
import {RatingModule} from "primeng/rating";
import {FormsModule} from "@angular/forms";
import {NgOptimizedImage} from "@angular/common";
import {Skeleton} from "primeng/skeleton";
import {AboutMeExperienceComponent} from "./about-me-experience/about-me-experience.component";

@Component({
  selector: 'app-about-me',
  imports: [
    CardModule,
    TimelineModule,
    Button,
    TranslateModule,
    ImageModule,
    TableModule,
    RatingModule,
    FormsModule,
    NgOptimizedImage,
    Skeleton,
    AboutMeExperienceComponent
  ],
  templateUrl: './about-me.component.html',
  styleUrl: './about-me.component.scss'
})
export class AboutMeComponent implements OnInit{

  constructor(
    private readonly headerService: HeaderService,
    private readonly routerNavigationService: RouterNavigationService
  ) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.about_me.translationKey)
  }

  /**
   * Navigates to the LinkedIn page using the router navigation service.
   *
   * @return {void} This method does not return a value.
   */
  openLinkedIn(): void {
    this.routerNavigationService.linkedIn()
  }

  /**
   * Navigates the user to the GitHub page using the router navigation service.
   *
   * @return {void}
   */
  openGithub(): void {
    this.routerNavigationService.github()
  }

  /**
   * Navigates the application to the donate page using the router navigation service.
   *
   * @return {void}
   */
  openDonate(): void {
    this.routerNavigationService.donate()
  }
}

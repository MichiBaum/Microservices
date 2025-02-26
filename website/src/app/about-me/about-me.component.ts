import {Component, inject, OnDestroy, OnInit} from '@angular/core';
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
import {MetaDataHolder, MetaService} from "../core/services/meta.service";

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
export class AboutMeComponent implements OnInit, OnDestroy{
  private readonly metaService = inject(MetaService);
  private readonly routerNavigationService = inject(RouterNavigationService);

  private oldMeta: MetaDataHolder = this.metaService.defaultHolder;

    ngOnInit(): void {
        const meta: MetaDataHolder = {
            description: "Michael Baumberger - Software developer from Switzerland specializing in Java, Kotlin, Spring Boot, and Angular. I create modern, scalable, and secure web applications. Contact me for custom software solutions!",
            keywords: ["Michael Baumberger", "Software Developer", "Web Developer", "Java", "Kotlin", "Spring Boot", "Angular", "Web Applications", "IT Security", "Software Architecture", "Backend Development", "Frontend Development", "Custom Software Solutions"],
        }
        this.oldMeta = this.metaService.setNewAndGetOld(meta)
    }

    ngOnDestroy(): void {
        this.metaService.setNewAndGetOld(this.oldMeta)
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

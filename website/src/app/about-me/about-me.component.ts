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
import {AccordionModule} from "primeng/accordion";

@Component({
  selector: 'app-about-me',
  standalone: true,
  imports: [
    CardModule,
    TimelineModule,
    Button,
    TranslateModule,
    ImageModule,
    TableModule,
    RatingModule,
    FormsModule,
    AccordionModule
  ],
  templateUrl: './about-me.component.html',
  styleUrl: './about-me.component.scss'
})
export class AboutMeComponent implements OnInit{
  programmingLanguages: any[] = [
    {"experience": "Java", "years": "7", "rating": 5},
    {"experience": "Kotlin", "years": "3", "rating": 4},
    {"experience": "Spring Boot", "years": "6", "rating": 5},
    {"experience": "Spring Security", "years": "5", "rating": 5},
    {"experience": "Spring Jpa", "years": "6", "rating": 5},
    {"experience": "Spring Mvc", "years": "6", "rating": 5},
    {"experience": "Spring Webflux", "years": "1", "rating": 2},
    {"experience": "Spring Cloud", "years": "1", "rating": 2},
    {"experience": "MariaDB & MySql", "years": "5", "rating": 4},
    {"experience": "OracleDB", "years": "3", "rating": 3},
    {"experience": "H2", "years": "4", "rating": 3},
    {"experience": "Html & CSS", "years": "7", "rating": 4},
    {"experience": "Angular", "years": "3", "rating": 4},
    {"experience": "KnockoutJS", "years": "4", "rating": 3},
    {"experience": "Ajax", "years": "4", "rating": 3},
    {"experience": "C#", "years": "1", "rating": 1},
    {"experience": "Sharepoint OnPrem", "years": "1", "rating": 1},
  ];
  platforms: any[] = [
    {"experience": "Linux", "years": "5", "rating": 4},
    {"experience": "Docker", "years": "5", "rating": 4},
    {"experience": "Git", "years": "5", "rating": 4},
    {"experience": "Svn", "years": "2", "rating": 2},
    {"experience": "Jira", "years": "3", "rating": 3},
    {"experience": "Bitbucket", "years": "3", "rating": 3},
    {"experience": "Confluence", "years": "3", "rating": 3},
    {"experience": "Github", "years": "6", "rating": 4},
  ];
  softskills: any[] = [
    {"experience": "Project Management", "years": "3", "rating": 4},
    {"experience": "Analytical and abstract thinking", "years": "-", "rating": 5},
    {"experience": "Willing to learn and eager to learn", "years": "-", "rating": 5},
    {"experience": "Reliable and Independent", "years": "-", "rating": 5},
    {"experience": "Team player, communication skills and helpfulness", "years": "-", "rating": 5},
    {"experience": "Self-reflection", "years": "-", "rating": 4},
    {"experience": "Willingness to compromise", "years": "-", "rating": 4},
  ];

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

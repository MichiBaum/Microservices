import {Component, Input} from '@angular/core';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from "primeng/accordion";
import {TranslatePipe} from "@ngx-translate/core";
import {TableModule} from "primeng/table";
import {Rating} from "primeng/rating";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-about-me-experience',
  imports: [
    Accordion,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    TranslatePipe,
    TableModule,
    Rating,
    FormsModule
  ],
  templateUrl: './about-me-experience.component.html',
  styleUrl: './about-me-experience.component.scss'
})
export class AboutMeExperienceComponent {

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
    {"experience": "IntelliJ", "years": "5", "rating": 4},
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

}

import {Component, signal} from '@angular/core';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from "primeng/accordion";
import {TranslatePipe} from "@ngx-translate/core";
import {TableModule} from "primeng/table";
import {Rating} from "primeng/rating";
import {FormsModule} from "@angular/forms";
import {AboutMeExperienceCardComponent} from "./about-me-experience-card/about-me-experience-card.component";
import {Card} from "primeng/card";

@Component({
  selector: 'app-about-me-experience',
  imports: [
    TranslatePipe,
    TableModule,
    FormsModule,
    AboutMeExperienceCardComponent,
    Card
  ],
  templateUrl: './about-me-experience.component.html',
  styleUrl: './about-me-experience.component.css'
})
export class AboutMeExperienceComponent {

  currentYear = signal(new Date().getFullYear());

  programmingLanguages = signal([
    {"experience": "Java",              "from": 2018, "until": this.currentYear()},
    {"experience": "Kotlin",            "from": 2021, "until": this.currentYear()},
    {"experience": "Spring Boot",       "from": 2018, "until": this.currentYear()},
    {"experience": "Spring Security",   "from": 2019, "until": this.currentYear()},
    {"experience": "Spring Jpa",        "from": 2018, "until": this.currentYear()},
    {"experience": "Spring Mvc",        "from": 2018, "until": this.currentYear()},
    {"experience": "Spring Webflux",    "from": 2024, "until": this.currentYear()},
    {"experience": "Spring Cloud",      "from": 2023, "until": this.currentYear()},
    {"experience": "MariaDB & MySql",   "from": 2019, "until": this.currentYear()},
    {"experience": "OracleDB",          "from": 2020, "until": 2024,             },
    {"experience": "H2",                "from": 2020, "until": 2024,             },
    {"experience": "Html & CSS",        "from": 2018, "until": this.currentYear()},
    {"experience": "Angular",           "from": 2021, "until": this.currentYear()},
    {"experience": "KnockoutJS",        "from": 2019, "until": 2024,             },
    {"experience": "Ajax",              "from": 2019, "until": 2024,             },
    {"experience": "C#",                "from": 2021, "until": 2022,             },
    {"experience": "Sharepoint OnPrem", "from": 2021, "until": 2022,             },
  ]);
  platforms = signal([
    {"experience": "IntelliJ",      "from": 2018, "until": this.currentYear()},
    {"experience": "Linux",         "from": 2019, "until": this.currentYear()},
    {"experience": "Docker",        "from": 2020, "until": this.currentYear()},
    {"experience": "Git",           "from": 2019, "until": this.currentYear()},
    {"experience": "Svn",           "from": 2018, "until": 2019,             },
    {"experience": "Jira",          "from": 2018, "until": this.currentYear()},
    {"experience": "Bitbucket",     "from": 2020, "until": this.currentYear()},
    {"experience": "Confluence",    "from": 2020, "until": this.currentYear()},
    {"experience": "Github",        "from": 2018, "until": this.currentYear()},
  ]);
  softskills = signal([
    {"experience": "Project Management", "description": "Led several projects"},
    {"experience": "Analytical and abstract thinking", "description": ""},
    {"experience": "Willing to learn and eager to learn", "description": ""},
    {"experience": "Reliable and Independent", "description": ""},
    {"experience": "Team player, communication skills and helpfulness", "description": ""},
    {"experience": "Self-reflection", "description": ""},
    {"experience": "Willingness to compromise", "description": ""},
  ]);

}



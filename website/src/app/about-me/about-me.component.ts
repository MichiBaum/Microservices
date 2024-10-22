import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {CardModule} from "primeng/card";
import {TimelineModule} from "primeng/timeline";
import {Button} from "primeng/button";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {TranslateModule} from "@ngx-translate/core";
import {ImageModule} from "primeng/image";

@Component({
  selector: 'app-about-me',
  standalone: true,
  imports: [
    CardModule,
    TimelineModule,
    Button,
    TranslateModule,
    ImageModule
  ],
  templateUrl: './about-me.component.html',
  styleUrl: './about-me.component.scss'
})
export class AboutMeComponent implements OnInit{

  constructor(private headerService: HeaderService, private routerNavigationService: RouterNavigationService) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.about_me.translationKey)
  }

  openLinkedIn() {
    this.routerNavigationService.linkedIn()
  }

  openGithub() {
    this.routerNavigationService.github()
  }
}

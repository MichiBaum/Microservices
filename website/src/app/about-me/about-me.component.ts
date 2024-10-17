import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";

@Component({
  selector: 'app-about-me',
  standalone: true,
  imports: [],
  templateUrl: './about-me.component.html',
  styleUrl: './about-me.component.scss'
})
export class AboutMeComponent implements OnInit{

  constructor(private headerService: HeaderService) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.about_me.translationKey)
  }

}

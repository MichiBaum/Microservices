import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from '../core/config/sides';
import {RouterOutlet} from "@angular/router";
import {MusicNavigationComponent} from "./music-navigation/music-navigation.component";

@Component({
  selector: 'app-music',
    imports: [
        RouterOutlet,
        MusicNavigationComponent
    ],
  templateUrl: './music.component.html',
  styleUrl: './music.component.scss'
})
export class MusicComponent implements OnInit{
  private readonly headerService = inject(HeaderService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music.translationKey)
  }

}

import {Component, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from '../core/config/sides';

@Component({
  selector: 'app-music',
  standalone: true,
  imports: [

  ],
  templateUrl: './music.component.html',
  styleUrl: './music.component.scss'
})
export class MusicComponent implements OnInit{

  constructor(
    private readonly headerService: HeaderService,
  ) {
  }

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music.translationKey)
  }

}

import { Component, OnInit, inject } from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from '../core/config/sides';

@Component({
  selector: 'app-music',
  imports: [],
  templateUrl: './music.component.html',
  styleUrl: './music.component.scss'
})
export class MusicComponent implements OnInit{
  private readonly headerService = inject(HeaderService);

  ngOnInit(): void {
    this.headerService.changeTitle(Sides.music.translationKey)
  }

}

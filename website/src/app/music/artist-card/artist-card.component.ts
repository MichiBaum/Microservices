import { Component, input } from '@angular/core';
import { Card } from 'primeng/card';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { faUser } from '@fortawesome/free-solid-svg-icons';
import { Artist } from '../../core/models/music/artist.model';

@Component({
  selector: 'app-artist-card',
  imports: [
    Card,
    FaIconComponent
  ],
  templateUrl: './artist-card.component.html',
  styleUrl: './artist-card.component.scss'
})
export class ArtistCardComponent {
  readonly artist = input.required<Artist>();
  readonly faUser = faUser;
}

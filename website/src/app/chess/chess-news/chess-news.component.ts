import {Component, OnInit} from '@angular/core';
import {Message} from "primeng/message";

@Component({
  selector: 'app-chess-news',
  standalone: true,
  imports: [
    Message
  ],
  templateUrl: './chess-news.component.html',
  styleUrl: './chess-news.component.scss'
})
export class ChessNewsComponent implements OnInit {

  ngOnInit(): void {
  }


}

import {Component, OnInit} from '@angular/core';
import {MessagesModule} from "primeng/messages";
import {ToastMessageOptions} from "primeng/api";

@Component({
  selector: 'app-chess-news',
  standalone: true,
  imports: [
    MessagesModule
  ],
  templateUrl: './chess-news.component.html',
  styleUrl: './chess-news.component.scss'
})
export class ChessNewsComponent implements OnInit {
  messages: ToastMessageOptions[] = [
    { severity: 'error', detail: 'Not implemented yet!' }
  ];

  ngOnInit(): void {
  }


}

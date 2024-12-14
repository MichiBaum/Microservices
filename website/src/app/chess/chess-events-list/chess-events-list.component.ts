import {Component, OnInit} from '@angular/core';
import {ChessService} from "../../core/services/chess.service";
import {CardModule} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {ChessEvent, ChessEventCategoryWithEvents} from "../../core/models/chess/chess.models";
import {TimelineModule} from "primeng/timeline";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faClock} from "@fortawesome/free-regular-svg-icons";
import {faCalendarDay, faCalendarPlus, faCalendarXmark} from "@fortawesome/free-solid-svg-icons";
import {ScrollTopModule} from "primeng/scrolltop";

@Component({
  selector: 'app-chess-events-list',
  standalone: true,
  imports: [
    CardModule,
    NgForOf,
    RouterLink,
    TimelineModule,
    FaIconComponent,
    ScrollTopModule,
    NgIf
  ],
  templateUrl: './chess-events-list.component.html',
  styleUrl: './chess-events-list.component.scss'
})
export class ChessEventsListComponent implements OnInit {

  categories: ChessEventCategoryWithEvents[] = []

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnInit(): void {
    this.chessService.eventCategoriesWithEvents().subscribe(categories => {
      this.categories = [...categories];
    })
  }

  getColor(event: ChessEvent){
    if(event.dateFrom && event.dateTo){
      const dateFrom = new Date(event.dateFrom)
      const dateTo = new Date(event.dateTo)
      const current = new Date()
      if(dateTo > current && dateFrom < current){ return "color: green"}
      if(dateTo < current ){ return "color: red"}
      if(dateFrom > current){ return "color: #0688fb"}
    }
    return ""
  }

  getIcon(event: ChessEvent) {
    if(event.dateFrom && event.dateTo){
      const dateFrom = new Date(event.dateFrom)
      const dateTo = new Date(event.dateTo)
      const current = new Date()
      if(dateTo > current && dateFrom < current){ return faCalendarDay}
      if(dateTo < current ){ return faCalendarXmark}
      if(dateFrom > current){ return faCalendarPlus}
    }
    return faClock;
  }
}

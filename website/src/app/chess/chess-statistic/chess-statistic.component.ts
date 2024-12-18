import {Component, OnInit} from '@angular/core';
import {TabMenuModule} from "primeng/tabmenu";
import {TabViewModule} from "primeng/tabview";

@Component({
  selector: 'app-chess-statistic',
  imports: [
    TabMenuModule,
    TabViewModule
  ],
  templateUrl: './chess-statistic.component.html',
  styleUrl: './chess-statistic.component.scss'
})
export class ChessStatisticComponent implements OnInit {

  constructor() { }

  ngOnInit() {

  }
}

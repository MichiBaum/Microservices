import {Component, OnInit} from '@angular/core';
import {TabMenuModule} from "primeng/tabmenu";
import {MenuItem} from "primeng/api";
import {TabViewModule} from "primeng/tabview";

@Component({
  selector: 'app-chess-statistic',
  standalone: true,
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

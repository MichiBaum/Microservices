import {Component} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {MenubarModule} from "primeng/menubar";
import {BadgeModule} from "primeng/badge";
import {TagModule} from "primeng/tag";
import {RouterOutlet} from "@angular/router";
import {ChessNavigationComponent} from "./chess-navigation/chess-navigation.component";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-chess',
    imports: [
        SplitterModule,
        MenubarModule,
        BadgeModule,
        TagModule,
        RouterOutlet,
        ChessNavigationComponent,
        TranslateModule
    ],
  templateUrl: './chess.component.html',
  styleUrl: './chess.component.css'
})
export class ChessComponent {

}



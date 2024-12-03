import {EventEmitter, Output, Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {ChessEvent, ChessGame} from "../../../core/models/chess/chess.models";
import {ChessService} from "../../../core/services/chess.service";
import LichessPgnViewer from 'lichess-pgn-viewer';
import PgnViewer from "lichess-pgn-viewer/pgnViewer";
import {Button} from "primeng/button";
import {DialogModule} from "primeng/dialog";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-chess-event-games',
  standalone: true,
  imports: [
    Button,
    DialogModule,
    NgForOf
  ],
  templateUrl: './chess-event-games.component.html',
  styleUrl: './chess-event-games.component.scss'
})
export class ChessEventGamesComponent implements OnChanges {
  @Input() event: ChessEvent | undefined;
  games: ChessGame[] | undefined;
  @Output() haveContent = new EventEmitter<boolean>();

  @ViewChild('game',{static:false})
  gameDiv: ElementRef | undefined;

  lpv: PgnViewer | undefined;

  constructor(
    private readonly chessService: ChessService,
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['event'] && changes['event'].currentValue) {
      this.chessService.eventGames(changes['event'].currentValue.id).subscribe(games => {
        this.games = [...games]
        this.haveContent.emit(this.games.length > 0)
      })
    }
  }

  visible: boolean = false;

  showDialog(game: ChessGame) {
    if(game.pgn){
      this.visible = true;
      setInterval(() => {
        this.initGame(game.pgn);
      }, 500);
    }
  }

  initGame(pgn: string): void {
    this.lpv = LichessPgnViewer(this.gameDiv?.nativeElement, {
      pgn: pgn,
    });

  }

}

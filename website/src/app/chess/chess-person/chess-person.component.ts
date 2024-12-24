import {Component, inject, OnDestroy, signal, WritableSignal} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {rxResource} from "@angular/core/rxjs-interop";
import {EMPTY} from "rxjs";
import {TableModule} from "primeng/table";
import {Button} from "primeng/button";
import {ChessService} from "../../core/api-services/chess.service";

@Component({
  selector: 'app-chess-person',
  imports: [
    TableModule,
    Button
  ],
  templateUrl: './chess-person.component.html',
  styleUrl: './chess-person.component.scss'
})
export class ChessPersonComponent implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly chessService = inject(ChessService);

  personId: WritableSignal<string | undefined> = signal("")
  person = rxResource({
    request: () => ({id: this.personId()}),
    loader: (param) => {
      if(param.request.id == undefined)
        return EMPTY
      return this.chessService.person(param.request.id)
    }
  })

  routeParamsSubscription = this.route.params.subscribe(params => {
    const id = params['id'];
    this.personId.set(id)
  });

  ngOnDestroy() {
    this.routeParamsSubscription.unsubscribe()
  }

}

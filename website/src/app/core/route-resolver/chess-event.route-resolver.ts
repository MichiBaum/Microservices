import {ActivatedRouteSnapshot, MaybeAsync, RedirectCommand, Resolve, RouterStateSnapshot} from "@angular/router";
import {ChessEvent} from "../models/chess/chess.models";
import {ChessService} from "../api-services/chess.service";
import {EMPTY} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({ providedIn: 'root' })
export class ChessEventRouteResolver implements Resolve<ChessEvent>{

    constructor(private readonly chessService:ChessService) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<RedirectCommand | ChessEvent> {
        let id = route.paramMap.get('id');
        if(id == undefined)
            return EMPTY
        return this.chessService.event(id)
    }

}

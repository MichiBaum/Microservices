import {
    ActivatedRouteSnapshot,
    ResolveFn,
    RouterStateSnapshot
} from "@angular/router";
import {ChessService} from "../api-services/chess.service";
import {EMPTY} from "rxjs";
import {inject} from "@angular/core";

export const chessEventRouteResolver: ResolveFn<any> = (
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
) => {
    const chessService = inject(ChessService);

    let id = route.paramMap.get('id');
    if(id == undefined)
        return EMPTY
    return chessService.event(id)
};

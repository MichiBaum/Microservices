import {
    ActivatedRouteSnapshot,
    ResolveFn, Router,
    RouterStateSnapshot
} from "@angular/router";
import {ChessService} from "../api-services/chess.service";
import {catchError, EMPTY, map} from "rxjs";
import {inject} from "@angular/core";

export const chessEventRouteResolver: ResolveFn<any> = (
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
) => {
    const chessService = inject(ChessService);
    const router = inject(Router);

    let id = route.paramMap.get('id');
    if(id == undefined)
        return EMPTY
    return chessService.event(id)
        .pipe(catchError(err => router.navigate(['/chess/events'])))
};

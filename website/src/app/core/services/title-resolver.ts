import {ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot} from "@angular/router";
import {inject} from "@angular/core";
import {Title} from "@angular/platform-browser";
import {HeaderService} from "./header.service";

export const titleResolver: ResolveFn<string> = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const title = inject(Title)
    const header = inject(HeaderService)

    // TODO
    // route.data Data Like event in route events/:id
    console.log(route)

    return ""
};

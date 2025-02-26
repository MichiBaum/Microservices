import {ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot} from "@angular/router";
import {inject} from "@angular/core";
import {HeaderService} from "../services/header.service";
import {TranslateService} from "@ngx-translate/core";

export const titleResolver: ResolveFn<string> = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const headerService = inject(HeaderService)
    const translate = inject(TranslateService);

    let data = route.data;
    const tabTitleFun = data['tabTitle']
    const tabTitle = tabTitleFun(translate).toPromise()

    const headerTitleFun = data['headerTitle']
    headerTitleFun(headerService)

    return tabTitle
};

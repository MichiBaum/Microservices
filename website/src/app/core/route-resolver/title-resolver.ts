import {ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot} from "@angular/router";
import {inject} from "@angular/core";
import {HeaderService} from "../services/header.service";
import {TranslateService} from "@ngx-translate/core";
import {Meta} from "@angular/platform-browser";
import {Observable} from "rxjs";

export const titleResolver: ResolveFn<string> = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const headerService = inject(HeaderService);
    const translate = inject(TranslateService);
    const meta = inject(Meta);

    let data = route.data;
    const tabTitleFun = data['tabTitle']
    const tabTitle = (tabTitleFun(translate, route) as Observable<any>).toPromise()

    const headerTitleFun = data['headerTitle']
    headerTitleFun(headerService, route)

    const metaDescriptionFun = data['metaDescription']
    const metaDescription = (metaDescriptionFun(translate, route) as Observable<any>)
    metaDescription.subscribe(value => meta.updateTag({name: 'description', content: value}))

    return tabTitle
};

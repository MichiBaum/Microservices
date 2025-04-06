import {Component} from '@angular/core';
import {MsApplicationsComponent} from "./ms-applications/ms-applications.component";
import {CookieAdminComponent} from "./cookie-admin/cookie-admin.component";

@Component({
  selector: 'app-microservice-overview',
    imports: [
        MsApplicationsComponent,
        CookieAdminComponent
    ],
  templateUrl: './microservice-overview.component.html',
  styleUrl: './microservice-overview.component.scss'
})
export class MicroserviceOverviewComponent {
}

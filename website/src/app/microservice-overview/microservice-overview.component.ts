import {Component} from '@angular/core';
import {MsApplicationsComponent} from "./ms-applications/ms-applications.component";

@Component({
  selector: 'app-microservice-overview',
  imports: [
    MsApplicationsComponent
  ],
  templateUrl: './microservice-overview.component.html',
  styleUrl: './microservice-overview.component.scss'
})
export class MicroserviceOverviewComponent {
}

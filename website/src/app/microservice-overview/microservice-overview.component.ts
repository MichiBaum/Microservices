import { Component } from '@angular/core';
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-microservice-overview',
  standalone: true,
  imports: [],
  templateUrl: './microservice-overview.component.html',
  styleUrl: './microservice-overview.component.scss'
})
export class MicroserviceOverviewComponent {

  protected readonly environment = environment;
}

import {Component, inject, OnInit} from '@angular/core';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";
import {MsApplicationsComponent} from "./ms-applications/ms-applications.component";

@Component({
  selector: 'app-microservice-overview',
  imports: [
    MsApplicationsComponent
  ],
  templateUrl: './microservice-overview.component.html',
  styleUrl: './microservice-overview.component.scss'
})
export class MicroserviceOverviewComponent implements OnInit{
  private readonly headerService = inject(HeaderService);


  ngOnInit(): void {
    this.headerService.changeTitle(Sides.microservices.translationKey)
  }
}

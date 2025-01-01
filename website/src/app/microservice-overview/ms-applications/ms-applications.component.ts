import {Component, inject} from '@angular/core';
import {Card} from "primeng/card";
import {NgForOf, NgIf} from "@angular/common";
import {AdminService} from "../../core/services/admin.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {MsApplicationComponent} from "../ms-application/ms-application.component";

@Component({
  selector: 'app-ms-applications',
  imports: [
    Card,
    NgForOf,
    NgIf,
    MsApplicationComponent
  ],
  templateUrl: './ms-applications.component.html',
  styleUrl: './ms-applications.component.scss'
})
export class MsApplicationsComponent {
  private readonly adminService = inject(AdminService);

  applications = rxResource({
    loader: () => this.adminService.applications()
  })
}

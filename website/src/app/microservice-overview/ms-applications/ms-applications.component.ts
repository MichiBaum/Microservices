import {Component, inject} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {AdminService} from "../../core/api-services/admin.service";
import {rxResource} from "@angular/core/rxjs-interop";
import {MsApplicationComponent} from "../ms-application/ms-application.component";

@Component({
  selector: 'app-ms-applications',
  imports: [
    NgForOf,
    NgIf,
    MsApplicationComponent
  ],
  templateUrl: './ms-applications.component.html',
  styleUrl: './ms-applications.component.css'
})
export class MsApplicationsComponent {
  private readonly adminService = inject(AdminService);

  applications = rxResource({
    loader: () => this.adminService.applications()
  })
}



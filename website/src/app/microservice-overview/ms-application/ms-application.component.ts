import {Component, input, Input} from '@angular/core';
import {Card} from "primeng/card";
import {NgForOf} from "@angular/common";
import {Application} from "../../core/models/admin/admin.model";
import {MsStatusIconPipe} from "../pipes/ms-status-icon.pipe";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";
import {MsStatusStylePipe} from "../pipes/ms-status-style.pipe";

@Component({
  selector: 'app-ms-application',
  imports: [
    Card,
    NgForOf,
    MsStatusIconPipe,
    FaIconComponent,
    EventIconColorPipe,
    MsStatusStylePipe
  ],
  templateUrl: './ms-application.component.html',
  styleUrl: './ms-application.component.scss'
})
export class MsApplicationComponent {
  application = input<Application>()
}

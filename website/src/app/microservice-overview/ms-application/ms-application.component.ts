import {Component, input} from '@angular/core';
import {Card} from "primeng/card";
import {KeyValuePipe, NgForOf} from "@angular/common";
import {Application} from "../../core/models/admin/admin.model";
import {MsStatusIconPipe} from "../pipes/ms-status-icon.pipe";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {MsStatusStylePipe} from "../pipes/ms-status-style.pipe";
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from "primeng/accordion";

@Component({
  selector: 'app-ms-application',
  imports: [
    Card,
    NgForOf,
    MsStatusIconPipe,
    FaIconComponent,
    MsStatusStylePipe,
    KeyValuePipe,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    Accordion
  ],
  templateUrl: './ms-application.component.html',
  styleUrl: './ms-application.component.scss'
})
export class MsApplicationComponent {
  application = input<Application>()
}

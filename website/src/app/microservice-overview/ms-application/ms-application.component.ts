import {Component, input, Input} from '@angular/core';
import {Card} from "primeng/card";
import {JsonPipe, KeyValuePipe, NgForOf} from "@angular/common";
import {Application} from "../../core/models/admin/admin.model";
import {MsStatusIconPipe} from "../pipes/ms-status-icon.pipe";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {EventIconColorPipe} from "../../core/pipes/event-icon-color.pipe";
import {MsStatusStylePipe} from "../pipes/ms-status-style.pipe";
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from "primeng/accordion";

@Component({
  selector: 'app-ms-application',
  imports: [
    Card,
    NgForOf,
    MsStatusIconPipe,
    FaIconComponent,
    EventIconColorPipe,
    MsStatusStylePipe,
    JsonPipe,
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

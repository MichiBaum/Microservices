import {Component} from '@angular/core';
import {CardModule} from "primeng/card";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-imprint',
  imports: [
    CardModule,
    TranslateModule
  ],
  templateUrl: './imprint.component.html',
  styleUrl: './imprint.component.scss'
})
export class ImprintComponent {

}

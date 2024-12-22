import {Component, input} from '@angular/core';
import {Skeleton} from "primeng/skeleton";

@Component({
  selector: 'app-defer-placeholder',
  imports: [
    Skeleton
  ],
  templateUrl: './defer-placeholder.component.html',
  styleUrl: './defer-placeholder.component.scss'
})
export class DeferPlaceholderComponent {

  height = input<string>("2rem")

}

import {Component, input} from '@angular/core';

@Component({
  selector: 'app-defer-placeholder',
  imports: [
  ],
  templateUrl: './defer-placeholder.component.html',
  styleUrl: './defer-placeholder.component.css'
})
export class DeferPlaceholderComponent {

  height = input<string>("2rem")

}



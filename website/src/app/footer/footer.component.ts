import { Component } from '@angular/core';
import {Footer} from "primeng/api";

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [
    Footer
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {

}

import { Component, OnInit } from '@angular/core';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-backend-documentation',
  templateUrl: './backend-documentation.component.html',
  styleUrls: ['./backend-documentation.component.scss']
})
export class BackendDocumentationComponent implements OnInit {

  swaggerUrl: string;

  constructor() {
    this.swaggerUrl = environment.swagger_url;
  }

  ngOnInit(): void {
  }

}

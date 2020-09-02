import {Component, OnInit} from '@angular/core';
import {SwUpdate} from '@angular/service-worker';
import { PrimeNGConfig } from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
/**
 * The App Component
 */
export class AppComponent implements OnInit{

  /**
   * Constructor which subscribes to the {@link SwUpdate} and updates
   * @param swupdate the {@link SwUpdate}
   */
  constructor(private swupdate: SwUpdate, private primengConfig: PrimeNGConfig) {
    // checks if update available
    this.swupdate.available.subscribe((event: any) => {
      // reload / refresh the browser
      this.swupdate.activateUpdate().then(() => document.location.reload());
    });
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true; // TODO add ripple to some elements
  }

}

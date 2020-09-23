import { Component } from '@angular/core';
import {SwUpdate} from '@angular/service-worker';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
/**
 * The App Component
 */
export class AppComponent {

  /**
   * Constructor which subscribes to the {@link SwUpdate} and updates
   * @param swupdate the {@link SwUpdate}
   */
  constructor(private swupdate: SwUpdate) {
    // checks if update available
    this.swupdate.available.subscribe((event: any) => {
      // reload / refresh the browser
      this.swupdate.activateUpdate().then(() => document.location.reload());
    });
  }

}

import {Component, inject} from '@angular/core';
import {DOCUMENT} from "@angular/common";
import {InputSwitchModule} from "primeng/inputswitch";
import {FormsModule} from "@angular/forms";
import {ToggleButtonModule} from "primeng/togglebutton";

@Component({
  selector: 'app-light-dark-mode',
  standalone: true,
  imports: [
    InputSwitchModule,
    FormsModule,
    ToggleButtonModule
  ],
  templateUrl: './light-dark-mode.component.html',
  styleUrl: './light-dark-mode.component.scss'
})
export class LightDarkModeComponent {
  #document = inject(DOCUMENT);
  isDarkMode = false;

  constructor() {
    if (this.isSystemDark()) {
      this.toggleLightDark();
    }
  }

  isSystemDark(): boolean {
    return window?.matchMedia?.('(prefers-color-scheme:dark)')?.matches;
  }

  toggleLightDark() {
    const linkElement = this.#document.getElementById(
      'app-theme',
    ) as HTMLLinkElement;
    if (linkElement.href.includes('light')) {
      linkElement.href = 'theme-dark.css';
      this.isDarkMode = true;
    } else {
      linkElement.href = 'theme-light.css';
      this.isDarkMode = false;
    }
  }
}

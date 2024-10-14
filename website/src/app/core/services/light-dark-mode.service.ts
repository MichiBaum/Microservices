import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class LightDarkModeService {

  isDarkMode = false;

  isSystemDark(): boolean {
    return window?.matchMedia?.('(prefers-color-scheme:dark)')?.matches;
  }

  changeMode(document: Document){
    const linkElement = document.getElementById(
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

  changeModeTo(document: Document, lightDarkMode: LightDarkMode){
    const linkElement = document.getElementById(
      'app-theme',
    ) as HTMLLinkElement;

    if(lightDarkMode == LightDarkMode.dark){
      linkElement.href = 'theme-dark.css';
      this.isDarkMode = true;
    }

    if(lightDarkMode == LightDarkMode.light){
      linkElement.href = 'theme-light.css';
      this.isDarkMode = false;
    }
  }

}

export enum LightDarkMode{
  light,
  dark
}

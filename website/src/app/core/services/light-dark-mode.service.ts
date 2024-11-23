import {Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class LightDarkModeService {

  currentMode(): LightDarkMode{
    const theme: string | null = localStorage.getItem("theme");
    if(theme == null){
      return this.isSystemDark() ? LightDarkMode.dark : LightDarkMode.light;
    }
    // @ts-ignore
    return LightDarkMode[theme]
  }

  private saveMode(mode: LightDarkMode){
    localStorage.setItem("theme", LightDarkMode[mode]);
  }

  isSystemDark(): boolean {
    return window?.matchMedia?.('(prefers-color-scheme:dark)')?.matches;
  }

  changeMode(document: Document){
    const linkElement = document.getElementById(
      'app-theme',
    ) as HTMLLinkElement;
    if (linkElement.href.includes('light')) {
      linkElement.href = 'theme-dark.css';
      this.saveMode(LightDarkMode.dark)
    } else {
      linkElement.href = 'theme-light.css';
      this.saveMode(LightDarkMode.light)
    }
  }

  changeModeTo(document: Document, lightDarkMode: LightDarkMode){
    const linkElement = document.getElementById(
      'app-theme',
    ) as HTMLLinkElement;

    if(lightDarkMode == LightDarkMode.dark){
      linkElement.href = 'theme-dark.css';
      this.saveMode(LightDarkMode.dark)
    }

    if(lightDarkMode == LightDarkMode.light){
      linkElement.href = 'theme-light.css';
      this.saveMode(LightDarkMode.light)
    }
  }

  init(document: Document) {
    const current = this.currentMode();
    this.changeModeTo(document, current)
  }
}

export enum LightDarkMode{
  light,
  dark
}

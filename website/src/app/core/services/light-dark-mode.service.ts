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
    const element = document.querySelector('html');
    if(element == null){
      return;
    }

    const darkModeEnabled = element.classList.contains('dark');
    if (darkModeEnabled) {
      this.saveMode(LightDarkMode.light)
      element.classList.remove('dark');
    } else {
      this.saveMode(LightDarkMode.dark)
      element.classList.add('dark');
    }
  }

  changeModeTo(document: Document, lightDarkMode: LightDarkMode){
    const element = document.querySelector('html');
    if(element == null){
      return;
    }


    if(lightDarkMode == LightDarkMode.light){
      const darkModeEnabled = element.classList.contains('dark');
      if (darkModeEnabled) {
        this.saveMode(LightDarkMode.light)
        element.classList.remove('dark');
      }
      return;
    }
    if(lightDarkMode == LightDarkMode.dark){
      const darkModeEnabled = element.classList.contains('dark');
      if (!darkModeEnabled) {
        this.saveMode(LightDarkMode.dark)
        element.classList.add('dark');
      }
      return;
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

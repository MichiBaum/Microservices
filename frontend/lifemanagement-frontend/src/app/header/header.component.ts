import {Component, OnInit} from '@angular/core';
import {LanguageConfig} from '../core/language.config';
import {PrimeNgBase} from '../core/models/primeng-base.model';
import {AuthService} from '../core/services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  languages: PrimeNgBase[];
  selectedLanguage: PrimeNgBase;

  constructor(public languageConfig: LanguageConfig, public authService: AuthService) { }

  ngOnInit(): void {
    this.initLanguages();
    this.selectedLanguage = this.initSelectedLanguage();
  }

  initLanguages = () => {
    this.languages = this.languageConfig.languages.map(lang => {
      return {label: lang.name, field: lang.isoCode, value: lang.isoCode} as PrimeNgBase;
    });
  }

  selectLanguage = (event) => {
    this.languageConfig.setLanguage(
      this.languageConfig.languages.find(lang => lang.isoCode === event.value)
    );
  }

  private initSelectedLanguage = (): PrimeNgBase => {
    return {field: this.languageConfig.current.isoCode, label: this.languageConfig.current.name} as PrimeNgBase;
  }
}

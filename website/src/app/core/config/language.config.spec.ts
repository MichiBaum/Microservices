import {TestBed} from '@angular/core/testing';
import {LanguageConfig} from './language.config';
import {TranslateService} from '@ngx-translate/core';
import {Language} from "../models/language.model";

describe('LanguageConfig', () => {
    let languageConfig: LanguageConfig;
    let mockTranslateService: jasmine.SpyObj<TranslateService>;

    const languages: Language[] = [
        {name: 'English', isoCode: 'en'},
        {name: 'Spanish', isoCode: 'es'}
    ];
    const defaultLanguage: Language = {name: 'English', isoCode: 'en'};

    beforeEach(() => {
        mockTranslateService = jasmine.createSpyObj('TranslateService', [
            'use',
            'setDefaultLang',
            'getBrowserLang'
        ]);

        TestBed.configureTestingModule({
            providers: [
                LanguageConfig,
                {provide: TranslateService, useValue: mockTranslateService}
            ]
        });

        languageConfig = TestBed.inject(LanguageConfig);
        languageConfig.languages = languages;
        languageConfig.defaultLanguage = defaultLanguage;
    });

    it('should create an instance', () => {
        expect(languageConfig).toBeTruthy();
    });

    it('should set and emit the language', () => {
        const language: Language = {name: 'Spanish', isoCode: 'es'};
        spyOn(languageConfig.languageChanged, 'emit');

        languageConfig.setLanguage(language);

        expect(languageConfig.current).toBe(language);
        expect(mockTranslateService.use).toHaveBeenCalledWith(language.isoCode);
        expect(mockTranslateService.setDefaultLang).toHaveBeenCalledWith(language.isoCode);
        expect(localStorage.setItem).toHaveBeenCalledWith(languageConfig.localStorageKey, language.isoCode); // TODO Error: <toHaveBeenCalledWith> : Expected a spy, but got Function.
        expect(languageConfig.languageChanged.emit).toHaveBeenCalledWith(language);
    });

    it('should initialize language from localStorage', () => {
        spyOn(localStorage, 'getItem').and.returnValue('es');
        spyOn(languageConfig, 'setLanguage');

        languageConfig['setInitLanguage']();

        expect(localStorage.getItem).toHaveBeenCalledWith('languageIso');
        expect(languageConfig.setLanguage).toHaveBeenCalledWith(languages[1]);
    });

    it('should initialize language from browser language', () => {
        mockTranslateService.getBrowserLang.and.returnValue('es');
        spyOn(languageConfig, 'setLanguage');

        localStorage.removeItem('languageIso');
        languageConfig['setInitLanguage']();

        expect(mockTranslateService.getBrowserLang).toHaveBeenCalled(); // TODO Expected spy TranslateService.getBrowserLang to have been called.
        expect(languageConfig.setLanguage).toHaveBeenCalledWith(languages[1]);
    });

    it('should initialize language to default if no localStorage or browser language', () => {
        spyOn(languageConfig, 'setLanguage');

        localStorage.removeItem('languageIso');
        mockTranslateService.getBrowserLang.and.returnValue(undefined);
        languageConfig['setInitLanguage']();

        expect(languageConfig.setLanguage).toHaveBeenCalledWith(languages[0]);
        // TODO Expected spy setLanguage to have been called with:
        //    [ Object({ name: 'English', isoCode: 'en' }) ]
        //  but actual calls were:
        //    [ Object({ name: 'languages.english', isoCode: 'en' }) ].
        //  Call 0:
        //    Expected $[0].name = 'languages.english' to equal 'English'.
    });

    it('should retrieve language from localStorage', () => {
        spyOn(localStorage, 'getItem').and.returnValue('es');

        const language = languageConfig['fromLocalStorage']();

        expect(localStorage.getItem).toHaveBeenCalledWith(languageConfig.localStorageKey);
        expect(language).toEqual(languages[1]);
    });

    it('should retrieve browser language', () => {
        mockTranslateService.getBrowserLang.and.returnValue('en');

        const language = languageConfig['getBrowserLanguage']();

        expect(mockTranslateService.getBrowserLang).toHaveBeenCalled();
        expect(language).toEqual(languages[0]);
    });

    it('should return default language if isoCode is undefined', () => {
        const language = languageConfig['getFromList'](undefined);

        expect(language).toEqual(defaultLanguage); // TODO Expected $.name = 'languages.english' to equal 'English'.
    });

    it('should return language from the list by isoCode', () => {
        const language = languageConfig['getFromList']('es');

        expect(language).toEqual(languages[1]);
    });

});

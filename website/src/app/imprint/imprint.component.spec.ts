import {ComponentFixture, TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {ImprintComponent} from './imprint.component';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {CardModule} from 'primeng/card';
import {HeaderService} from "../core/services/header.service";
import {Sides} from "../core/config/sides";

describe('ImprintComponent', () => {
    let component: ImprintComponent;
    let fixture: ComponentFixture<ImprintComponent>;
    let headerService: HeaderService;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot(), CardModule, ImprintComponent],
            declarations: [],
            providers: [HeaderService, TranslateService, provideHttpClient()],
        }).compileComponents();

        fixture = TestBed.createComponent(ImprintComponent);
        component = fixture.componentInstance;
        headerService = TestBed.inject(HeaderService);
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should call changeTitle on headerService with the correct title', () => {
        spyOn(headerService, 'changeTitle');
        const testComponent = new ImprintComponent(headerService);
        expect(headerService.changeTitle).toHaveBeenCalledWith(Sides.imprint.translationKey);
    });
});

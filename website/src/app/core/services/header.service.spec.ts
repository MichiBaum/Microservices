import {TestBed} from '@angular/core/testing';
import {HeaderService} from './header.service';

describe('HeaderService', () => {
    let service: HeaderService;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(HeaderService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should emit title change', (done) => {
        const testTitle = 'New Title';
        service.titleChangeEmitter.subscribe((title) => {
            expect(title).toBe(testTitle);
            done();
        });
        service.changeTitle(testTitle);
    });
});

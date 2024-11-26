import {SecondsPipe} from './seconds.pipe';

describe('SecondsPipe', () => {
    let pipe: SecondsPipe;

    beforeEach(() => {
        pipe = new SecondsPipe();
    });

    it('should transform 3661 seconds to "1h 1min 1s"', () => {
        expect(pipe.transform(3661)).toBe('1h 1min 1s');
    });

    it('should transform 0 seconds to "0h 0min 0s"', () => {
        expect(pipe.transform(0)).toBe('0h 0min 0s');
    });

    it('should transform 59 seconds to "0h 0min 59s"', () => {
        expect(pipe.transform(59)).toBe('0h 0min 59s');
    });

    it('should transform 3600 seconds to "1h 0min 0s"', () => {
        expect(pipe.transform(3600)).toBe('1h 0min 0s');
    });

    it('should transform 123456 seconds to "34h 17min 36s"', () => {
        expect(pipe.transform(123456)).toBe('34h 17min 36s');
    });
});

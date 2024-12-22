import {Pipe, PipeTransform} from "@angular/core";

/**
 * A pipe that transforms a numeric value representing seconds into a human-readable string
 * formatted as "xh ymin zs".
 *
 * This pipe can be used to convert a duration given in seconds into a string that expresses
 * the equivalent time in hours, minutes, and seconds.
 *
 * Example:
 *   If the input value is 3661, the output will be "1h 1min 1s".
 *
 * @Pipe
 */
@Pipe({
  name: 'seconds',
})
export class SecondsPipe implements PipeTransform {

  transform(value: number): string {
    const h = Math.floor(value / 3600);
    const m = Math.floor(value % 3600 / 60);
    const s = Math.floor(value % 3600 % 60);

    return `${h}h ${m}min ${s}s`;
  }
}

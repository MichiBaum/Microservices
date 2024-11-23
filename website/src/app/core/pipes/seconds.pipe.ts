import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  standalone: true,
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

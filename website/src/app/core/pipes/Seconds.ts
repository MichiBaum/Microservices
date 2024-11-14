import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  standalone: true,
  name: 'seconds',
})
export class SecondsPipe implements PipeTransform {
  transform(value: number): string {
    var h = Math.floor(value / 3600);
    var m = Math.floor(value % 3600 / 60);
    var s = Math.floor(value % 3600 % 60);

    return "" + h + "h " + m + "min " + s + "s";
  }
}

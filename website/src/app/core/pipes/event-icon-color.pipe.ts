import {Pipe, PipeTransform} from "@angular/core";
import {ChessEvent} from "../models/chess/chess.models";

@Pipe({
  standalone: true,
  name: 'eventIconColor',
})
export class EventIconColorPipe implements PipeTransform {

  transform(event: ChessEvent): string {
    if(event.dateFrom && event.dateTo){
      const dateFrom = new Date(event.dateFrom)
      const dateTo = new Date(event.dateTo)
      const current = new Date()
      if(dateTo > current && dateFrom < current){ return "color: green"}
      if(dateTo < current ){ return "color: red"}
      if(dateFrom > current){ return "color: #0688fb"}
    }
    return ""
  }
}

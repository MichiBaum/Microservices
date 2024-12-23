import {Pipe, PipeTransform} from "@angular/core";
import {IconDefinition} from "@fortawesome/angular-fontawesome";
import {faCalendarDay, faCalendarPlus, faCalendarXmark} from "@fortawesome/free-solid-svg-icons";
import {faClock} from "@fortawesome/free-regular-svg-icons";
import {ChessEvent} from "../models/chess/chess.models";

@Pipe({
  name: 'eventIcon',
})
export class EventIconPipe implements PipeTransform {

  transform(event: ChessEvent): IconDefinition {
    if(event.dateFrom && event.dateTo){
      const dateFrom = new Date(event.dateFrom).setHours(0,0,0,0)
      const dateTo = new Date(event.dateTo).setHours(0,0,0,0)
      const current = new Date().setHours(0,0,0,0)
      if(dateTo >= current && dateFrom <= current){ return faCalendarDay}
      if(dateTo < current ){ return faCalendarXmark}
      if(dateFrom > current){ return faCalendarPlus}
    }
    return faClock;
  }
}

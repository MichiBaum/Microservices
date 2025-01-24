import {Pipe, PipeTransform} from "@angular/core";
import {Gender} from "../models/chess/chess.models";
import {IconDefinition} from "@fortawesome/angular-fontawesome";
import {faMars, faVenus, faVenusMars} from "@fortawesome/free-solid-svg-icons";

@Pipe({
  name: 'genderIcon',
})
export class EventIconPipe implements PipeTransform {

  transform(gender: Gender): IconDefinition {
    if(gender == Gender.MALE)
      return faMars
    if (gender == Gender.FEMALE)
      return faVenus
    return faVenusMars;
  }
}

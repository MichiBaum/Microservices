import {Pipe, PipeTransform} from "@angular/core";
import {Gender, Person} from "../models/chess/chess.models";
import {IconDefinition} from "@fortawesome/angular-fontawesome";
import {faMars, faVenus, faVenusMars} from "@fortawesome/free-solid-svg-icons";

@Pipe({
  name: 'genderIcon',
})
export class EventIconPipe implements PipeTransform {

  transform(person: Person): IconDefinition {
    if(person.gender == Gender.MALE)
      return faMars
    if (person.gender == Gender.FEMALE)
      return faVenus
    return faVenusMars;
  }
}

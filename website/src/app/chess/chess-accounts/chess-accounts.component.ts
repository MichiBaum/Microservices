import {Component, Input} from '@angular/core';
import {TableModule} from "primeng/table";
import {Account, Person} from "../../core/models/chess.models";
import {TreeTableModule} from "primeng/treetable";
import {TreeNode} from "primeng/api";

@Component({
  selector: 'app-chess-accounts',
  standalone: true,
  imports: [
    TableModule,
    TreeTableModule
  ],
  templateUrl: './chess-accounts.component.html',
  styleUrl: './chess-accounts.component.scss'
})
export class ChessAccountsComponent {

  protected tree: TreeNode[] = [];
  selectedNodes: TreeNode[] = [];

  @Input()
  set persons(value: Person[]){
    this.tree = [...this.mapPersonsToTreeNodes(value)]
  }

   mapPersonsToTreeNodes(persons: Person[]): TreeNode[] {
    return persons.map((person, personIndex) => ({
      key: `${personIndex}`,
      expanded: true,
      data: {
        name: person.firstname + " " + person.lastname,
        size: `${person.accounts.length} accounts`,
        platform: '',
        person: person
      },
      children: person.accounts.map((account, accountIndex) => ({
        key: `${personIndex}-${accountIndex}`,
        data: {
          name: account.username,
          size: `0 games`,
          platform: account.platform,
          account: account
        }
      }))
    }));
  }

}
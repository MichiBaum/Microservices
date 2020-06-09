import {Component, OnInit} from '@angular/core';
import {MenuItem} from 'primeng';
import {CheckListItem} from '../core/models/check-list-item.model';
import {CheckListService} from './check-list.service';

@Component({
  selector: 'app-check-list',
  templateUrl: './check-list.component.html',
  styleUrls: ['./check-list.component.scss'],
})
export class CheckListComponent implements OnInit {

  slideMenuViewportHeight: number;
  checkListItems: MenuItem[];
  selectedCheckListItem: CheckListItem;

  constructor(private checkListService: CheckListService) { }

  ngOnInit(): void {
    this.slideMenuViewportHeight = (window.innerHeight / 100 * 85);
    this.checkListService.getCheckListItems().subscribe(value => {
      this.checkListItems = this.convertToTreeNodes(value);
    });
  }

  convertToTreeNodes(checkListItems: CheckListItem[]): MenuItem[] {
    return checkListItems.map((value) => this.convertToTreeNode(value)) as MenuItem[];
  }

  private convertToTreeNode(checkListItem: CheckListItem): MenuItem {
    const items: MenuItem[] = checkListItem.checkListItems.map(value => this.convertToTreeNode(value));
    if (items === undefined || items === null || items.length < 1) {
      return {
        label: checkListItem.name,
        data: checkListItem,
        command: event => this.selectedCheckListItem = event.item.data // TODO not shure .value
      } as MenuItem;
    }
    return {
      label: checkListItem.name,
      data: checkListItem.id,
      command: event => this.selectedCheckListItem = event.item.data, // TODO not shure .value
      items
    } as MenuItem;
  }
}

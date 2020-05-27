import { Component, OnInit } from '@angular/core';
import {TreeNode} from 'primeng';
import {CheckListItem} from '../core/models/check-list-item.model';
import {CheckListService} from './check-list.service';

@Component({
  selector: 'app-check-list',
  templateUrl: './check-list.component.html',
  styleUrls: ['./check-list.component.scss']
})
export class CheckListComponent implements OnInit {

  checkListItems: TreeNode[];

  constructor(private checkListService: CheckListService) { }

  ngOnInit(): void {
    this.checkListService.getCheckListItems().subscribe((value) => {
      this.checkListItems = this.convertToTreeNodes(value);
    });
  }

  convertToTreeNodes(checkListItems: CheckListItem[]): TreeNode[] {
    return checkListItems.map((value) => this.convertToTreeNode(value)) as TreeNode[];
  }

  private convertToTreeNode(checkListItem: CheckListItem): TreeNode {
    return {
      label: checkListItem.name,
      data: checkListItem.id,
      children: checkListItem.checkListItems.map((value) => this.convertToTreeNode(value))
    } as TreeNode;
  }
}

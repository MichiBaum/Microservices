import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {CheckListItem} from '../core/models/check-list-item.model';

@Component({
  selector: 'app-check-list-input',
  templateUrl: './check-list-input.component.html',
  styleUrls: ['./check-list-input.component.scss']
})
export class CheckListInputComponent implements OnInit, OnChanges {

  @Input() checkListItem: CheckListItem;
  changeableCheckListItem: CheckListItem;

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.changeableCheckListItem = changes.checkListItem.currentValue;

  }

}

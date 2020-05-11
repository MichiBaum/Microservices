import { Component, OnInit } from '@angular/core';
import {TabPanel} from 'primeng';

@Component({
  selector: 'app-help-dialog',
  templateUrl: './help-dialog.component.html',
  styleUrls: ['./help-dialog.component.scss']
})
export class HelpDialogComponent implements OnInit {
  dialogVisible: boolean;

  constructor() { }

  ngOnInit(): void {

  }

}

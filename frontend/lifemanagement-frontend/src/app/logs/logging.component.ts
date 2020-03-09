import { Component, OnInit } from '@angular/core';
import {MenuItem} from 'primeng';
import {LogFilter} from '../core/models/log-filter.model';
import {Log} from '../core/models/log.model';
import {PrimeNgBase} from '../core/models/primeng-base.model';
import {LocalStorageService} from '../core/services/local-storage.service';
import {SessionStorageService} from '../core/services/session-storage.service';
import {LoggingService} from './logging.service';

@Component({
  selector: 'app-logging',
  templateUrl: './logging.component.html',
  styleUrls: ['./logging.component.scss'],
})
export class LoggingComponent implements OnInit {

  logs: Log[];

  tableCols: PrimeNgBase[];
  visibleSelectedColumns: PrimeNgBase[];
  selectedLog: Log;

  levels: PrimeNgBase[];
  selectedLevels: PrimeNgBase[] = [];

  localStorageKeyVisibleSelectedColumns = 'visibleSelectedColumnsLogs';
  seenInputSwitch = false;

  logContextMenuItems: MenuItem[];

  filterDialogVisible = false;
  sessionStorageKeyFilterDialogVisible = 'filterDialogVisible';

  filterLevelCollapsed = true;
  sessionStorageKeyFilterLevelCollapsed = 'filterLevelCollapsed';

  filterSeenCollapsed = true;
  sessionStorageKeyFilterSeenCollapsed = 'filterSeenCollapsed';

  constructor(
    private loggingService: LoggingService,
    private localStorageService: LocalStorageService,
    private sessionStorageService: SessionStorageService
  ) {
  }

  ngOnInit(): void {
    this.tableCols = this.initCols();
    this.logContextMenuItems = this.initLogContextMenuItems();
    this.getLevels();
    this.visibleSelectedColumns = this.loadvisibleSelectedColumns();
    this.initFromLocalStorage();
  }

  searchLogs = (): void => {
    const filter: LogFilter = {level: this.selectedLevels.map(value => value.label), seen: this.seenInputSwitch};
    this.loggingService.getLogs(filter).toPromise().then(logs => this.logs = logs);
  }

  private getLevels = (): PrimeNgBase | undefined => {
    this.loggingService.getAllLevels().toPromise().then(levels => {
      const levelArray = levels as string[];
      this.levels = levelArray.map(level => {
        return {label: level, field: level} as PrimeNgBase;
      });
    });
    return undefined;
  }

  initLogContextMenuItems = (): MenuItem[] => {
    return [
      {label: 'log.seen', icon: 'pi pi-search', command: () => this.setSeen(this.selectedLog)} as MenuItem,
    ];
  }

  initCols = (): PrimeNgBase[] => {
    return [
      {field: 'id', label: 'log.id'} as PrimeNgBase,
      {field: 'date', label: 'log.date'} as PrimeNgBase,
      {field: 'formattedMessage', label: 'log.formattedMessage'} as PrimeNgBase,
      {field: 'loggerName', label: 'log.loggerName'} as PrimeNgBase,
      {field: 'level', label: 'log.level'} as PrimeNgBase,
      {field: 'threadName', label: 'log.threadName'} as PrimeNgBase,
      {field: 'arg0', label: 'log.arg0'} as PrimeNgBase,
      {field: 'arg1', label: 'log.arg1'} as PrimeNgBase,
      {field: 'arg2', label: 'log.arg2'} as PrimeNgBase,
      {field: 'arg3', label: 'log.arg3'} as PrimeNgBase,
      {field: 'callerFilename', label: 'log.callerFilename'} as PrimeNgBase,
      {field: 'callerClass', label: 'log.callerClass'} as PrimeNgBase,
      {field: 'callerMethod', label: 'log.callerMethod'} as PrimeNgBase,
      {field: 'callerLine', label: 'log.callerLine'} as PrimeNgBase,
      {field: 'seen', label: 'log.seen'} as PrimeNgBase,
    ];
  }

  setSeen = (log: Log) => {
    this.loggingService.setSeen(log.id, !log.seen).subscribe(
      () => {
        const index = this.logs.findIndex(searchlog => searchlog.id === log.id);
        if (index > -1) {
          this.logs.splice(index, 1);
        }
      }
    );
  }

  visibleColsChanged = () => {
    this.localStorageService.setKeyAndValue(
      this.localStorageKeyVisibleSelectedColumns,
      this.visibleSelectedColumns.map(a => a.label).join());
  }

  private loadvisibleSelectedColumns = (): PrimeNgBase[] => {
    const localStorageString = this.localStorageService.getValue(this.localStorageKeyVisibleSelectedColumns);
    if (localStorageString) {
      const stringarray = localStorageString.substring(0, localStorageString.length).split(',');
      return stringarray.map(value => {
        const tableobject: PrimeNgBase = {field: value.substr(4, value.length - 1), label: value};
        return tableobject;
      });
    }
    return [];
  }

  private initFromLocalStorage = () => {
    this.filterLevelCollapsed = this.sessionStorageService.getValue(this.sessionStorageKeyFilterLevelCollapsed) === 'true';
    this.filterSeenCollapsed = this.sessionStorageService.getValue(this.sessionStorageKeyFilterSeenCollapsed) === 'true';
    this.filterDialogVisible = this.sessionStorageService.getValue(this.sessionStorageKeyFilterDialogVisible) === 'true';
  }

  filterLevelCollapsedEvent = (event: any) => {
    this.sessionStorageService.setKeyAndValue(this.sessionStorageKeyFilterLevelCollapsed, event.collapsed);
  }

  filterSeenCollapsedEvent = (event: any) => {
    this.sessionStorageService.setKeyAndValue(this.sessionStorageKeyFilterSeenCollapsed, event.collapsed);
  }


  logFilterDialogHide = () => {
    this.sessionStorageService.setKeyAndValue(this.sessionStorageKeyFilterDialogVisible, 'false');
  }

  logFilterDialogShow = () => {
    this.sessionStorageService.setKeyAndValue(this.sessionStorageKeyFilterDialogVisible, 'true');
  }
}

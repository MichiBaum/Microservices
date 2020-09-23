import { Component, OnInit } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {MenuItem} from 'primeng';
import {LanguageConfig} from '../core/language.config';
import {ILogFilter} from '../core/models/log-filter.model';
import {ILog} from '../core/models/log.model';
import {IPrimeNgBase} from '../core/models/primeng-base.model';
import {LocalStorageService} from '../core/services/local-storage.service';
import {SessionStorageService} from '../core/services/session-storage.service';
import {LoggingService} from './logging.service';

@Component({
  selector: 'app-logging',
  templateUrl: './logging.component.html',
  styleUrls: ['./logging.component.scss'],
})
export class LoggingComponent implements OnInit {

  logs: ILog[];

  tableCols: IPrimeNgBase[];
  visibleSelectedColumns: IPrimeNgBase[];
  selectedLog: ILog;

  levels: IPrimeNgBase[];
  selectedLevels: IPrimeNgBase[] = [];

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
    private sessionStorageService: SessionStorageService,
    private languageConfig: LanguageConfig,
    private translate: TranslateService
  ) {
    this.languageConfig.languageChanged.subscribe(() => {
      this.logContextMenuItems = this.initLogContextMenuItems();
    });
  }

  ngOnInit(): void {
    this.tableCols = this.initCols();
    this.logContextMenuItems = this.initLogContextMenuItems();
    this.getLevels();
    this.visibleSelectedColumns = this.loadvisibleSelectedColumns();
    this.initFromLocalStorage();
  }

  searchLogs = (): void => {
    const filter: ILogFilter = {level: this.selectedLevels.map((value) => value.label), seen: this.seenInputSwitch};
    this.loggingService.getLogs(filter).toPromise().then((logs) => this.logs = logs);
  }

  private getLevels = (): IPrimeNgBase | undefined => {
    this.loggingService.getAllLevels().toPromise().then((levels) => {
      const levelArray = levels as string[];
      this.levels = levelArray.map((level) => {
        return {label: level, field: level} as IPrimeNgBase;
      });
    });
    return undefined;
  }

  initLogContextMenuItems = (): MenuItem[] => {
    return [
      {label: this.translate.instant('log.seen'), icon: 'pi pi-search', command: () => this.setSeen(this.selectedLog)} as MenuItem,
    ];
  }

  initCols = (): IPrimeNgBase[] => {
    return [
      {field: 'id', label: 'log.id'} as IPrimeNgBase,
      {field: 'date', label: 'log.date'} as IPrimeNgBase,
      {field: 'formattedMessage', label: 'log.formattedMessage'} as IPrimeNgBase,
      {field: 'loggerName', label: 'log.loggerName'} as IPrimeNgBase,
      {field: 'level', label: 'log.level'} as IPrimeNgBase,
      {field: 'threadName', label: 'log.threadName'} as IPrimeNgBase,
      {field: 'arg0', label: 'log.arg0'} as IPrimeNgBase,
      {field: 'arg1', label: 'log.arg1'} as IPrimeNgBase,
      {field: 'arg2', label: 'log.arg2'} as IPrimeNgBase,
      {field: 'arg3', label: 'log.arg3'} as IPrimeNgBase,
      {field: 'callerFilename', label: 'log.callerFilename'} as IPrimeNgBase,
      {field: 'callerClass', label: 'log.callerClass'} as IPrimeNgBase,
      {field: 'callerMethod', label: 'log.callerMethod'} as IPrimeNgBase,
      {field: 'callerLine', label: 'log.callerLine'} as IPrimeNgBase,
      {field: 'seen', label: 'log.seen'} as IPrimeNgBase,
    ];
  }

  setSeen = (log: ILog) => {
    this.loggingService.setSeen(log.id, !log.seen).subscribe(
      () => {
        const index = this.logs.findIndex((searchlog) => searchlog.id === log.id);
        if (index > -1) {
          this.logs.splice(index, 1);
        }
      }
    );
  }

  visibleColsChanged = () => {
    this.localStorageService.setKeyAndValue(
      this.localStorageKeyVisibleSelectedColumns,
      this.visibleSelectedColumns.map((a) => a.label).join());
  }

  private loadvisibleSelectedColumns = (): IPrimeNgBase[] => {
    const localStorageString = this.localStorageService.getValue(this.localStorageKeyVisibleSelectedColumns);
    if (localStorageString) {
      const stringarray = localStorageString.substring(0, localStorageString.length).split(',');
      return stringarray.map((value) => {
        const tableobject: IPrimeNgBase = {field: value.substr(4, value.length - 1), label: value};
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

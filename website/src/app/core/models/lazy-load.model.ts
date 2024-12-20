import {TableLazyLoadEvent} from "primeng/table";

export interface LazyLoad <T> { // TODO maybe not needed? works without event.forceUpdate() but there are bugs in primeng until resolved waiting. https://github.com/primefaces/primeng/issues/17106
  data: T;
  event: TableLazyLoadEvent;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
}

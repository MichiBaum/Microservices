export interface ChessEvent {
  id: string | undefined;
  title: string;
  url: string | undefined;
  embedUrl: string | undefined;
  dateFrom: string | undefined;
  dateTo: string | undefined;
  categories: ChessEventCategory[]
}

export interface ChessEventCategory{
  id: string;
  title: string;
  description: string;
}

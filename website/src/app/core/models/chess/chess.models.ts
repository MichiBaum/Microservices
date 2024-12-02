export interface Person {
  id: string
  firstname:string
  lastname:string
  fideId?:string
  accounts: Account[]
}

export interface SearchPerson {
  firstname:string
  lastname:string
}

export interface Account {
  id: string
  username: string
  platform: ChessPlatform,
  url: string
}

export enum ChessPlatform{
  CHESSCOM="CHESSCOM",
  LICHESS="LICHESS",
  OVER_THE_BOARD="OVER_THE_BOARD"
}

export enum ChessGameType{
  BULLET="BULLET",
  BLITZ="BLITZ",
  RAPID="RAPID",
  CLASSICAL="CLASSICAL",
  UNKNOWN="UNKNOWN"
}

export interface ChessEvent {
  id: string | undefined;
  title: string;
  url: string | undefined;
  embedUrl: string | undefined;
  dateFrom: string | undefined;
  dateTo: string | undefined;
  categories: ChessEventCategory[];
  participants: Person[];
}

export interface ChessEventCategory{
  id: string;
  title: string;
  description: string;
}

export interface ChessGame {
  id: string;
  chessPlatform: ChessPlatform;
  platformId: string;
  pgn: string;
  gameType: ChessGameType;
}

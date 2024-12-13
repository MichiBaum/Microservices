export interface Person {
  id: string
  firstname:string
  lastname:string
  fideId?:string
  federation?:string
  birthday?:string
  gender: Gender
  accounts: Account[]
}

export interface WritePerson {
  firstname:string
  lastname:string
  fideId?:string
  federation?:string
  birthday?:string
  gender: Gender
}

export enum Gender {
  MALE = "MALE",
  FEMALE = "FEMALE",
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
  location: string | undefined;
  url: string | undefined;
  embedUrl: string | undefined;
  dateFrom: string | undefined;
  dateTo: string | undefined;
  categories: ChessEventCategory[];
  participants: Person[];
}

export interface WriteChessEvent {
  title: string;
  location: string | undefined;
  dateFrom: string | undefined;
  dateTo: string | undefined;
  url: string | undefined;
  embedUrl: string | undefined;
  categoryIds: string[];
  participantsIds: string[];
}

export interface ChessEventCategory{
  id: string;
  title: string;
  description: string;
}

export interface ChessEventCategoryWithEvents{
  id: string;
  title: string;
  description: string;
  events: ChessEvent[];
}

export interface ChessGame {
  id: string;
  chessPlatform: ChessPlatform;
  platformId: string;
  pgn: string;
  gameType: ChessGameType;
}

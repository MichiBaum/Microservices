import {Pageable} from "../lazy-load.model";

export interface Person {
  id: string
  firstname:string
  lastname:string
  federation?:string
  birthday?:string
  gender: Gender
  accounts: Account[]
}

export interface WritePerson {
  firstname:string
  lastname:string
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
  FIDE="FIDE",
  FREESTYLE="FREESTYLE"
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
  internalComment: string;
  platform: ChessPlatform;
  categories: ChessEventCategory[];
}

export interface WriteChessEvent {
  title: string;
  location: string | undefined;
  dateFrom: string | undefined;
  dateTo: string | undefined;
  url: string | undefined;
  embedUrl: string | undefined;
  internalComment: string;
  platform: ChessPlatform;
  categoryIds: string[];
  participantsIds: string[];
}

export interface SearchChessEvent extends Pageable {
  title: string;
  category: string;
  location: string;
  url: boolean;
  embedUrl: boolean;
}

export interface ChessEventCategory{
  id: string;
  title: string;
  description: string;
}

export interface WriteChessEventCategory{
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

export interface ChessEngine {
  id: string;
  name: string;
  version: string;
}

export interface WriteChessEngine {
  name: string;
  version: string;
}

export interface ChessOpening{
  id: string;
  name: string;
}

export interface ChessEvaluation{
    engineId: string;
    depth: number;
    evaluation: string;
}

export interface ChessOpeningMove{
    id: string;
    move: string;
    nextMoves: ChessOpeningMove[];
    evaluations: ChessEvaluation[]
}

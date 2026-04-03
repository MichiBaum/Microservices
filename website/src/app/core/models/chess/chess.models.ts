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
  firstname: string
  lastname: string
}

export interface SearchAccount {
  accountName: string;
  searchLocation?: SearchLocation;
}

export interface AccountPerson {
  id: string
  name: string
}

export interface Account {
  id?: string
  platformId: string
  name: string
  username: string
  platform: ChessPlatform
  url: string
  person?: AccountPerson
  createdAt?: string
}

export interface WriteAccount {
  platformId: string;
  name: string;
  username: string;
  platform: ChessPlatform;
  createdAt?: string;
  personId?: string;
}

export enum SearchLocation {
  LOCAL = "LOCAL",
  ONLINE = "ONLINE",
}

export enum ChessPlatform{
  CHESSCOM="CHESSCOM",
  LICHESS="LICHESS",
  FIDE="FIDE",
  FREESTYLE="FREESTYLE"
}

export enum GameResult {
  WHITE_WIN = "WHITE_WIN",
  BLACK_WIN = "BLACK_WIN",
  DRAW = "DRAW",
  ONGOING = "ONGOING",
  NOT_STARTED = "NOT_STARTED"
}

export enum TerminationType {
  CHECKMATE = "CHECKMATE",
  RESIGNATION = "RESIGNATION",
  TIMEOUT = "TIMEOUT",
  STALEMATE = "STALEMATE",
  DRAW_AGREEMENT = "DRAW_AGREEMENT",
  REPETITION = "REPETITION",
  FIFTY_MOVE_RULE = "FIFTY_MOVE_RULE",
  INSUFFICIENT_MATERIAL = "INSUFFICIENT_MATERIAL",
  ABANDONMENT = "ABANDONMENT",
  TIME_EXPIRED_VS_INSUFFICIENT_MATERIAL = "TIME_EXPIRED_VS_INSUFFICIENT_MATERIAL"
}

export enum TimeControlCategory {
  BULLET = "BULLET",
  BLITZ = "BLITZ",
  RAPID = "RAPID",
  CLASSICAL = "CLASSICAL",
  CORRESPONDENCE = "CORRESPONDENCE"
}

export enum SourceType {
  ONLINE_PLATFORM = "ONLINE_PLATFORM",
  OTB = "OTB",
  IMPORTED = "IMPORTED"
}

export enum GameVariant {
  STANDARD = "STANDARD",
  CHESS960 = "CHESS960",
  KING_OF_THE_HILL = "KING_OF_THE_HILL",
  THREE_CHECK = "THREE_CHECK",
  CRAZYHOUSE = "CRAZYHOUSE",
  ATOMIC = "ATOMIC",
  HORDE = "HORDE",
  RACING_KINGS = "RACING_KINGS"
}

export enum PieceColor {
  WHITE = "WHITE",
  BLACK = "BLACK"
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

export interface ChessPlayer {
  id: string;
  username: string | null;
  rating: number | null;
  pieceColor: PieceColor;
  accountId: string | null;
}

export interface WriteChessPlayer {
  accountId: string | null;
  username: string | null;
  rating: number | null;
}

export interface ChessGame {
  id: string;
  sourceType: SourceType;
  platform: ChessPlatform | null;
  externalGameId: string | null;
  pgn: string;
  openingName: string | null;
  gameResult: GameResult;
  termination: TerminationType | null;
  playedAt: string;
  timeControl: string | null;
  timeControlCategory: TimeControlCategory | null;
  variant: GameVariant;
  whitePlayer: ChessPlayer;
  blackPlayer: ChessPlayer;
}

export interface WriteChessGame {
  sourceType: SourceType;
  platform: ChessPlatform | null;
  externalGameId: string | null;
  pgn: string;
  openingName: string | null;
  gameResult: GameResult;
  termination: TerminationType | null;
  playedAt: string;
  timeControl: string | null;
  timeControlCategory: TimeControlCategory | null;
  variant: GameVariant;
  whitePlayer: WriteChessPlayer;
  blackPlayer: WriteChessPlayer;
  eventId: string | null;
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
    moveId: string;
}

export interface PopularChessOpening{
    id: string;
    name: string;
    moveId: string;
    ranking: number;
}

export interface ChessEvaluation{
    id: string;
    engineId: string;
    engineName: string;
    engineVersion: string;
    depth: number;
    evaluation: string;
}

export interface ChessOpeningMove{
    id: string;
    move: string;
    fen: string;
    openingName: string;
    openingId: string;
    nextMoves: ChessOpeningMove[];
    evaluations: ChessEvaluation[]
}

export interface WriteOpeningMove{
    id: string;
    move: string;
    parentMoveId: string;
}

export interface OpeningEvaluation{
    id: string;
    engineId: string;
    depth: number;
    evaluation: string;
}

export interface WriteOpeningEvaluation{
    engineId: string;
    depth: number;
    evaluation: string;
}

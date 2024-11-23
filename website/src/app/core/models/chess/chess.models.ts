export interface Person {
  id: string
  firstname:string
  lastname:string
  accounts: Account[]
}

export interface SearchPerson {
  firstname:string
  lastname:string
}

export interface Account {
  id: string
  username: string
  platform: Platform
}

export enum Platform{
  LICHESS,
  CHESSCOM
}

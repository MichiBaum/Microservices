import {Artist} from "./artist.model";

export interface Playlist {
  id: string;
  name: string;
  description: string | null;
  coverImageUrl: string | null;
  isSpotify: boolean;
  spotifyId: string | null;
  trackCount: number;
  created: string | null;
  updated: string | null;
}

export interface PlaylistTrack {
  trackId: string;
  name: string;
  artists: Artist[];
  position: number;
  releaseDate: string | null;
}

export interface PlaylistDetails {
  id: string;
  name: string;
  description: string | null;
  coverImageUrl: string | null;
  isSpotify: boolean;
  spotifyId: string | null;
  trackCount: number;
  created: string | null;
  updated: string | null;
  tracks: PlaylistTrack[];
}

export interface CreatePlaylistRequest {
  name: string;
  description?: string | null;
  coverImageUrl?: string | null;
}

export interface UpdatePlaylistRequest {
  name?: string | null;
  description?: string | null;
  coverImageUrl?: string | null;
}

export interface SyncPlaylistsRequest {
  spotifyPlaylistIds?: string[] | null;
}

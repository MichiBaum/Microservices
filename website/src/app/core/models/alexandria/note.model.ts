export interface NoteResponse {
    id: string;
    title: string;
    text: string;
    encrypted: boolean;
}

export interface NoteRequest {
    title: string;
    text: string;
    encrypted: boolean;
}

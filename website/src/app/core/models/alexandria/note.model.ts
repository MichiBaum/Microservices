export interface NoteResponse {
    id: string;
    title: string;
    content: string;
    encrypted: boolean;
}

export interface NoteRequest {
    title: string;
    content: string;
    encrypted: boolean;
}

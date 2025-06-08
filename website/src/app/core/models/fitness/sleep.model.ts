export interface Sleep {
    id: string;
    startTime: Date;
    endTime: Date;
    duration: number;
}

export interface SleepStage{
    start: Date;
    end: Date;
    stage: string;
    duration: number;
}

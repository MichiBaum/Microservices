export interface Sleep {
  startTime: Date;
  endTime: Date;
  duration: number;
  stages: SleepStage[];
}

export interface SleepStage{
  start: Date;
  end: Date;
  stage: string;
  duration: number;
}

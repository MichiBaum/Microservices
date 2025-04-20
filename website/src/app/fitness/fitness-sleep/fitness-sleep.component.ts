import {Component, inject, OnInit} from '@angular/core';
import {FitnessService} from "../../core/api-services/fitness.service";
import {Sleep} from "../../core/models/fitness/sleep.model";
import {SleepStagesChartComponent} from "./sleep-stages-chart/sleep-stages-chart.component";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {DatePipe, NgIf} from "@angular/common";
import {SecondsPipe} from "../../core/pipes/seconds.pipe";
import {Select} from "primeng/select";

@Component({
  selector: 'app-fitness-sleep',
  imports: [
    SleepStagesChartComponent,
    DropdownModule,
    FormsModule,
    NgIf,
    DatePipe,
    SecondsPipe,
    Select
  ],
  templateUrl: './fitness-sleep.component.html',
  styleUrl: './fitness-sleep.component.css'
})
export class FitnessSleepComponent implements OnInit {
  private readonly fitnessService = inject(FitnessService);


  protected sleeps: Sleep[] = []
  protected selectedSleep: Sleep | undefined


  ngOnInit(): void {
    this.fitnessService.getSleep().subscribe(sleep => {
      const sorted = this.fDates(sleep);
      this.sleeps = [...sorted]
    })
  }

  fDates(sleep: Sleep[]): Sleep[]{
    sleep.forEach(s => {
      s.startTime = new Date(s.startTime)
      s.endTime = new Date(s.endTime)
      s.stages.forEach(s => {
        s.start = new Date(s.start)
        s.end = new Date(s.end)
      })
    })
    return sleep.sort((a, b) => a.startTime.getTime() - b.startTime.getTime())
  }

  sumStage(sleep: Sleep, stage: string){
    return sleep.stages.filter(v => v.stage === stage).reduce((sum, current) => sum + current.duration, 0)
  }

}



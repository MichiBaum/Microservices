import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FitnessService} from "../../core/services/fitness.service";
import * as d3 from 'd3';
import {Sleep, SleepStage} from "../../core/models/fitness/sleep.model";
import {SleepStagesChartComponent} from "./sleep-stages-chart/sleep-stages-chart.component";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {DatePipe, NgIf} from "@angular/common";
import {SecondsPipe} from "../../core/pipes/Seconds";

@Component({
  selector: 'app-fitness-sleep',
  standalone: true,
  imports: [
    SleepStagesChartComponent,
    DropdownModule,
    FormsModule,
    NgIf,
    DatePipe,
    SecondsPipe
  ],
  templateUrl: './fitness-sleep.component.html',
  styleUrl: './fitness-sleep.component.scss'
})
export class FitnessSleepComponent implements OnInit {

  protected sleeps: Sleep[] = []
  protected selectedSleep: Sleep | undefined

  constructor(private readonly fitnessService: FitnessService) {

  }

  ngOnInit(): void {
    this.fitnessService.getSleep().subscribe(sleep => {
      var sorted = this.fDates(sleep)
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

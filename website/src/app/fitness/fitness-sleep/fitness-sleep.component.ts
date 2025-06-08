import {Component, inject, OnInit, signal} from '@angular/core';
import {FitnessService} from "../../core/api-services/fitness.service";
import {Sleep} from "../../core/models/fitness/sleep.model";
import {SleepStagesChartComponent} from "./sleep-stages-chart/sleep-stages-chart.component";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {DatePipe, NgIf} from "@angular/common";
import {SecondsPipe} from "../../core/pipes/seconds.pipe";
import {Select} from "primeng/select";
import {rxResource} from "@angular/core/rxjs-interop";
import {Observable, of} from "rxjs";

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

  sleeps = rxResource({
      loader: () => this.fitnessService.getSleep()
  })
  selectedSleep = signal<Sleep | undefined>(undefined)
    stages = rxResource({
        request: () => ({sleepId: this.selectedSleep()?.id}),
        loader: (params) => {
            const sleepId = params.request.sleepId
            if (sleepId == undefined)
                return of([])
            return this.fitnessService.getSleepStages(sleepId)
        }
    })


  ngOnInit(): void {
  }

  fDates(sleep: Sleep[]): Sleep[]{
    sleep.forEach(s => {
      s.startTime = new Date(s.startTime)
      s.endTime = new Date(s.endTime)
    })
    return sleep.sort((a, b) => a.startTime.getTime() - b.startTime.getTime())
  }

}



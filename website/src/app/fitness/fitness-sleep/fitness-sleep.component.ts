import {Component, inject, OnInit, signal} from '@angular/core';
import {FitnessService} from "../../core/api-services/fitness.service";
import {Sleep} from "../../core/models/fitness/sleep.model";
import {SleepStagesChartComponent} from "./sleep-stages-chart/sleep-stages-chart.component";
import {FormsModule} from "@angular/forms";
import { DatePipe } from "@angular/common";
import {SecondsPipe} from "../../core/pipes/seconds.pipe";
import {Select} from "primeng/select";
import {rxResource} from "@angular/core/rxjs-interop";
import {Observable, of} from "rxjs";

@Component({
  selector: 'app-fitness-sleep',
  imports: [
    SleepStagesChartComponent,
    FormsModule,
    DatePipe,
    SecondsPipe,
    Select
],
  templateUrl: './fitness-sleep.component.html',
  styleUrl: './fitness-sleep.component.css'
})
export class FitnessSleepComponent {
  private readonly fitnessService = inject(FitnessService);

    sleeps = rxResource({
      stream: () => this.fitnessService.getSleep()
    })
    selectedSleep = signal<Sleep | undefined>(undefined)
    stages = rxResource({
        params: () => ({sleepId: this.selectedSleep()?.id}),
        stream: (params) => {
            const sleepId = params.params.sleepId
            if (sleepId == undefined)
                return of([])
            return this.fitnessService.getSleepStages(sleepId)
        }
    })


}



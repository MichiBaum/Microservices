import {Component, OnInit} from '@angular/core';
import {Weight} from "../../core/models/fitness/weight.model";
import {FitnessService} from "../../core/services/fitness.service";
import {ChartModule} from "primeng/chart";

@Component({
  selector: 'app-fitness-weight',
  standalone: true,
  imports: [
    ChartModule
  ],
  templateUrl: './fitness-weight.component.html',
  styleUrl: './fitness-weight.component.scss'
})
export class FitnessWeightComponent implements OnInit {
  weightData: Weight[] = [];

  weightChartData: any;
  weightChartOptions: any;

  bmiChartData: any;
  bmiChartOptions: any;

  constructor(
    private readonly fitnessService: FitnessService
  ) {}

  ngOnInit(): void {
    this.getWeightData()
  }

  getWeightData(){
    this.fitnessService.getWeight().subscribe(weight => {
      this.weightData = [...weight]
      this.updateWeightChart()
      this.updateBmiChart()
    });
  }

  private updateWeightChart() {
    this.weightChartOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
    };

    let weights = this.weightData.slice().sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return dateA - dateB;
    });
    let data = {
      labels: weights.map(value => value.date),
      datasets: [
        {
          label: 'Weight',
          data: weights.map(value => value.weight),
          fill: false,
          tension: 0.4
        }
      ]
    };
    this.weightChartData = data
  }

  private updateBmiChart() {
    this.bmiChartOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
    };

    let weights = this.weightData.slice().sort((a, b) => {
      const dateA = new Date(a.date).getTime();
      const dateB = new Date(b.date).getTime();
      return dateA - dateB;
    });
    let data = {
      labels: weights.map(value => value.date),
      datasets: [
        {
          label: 'BMI',
          data: weights.map(value => value.bmi),
          fill: false,
          tension: 0.4
        }
      ]
    };
    this.bmiChartData = data
  }

}

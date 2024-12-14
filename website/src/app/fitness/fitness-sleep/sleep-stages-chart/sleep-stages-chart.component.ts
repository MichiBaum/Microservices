import {Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {SleepStage} from "../../../core/models/fitness/sleep.model";
import * as d3 from "d3";

@Component({
  selector: 'app-sleep-stages-chart',
  standalone: true,
  imports: [],
  templateUrl: './sleep-stages-chart.component.html',
  styleUrl: './sleep-stages-chart.component.scss'
})
export class SleepStagesChartComponent implements OnChanges{

  @Input() sleepStages: SleepStage[] | undefined;
  @ViewChild('container') container : ElementRef | undefined;

  private svg: any;
  private margin = 50;
  private width = 0;
  private height = 0;

  constructor(private elementRef: ElementRef) { }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes["sleepStages"].currentValue) {
      this.createSvg()
      this.drawChart(changes["sleepStages"].currentValue);
    }
  }

  private createSvg(): void {
    // @ts-ignore
    this.width = this.container.nativeElement.offsetWidth - (this.margin * 2);
    this.height = this.width * 0.2
    d3.select(this.elementRef.nativeElement).select('.chart').selectAll('*').remove();
    this.svg = d3.select(this.elementRef.nativeElement).select('.chart')
      .append("svg")
      .attr("width", this.width + (this.margin * 2))
      .attr("height", this.height + (this.margin * 2))
      .append("g")
      .attr("transform", "translate(50,0)");
  }

  private drawChart(sleepStages: SleepStage[]): void {
    let min: Date = d3.min(sleepStages, d => d.start) as Date;
    let max: Date = d3.max(sleepStages, d => d.end) as Date;

    const timeScale = d3.scaleTime()
      .domain([min, max])
      .nice()
      .range([0, this.width])

    let stageDomain = ["DEEP", "LIGHT", "REM", "WAKE"];
    let stageColor = ["navy", "skyblue", "purple", "orange"];
    const stageScale = d3.scaleBand()
      .domain(stageDomain)
      .range([this.height, 0])
      .padding(0.1);

    const colorScale = d3.scaleOrdinal()
      .domain(stageDomain)
      .range(stageColor);

    // Chart group
    const g = this.svg.append("g")
      .attr("transform", "translate("+this.margin+","+this.margin+")");

    // Add grid lines
    g.append("g")
      .attr("class", "grid grid-cols-12 gap-4 grid-cols-12 gap-6")
      .call(d3.axisLeft(stageScale).tickSize(-this.width).tickFormat((v, i) => ""))
      .selectAll("line")
      .attr("stroke", "#ddd");

    // Draw bars
    g.selectAll("rect")
      .data(sleepStages)
      .enter().append("rect")
      .attr("x", (d: { start: Date }) => timeScale(d.start))
      .attr("y", (d: { stage: string; }) => stageScale(d.stage))
      .attr("width", (d: { end: Date; start: Date; }) => timeScale(d.end) - timeScale(d.start))
      .attr("height", stageScale.bandwidth())
      .attr("class", (d: { stage: string; }) => d.stage)
      .attr("fill", (d: { stage: string; }) => colorScale(d.stage))
      .attr("rx", 10)

    // Add x-axis
    g.append("g")
      .attr("class", "x axis")
      .attr("transform", `translate(0,${this.height})`)
      .call(d3.axisBottom(timeScale).ticks(d3.timeHour.every(0.5)).tickFormat((domainValue, index) => d3.timeFormat("%H:%M")(domainValue as Date)))
      .append("text")
      .attr("fill", "#555")
      .attr("x", this.width / 2)
      .attr("y", 30)
      .attr("dy", "0.71em")
      .attr("text-anchor", "middle")
      .text("Time");

    // Add y-axis
    g.append("g")
      .attr("class", "y axis")
      .call(d3.axisLeft(stageScale))
      .append("text")
      .attr("fill", "#555")
      .attr("transform", "rotate(-90)")
      .attr("y", -50)
      .attr("x", -this.height / 2)
      .attr("dy", "0.71em")
      .attr("text-anchor", "middle")
      .text("Sleep Stage");
  }

}

import {Component, ElementRef, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {IOHLC} from "../../domain/dto-types";
import {PlotlyComponent} from "angular-plotly.js";
import {OhlcAnchorEnum} from "../../domain/domain-types";

@Component({
  selector: 'plotly',
  templateUrl: './plotly-ohlc.component.html',
  styleUrls: ['./plotly-ohlc.component.css']
})
export class PlotlyOhlcComponent implements OnChanges {
  @Input() title: String = 'A Fancy Plot';
  @Input() data: IOHLC[] = [];
  @ViewChild(PlotlyComponent) plotly: PlotlyComponent;
  @Input() chartWidth: Number = undefined;
  @Input() chartHeight: Number = undefined;

  _layout: any = {
    width: this.chartWidth,
    height: this.chartHeight,
    dragmode: 'zoom',
    margin: {
      r: 10,
      t: 25,
      b: 40,
      l: 60
    },
    showlegend: false,
    xaxis: {
      autorange: true,
      fixedrange: false,
      domain: [0, 1],
      /*range: ['2017-01-03 12:00', '2017-02-15 12:00'],
      rangeslider: {range: ['2017-01-03 12:00', '2017-02-15 12:00']},*/
      title: 'Date',
    },
    yaxis: {
      autorange: true,
      fixedrange: false,
      domain: [0, 1],
      /*range: [114.609999778, 137.410004222],*/
      type: 'linear'
    }
  };

  _config: any = {
    responsive: true
  };

  _data: any[] = [];

  constructor() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data']) {
      let rawData: IOHLC[] = changes['data'].currentValue;
      if (rawData) {
        let x = [];
        let open: Number[] = [];
        let high: Number[] = [];
        let low: Number[] = [];
        let close: Number[] = [];

        rawData.forEach(v => {
          x.push(v.date);
          open.push(v.open);
          high.push(v.high);
          low.push(v.low);
          close.push(v.close);
        });

        let trace: any = {
          type: 'candlestick',
          x,
          open,
          high,
          low,
          close,
          xaxis: 'x',
          yaxis: 'y',
          increasing: {line: {'color': 'green'}},
          decreasing: {line: {'color': 'red'}}
        }

        this._data = [trace];
      }

    }

    if (changes['chartWidth'] || changes['chartHeight']){
      let newWidth = changes['chartWidth'] ? changes['chartWidth'].currentValue : this.chartWidth;
      let newHeight = changes['chartHeight'] ? changes['chartHeight'].currentValue : this.chartHeight;
      let newLayout = {
        ...this._layout,
        height: newHeight,
        width: newWidth
      };
      console.log(`New layout: ${JSON.stringify(newLayout)}`);
      this._layout = {
        ...this._layout,
        ...{height: newHeight, width: newWidth}
      }
    }
  }

  drawLinePoints(
    x0: string | String,
    y0: number | Number,
    x1: string | String,
    y1: number | Number,
    line?: {
      color: string,
      width: number,
      dash: string
    }
  ) {
    if (!this._layout.shapes) {
      this._layout.shapes = [];
    }

    this._layout.shapes.push({
      type: 'line',
      x0, x1, y0, y1,
      line: line
    })
  }

  private obtainOhlc(ohlc: IOHLC, anchor: OhlcAnchorEnum): Number {
    switch (anchor) {
      case OhlcAnchorEnum.OPEN:
        return ohlc.open;
      case OhlcAnchorEnum.HIGH:
        return ohlc.high;
      case OhlcAnchorEnum.LOW:
        return ohlc.low;
      case OhlcAnchorEnum.CLOSE:
        return ohlc.close;
    }
  }

  removeAllShapes(){
    this._layout.shapes = [];
  }
}

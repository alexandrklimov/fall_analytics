import {Component, ElementRef, ViewChild, OnInit, AfterViewInit, AfterContentChecked, OnDestroy} from '@angular/core';
import {OhlcDataProviderService} from "./services/ohlc-data-provider.service";
import {MatSelectChange} from "@angular/material/select";
import {IOHLC} from "./domain/dto-types";
import {PlotlyOhlcComponent} from "./components/plotly/plotly-ohlc.component";
import {fromEvent, Subscription} from "rxjs";
import {debounceTime} from "rxjs/operators";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, AfterContentChecked, AfterViewInit, OnDestroy {
  title = 'client';
  tickets: String[] = [];
  _selectedTicker: String = "";
  @ViewChild('ptcInput')
  ptcInputRef: ElementRef;
  @ViewChild(PlotlyOhlcComponent)
  plotly: PlotlyOhlcComponent;
  @ViewChild('chartContainer', {read: ElementRef})
  chartContainerElemRef: ElementRef;

  private _windowOnResizeSubscription?: Subscription;
  private _chartAreaSizeInitialized: boolean = false;

  chartProps: {
    chartAreaWidth?: Number,
    chartAreaHeight?: Number,
    ohlcData?: Promise<IOHLC[]>
  } = {}


  constructor(private ohlcDataProvider: OhlcDataProviderService) {
  }

  ngOnInit(): void {
    this.ohlcDataProvider.loadTickers()
      .then(res => this.tickets = res)
  }

  ngAfterContentChecked(): void {
    if(this.chartContainerElemRef?.nativeElement && !this._chartAreaSizeInitialized) {
      this.chartProps = this.propsOnResizeUpdate();
      this._chartAreaSizeInitialized = true;
    }
  }

  ngAfterViewInit(): void {
    fromEvent(window, 'resize')
      .pipe(debounceTime(200))
      .subscribe(_ => {
        this.chartProps = {
          ohlcData: this.chartProps.ohlcData,
          ...(this.propsOnResizeUpdate())
        }
      });
  }

  ngOnDestroy() {
    if(this._windowOnResizeSubscription){
      this._windowOnResizeSubscription.unsubscribe();
    }
  }

  onTickerChng(evt: MatSelectChange) {
    this._selectedTicker = evt.value;
    this.chartProps = {
      ohlcData: this.ohlcDataProvider.loadOhlc(evt.value),
      ...(this.propsOnResizeUpdate())
    }
  }


  computeRequiredGainFromEndOfFall() {
    let ptcValue = this.ptcInputRef.nativeElement.value;
    this.ohlcDataProvider.computeRequiredGainFromEndOfFall(this._selectedTicker, ptcValue)
      .then(res => {
        console.log(res)
        this.plotly.removeAllShapes();
        res.success.forEach(it => {
          this.plotly.drawLinePoints(
            it.fdr.base.date,
            it.fdr.base.close,
            it.fdr.end.date,
            it.fdr.end.close
          );
          this.plotly.drawLinePoints(
            it.fdr.end.date,
            it.fdr.end.close,
            it.point.date,
            it.point.close
          );
        })
      });
  }

  onWndResize(event: any) {
    return this.chartProps = {
      ohlcData: this.chartProps.ohlcData,
      ...(this.propsOnResizeUpdate())
    }
  }

  private propsOnResizeUpdate(): { chartAreaHeight: Number, chartAreaWidth: Number } {
    let res = {
      chartAreaHeight: this.chartContainerElemRef.nativeElement.clientHeight,
      chartAreaWidth: this.chartContainerElemRef.nativeElement.clientWidth
    }
    console.log(`chartAreaHeight: ${res.chartAreaHeight} | chartAreaWidth: ${res.chartAreaWidth}`);
    return res;
  }
}

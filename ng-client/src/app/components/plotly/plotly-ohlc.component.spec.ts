import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlotlyOhlcComponent } from './plotly-ohlc.component';

describe('PlotlyComponent', () => {
  let component: PlotlyOhlcComponent;
  let fixture: ComponentFixture<PlotlyOhlcComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlotlyOhlcComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlotlyOhlcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

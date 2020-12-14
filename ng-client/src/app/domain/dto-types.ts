export interface IOHLC {
  date: String;
  open: Number;
  high: Number;
  low: Number;
  close: Number;
}


export interface IGainFromEndOfFallResultDto {
  success: IPossibleProfitPoint[];
  fail: IPossibleProfitPoint[];
}

export interface IPossibleProfitPoint {
  fdr: IFallDetectResult;
  maxChngDuringProfitLvlSearch: Number;
  point?: IPoint;
  lowest?: IPoint;
  maxPossibleProfit?: IPoint;
}

export interface IFallDetectResult{
  base: IPoint,
  end: IPoint
}

export interface IPoint{
  date: String;
  close: Number;
  open: Number;
}

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http'
import {IGainFromEndOfFallResultDto, IOHLC} from "../domain/dto-types";
import {environment} from "../../environments/environment";


@Injectable({
  providedIn: 'root'
})
export class OhlcDataProviderService {
  private readonly _baseUrl;

  constructor(private httpClient: HttpClient) {
    let restServerAddress = environment.restServerAddress ? environment.restServerAddress : '';
    this._baseUrl = restServerAddress + '/rest/v1';
  }

  async loadOhlc(ticker: String): Promise<IOHLC[]> {
    return await this.httpClient.get(this._baseUrl + '/data/' + ticker)
      .toPromise()
      .then(res => res as IOHLC[]);
  }

  async loadTickers(): Promise<String[]> {
    return await this.httpClient.get(this._baseUrl + '/tickers')
      .toPromise()
      .then(res => res as String[])
  }

  async computeRequiredGainFromEndOfFall(ticker: String, fallChngPtc: Number): Promise<IGainFromEndOfFallResultDto> {
    return await this.httpClient.get(this._baseUrl + '/compute/req-gain-from-fall/' + ticker + '/' + fallChngPtc)
      .toPromise()
      .then(res => res as IGainFromEndOfFallResultDto)
  }
}

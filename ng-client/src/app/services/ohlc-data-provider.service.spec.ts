import { TestBed } from '@angular/core/testing';

import { OhlcDataProviderService } from './ohlc-data-provider.service';

describe('OhlcDataProviderService', () => {
  let service: OhlcDataProviderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OhlcDataProviderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

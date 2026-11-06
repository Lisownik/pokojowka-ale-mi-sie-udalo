import { TestBed } from '@angular/core/testing';

import { HeatersRepository } from './heaters-repository';

describe('HeatersRepository', () => {
  let service: HeatersRepository;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HeatersRepository);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

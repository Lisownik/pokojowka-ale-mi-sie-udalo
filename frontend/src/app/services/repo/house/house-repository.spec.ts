import { TestBed } from '@angular/core/testing';

import { HouseRepository } from './house-repository';

describe('HouseRepository', () => {
  let service: HouseRepository;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HouseRepository);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

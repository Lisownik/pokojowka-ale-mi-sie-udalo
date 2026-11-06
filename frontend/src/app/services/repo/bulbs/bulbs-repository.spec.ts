import { TestBed } from '@angular/core/testing';

import { BulbsRepository } from './bulbs-repository';

describe('BulbsRepository', () => {
  let service: BulbsRepository;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BulbsRepository);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

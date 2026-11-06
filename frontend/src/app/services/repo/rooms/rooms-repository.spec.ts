import { TestBed } from '@angular/core/testing';

import { RoomsRepository } from './rooms-repository';

describe('RoomsRepository', () => {
  let service: RoomsRepository;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoomsRepository);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

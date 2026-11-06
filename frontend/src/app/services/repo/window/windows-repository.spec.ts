import { TestBed } from '@angular/core/testing';

import { WindowsRepository } from './windows-repository';

describe('WindowsRepository', () => {
  let service: WindowsRepository;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WindowsRepository);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

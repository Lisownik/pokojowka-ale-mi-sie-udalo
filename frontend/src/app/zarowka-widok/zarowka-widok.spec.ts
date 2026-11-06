import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZarowkaWidok } from './zarowka-widok';

describe('ZarowkaWidok', () => {
  let component: ZarowkaWidok;
  let fixture: ComponentFixture<ZarowkaWidok>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZarowkaWidok]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZarowkaWidok);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

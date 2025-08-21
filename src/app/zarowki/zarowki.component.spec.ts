import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZarowkiComponent } from './zarowki.component';

describe('ZarowkiComponent', () => {
  let component: ZarowkiComponent;
  let fixture: ComponentFixture<ZarowkiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZarowkiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZarowkiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

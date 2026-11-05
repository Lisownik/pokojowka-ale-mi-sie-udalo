import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PotkiComponent } from './potki.component';

describe('PotkiComponent', () => {
  let component: PotkiComponent;
  let fixture: ComponentFixture<PotkiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PotkiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PotkiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

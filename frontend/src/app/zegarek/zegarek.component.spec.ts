import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZegarekComponent } from './zegarek.component';

describe('ZegarekComponent', () => {
  let component: ZegarekComponent;
  let fixture: ComponentFixture<ZegarekComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZegarekComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZegarekComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

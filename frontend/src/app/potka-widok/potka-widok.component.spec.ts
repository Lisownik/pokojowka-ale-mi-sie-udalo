import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PotkaWidokComponent } from './potka-widok.component';

describe('PotkaWidokComponent', () => {
  let component: PotkaWidokComponent;
  let fixture: ComponentFixture<PotkaWidokComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PotkaWidokComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PotkaWidokComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

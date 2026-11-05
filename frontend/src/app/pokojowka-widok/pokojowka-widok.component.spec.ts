import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PokojowkaWidokComponent } from './pokojowka-widok.component';

describe('PokojowkaWidokComponent', () => {
  let component: PokojowkaWidokComponent;
  let fixture: ComponentFixture<PokojowkaWidokComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PokojowkaWidokComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PokojowkaWidokComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

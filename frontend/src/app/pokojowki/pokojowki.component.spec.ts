import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PokojowkiComponent } from './pokojowki.component';

describe('PokojowkiComponent', () => {
  let component: PokojowkiComponent;
  let fixture: ComponentFixture<PokojowkiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PokojowkiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PokojowkiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

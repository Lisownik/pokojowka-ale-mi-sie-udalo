import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageChange } from './language-change';

describe('LanguageChange', () => {
  let component: LanguageChange;
  let fixture: ComponentFixture<LanguageChange>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LanguageChange]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LanguageChange);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

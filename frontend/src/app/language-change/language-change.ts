import { Component } from '@angular/core';
import { TranslocoService } from '@ngneat/transloco';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-language-change',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './language-change.html',
  styleUrls: ['./language-change.css']
})
export class LanguageChangeComponent {
  constructor(private translocoService: TranslocoService) {}

  toggleLanguage(): void {
    const currentLang = this.translocoService.getActiveLang();
    const newLang = currentLang === 'pl' ? 'en' : 'pl';
    this.translocoService.setActiveLang(newLang);
  }

  getCurrentLanguage(): string {
    return this.translocoService.getActiveLang().toUpperCase();
  }
}

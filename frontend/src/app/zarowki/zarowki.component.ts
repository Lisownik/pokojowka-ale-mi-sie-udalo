import { Component, computed } from '@angular/core';
import { Router } from '@angular/router';
import { BackButtonComponent } from '../back-button/back-button.component';
import { CommonModule } from '@angular/common';
import { TranslocoPipe } from '@ngneat/transloco';
import { BulbsRepository } from '../services/repo/bulbs/bulbs-repository';

@Component({
  selector: 'app-zarowki',
  standalone: true,
  imports: [BackButtonComponent, CommonModule, TranslocoPipe],
  templateUrl: './zarowki.component.html',
  styleUrls: ['./zarowki.component.css']
})
export class ZarowkiComponent {
  protected lights = computed(() => this.bulbsRepo.state());

  constructor(private router: Router, public bulbsRepo: BulbsRepository) {}

  toggleLight(id: string, power: boolean) {
    this.bulbsRepo.togglePower(id, power);
  }

  onBrightnessChange(event: Event, id: string) {
    const val = parseInt((event.target as HTMLInputElement).value, 10);
    this.bulbsRepo.setBrightness(id, val);
  }

  goToLight(id: string) {
    this.router.navigate(['/zarowka-widok', id]);
  }
}

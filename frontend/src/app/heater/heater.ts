import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { BackButtonComponent } from '../back-button/back-button.component';
import { HeatersRepository } from '../services/repo/heater/heaters-repository';

@Component({
  selector: 'app-heater',
  standalone: true,
  imports: [CommonModule, BackButtonComponent, TranslocoPipe],
  templateUrl: './heater.html',
  styleUrl: './heater.css'
})
export class Heater {
  protected heaters = computed(() => this.heatersRepo.state());

  constructor(private router: Router, public heatersRepo: HeatersRepository) {}

  toggleHeater(id: string, power: boolean, event: Event): void {
    event.stopPropagation();
    this.heatersRepo.togglePower(id, power);
  }

  goToHeater(id: string): void {
    this.router.navigate(['/heater-view', id]);
  }
}

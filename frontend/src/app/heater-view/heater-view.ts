import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { BackButtonComponent } from '../back-button/back-button.component';
import { HeatersRepository } from '../services/repo/heater/heaters-repository';
import { HeaterData } from '../types/types';

@Component({
  selector: 'app-heater-view',
  standalone: true,
  imports: [CommonModule, BackButtonComponent, TranslocoPipe],
  templateUrl: './heater-view.html',
  styleUrl: './heater-view.css'
})
export class HeaterView implements OnInit {
  readonly minTemp = 16;
  readonly maxTemp = 28;
  readonly step = 0.5;

  private heaterId = signal('');
  protected heater = computed<HeaterData | undefined>(() =>
    this.heatersRepo.state().find(h => h.id === this.heaterId())
  );

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public heatersRepo: HeatersRepository
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) this.heaterId.set(id);
      else this.router.navigate(['/heaters']);
    });
  }

  togglePower(): void {
    const h = this.heater();
    if (h) this.heatersRepo.togglePower(h.id);
  }

  changeTemp(delta: number): void {
    const h = this.heater();
    if (h) this.heatersRepo.setTemperatureGoal(h.id, h.state.temperatureGoal + delta);
  }

  onSliderChange(event: Event): void {
    const h = this.heater();
    if (!h) return;
    this.heatersRepo.setTemperatureGoal(h.id, parseFloat((event.target as HTMLInputElement).value));
  }
}

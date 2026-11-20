import { Component, computed, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BackButtonComponent } from '../back-button/back-button.component';
import { TranslocoPipe } from '@ngneat/transloco';
import { BulbData } from '../types/types';
import { BulbsRepository } from '../services/repo/bulbs/bulbs-repository';

@Component({
  selector: 'app-zarowka-widok',
  standalone: true,
  imports: [BackButtonComponent, CommonModule, TranslocoPipe],
  templateUrl: './zarowka-widok.html',
  styleUrl: './zarowka-widok.css'
})
export class ZarowkaWidok implements OnInit {
  lightId: string = '';
  light = computed(() =>
    this.bulbsRepo.state().find((bulb: BulbData) => bulb.id === this.lightId)
  );

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public bulbsRepo: BulbsRepository
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.lightId = idParam;
      } else {
        this.router.navigate(['/zarowki']);
      }
    });
  }

  toggleLight(power: boolean): void {
    if (this.lightId) {
      this.bulbsRepo.togglePower(this.lightId, power)
    }
  }

  onBrightnessChange(event: Event): void {
    if (!this.lightId) return;
    const val = parseInt((event.target as HTMLInputElement).value, 10);
    this.bulbsRepo.setBrightness(this.lightId, val)
  }

  onTemperatureChange(event: Event): void {
    if (!this.lightId) return;
    const val = parseInt((event.target as HTMLInputElement).value, 10);
    this.bulbsRepo.setTemperature(this.lightId, val);
  }
}

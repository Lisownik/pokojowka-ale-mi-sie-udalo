import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ActionButtonComponent } from '../action-button/action-button.component';
import { BackButtonComponent } from '../back-button/back-button.component';

interface Light {
  id: number;
  name: string;
  isOn: boolean;
  brightness: number;
  temperature?: number;
}

@Component({
  selector: 'app-zarowka-widok',
  standalone: true,
  imports: [
    ActionButtonComponent,
    BackButtonComponent,
    CommonModule
  ],
  templateUrl: './zarowka-widok.html',
  styleUrl: './zarowka-widok.css'
})
export class ZarowkaWidok implements OnInit {
  lightId: number = 0;
  light: Light | undefined;

  lights: Light[] = [
    {
      id: 1,
      name: 'Żarówka w salonie',
      isOn: true,
      brightness: 80,
      temperature: 3000
    },
    {
      id: 2,
      name: 'Żarówka w sypialni',
      isOn: false,
      brightness: 50,
      temperature: 2700
    },
    {
      id: 3,
      name: 'Żarówka w kuchni',
      isOn: true,
      brightness: 100,
      temperature: 4000
    },
    {
      id: 4,
      name: 'Żarówka w łazience',
      isOn: false,
      brightness: 60,
      temperature: 3500
    }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.lightId = +idParam;
        this.loadLightDetails();
      } else {
        this.router.navigate(['/zarowki']);
      }
    });
  }

  loadLightDetails(): void {
    this.light = this.lights.find(light => light.id === this.lightId);
    if (!this.light) {
      this.router.navigate(['/zarowki']);
    }
  }

  toggleLight(): void {
    if (this.light) {
      this.light.isOn = !this.light.isOn;

    }
  }

  onBrightnessChange(event: Event): void {
    if (this.light) {
      const value = parseInt((event.target as HTMLInputElement).value);
      this.light.brightness = value;
    }
  }

  onTemperatureChange(event: Event): void {
    if (this.light) {
      const value = parseInt((event.target as HTMLInputElement).value);
      this.light.temperature = value;
    }
  }
}

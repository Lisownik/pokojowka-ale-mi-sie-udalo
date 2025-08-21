import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BackButtonComponent } from '../back-button/back-button.component';
import { ActionButtonComponent } from '../action-button/action-button.component';
import { CommonModule } from '@angular/common';

interface Light {
  id: number;
  name: string;
  isOn: boolean;
  brightness: number;
}

@Component({
  selector: 'app-zarowki',
  standalone: true,
  imports: [BackButtonComponent, ActionButtonComponent, CommonModule],
  templateUrl: './zarowki.component.html',
  styleUrls: ['./zarowki.component.css']
})
export class ZarowkiComponent {
  lights: Light[] = [
    {
      id: 1,
      name: 'Żarówka w salonie',
      isOn: true,
      brightness: 80
    },
    {
      id: 2,
      name: 'Żarówka w sypialni',
      isOn: false,
      brightness: 50
    },
    {
      id: 3,
      name: 'Żarówka w kuchni',
      isOn: true,
      brightness: 100
    },
    {
      id: 4,
      name: 'Żarówka w łazience',
      isOn: false,
      brightness: 60
    }
  ];

  constructor(private router: Router) {}

  toggleLight(id: number) {
    const light = this.lights.find(light => light.id === id);
    if (light) {
      light.isOn = !light.isOn;
    }
  }

  onBrightnessChange(event: Event, id: number) {
    const value = parseInt((event.target as HTMLInputElement).value);
    const light = this.lights.find(light => light.id === id);
    if (light) {
      light.brightness = value;
    }
  }

  goToLight(id: number) {
    this.router.navigate(['/zarowka-widok', id]);
  }
}

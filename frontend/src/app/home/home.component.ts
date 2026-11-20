import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { ClockComponent } from '../zegarek/zegarek.component';
import { HouseRepository } from '../services/repo/house/house-repository';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, ClockComponent, TranslocoPipe],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  protected house = computed(() => this.houseRepo.state()[0]);

  constructor(public houseRepo: HouseRepository) {}

  toggleDoor(): void {
    this.houseRepo.toggleDoor();
  }
}

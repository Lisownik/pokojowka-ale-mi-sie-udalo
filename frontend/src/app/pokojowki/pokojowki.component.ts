import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { BackButtonComponent } from '../back-button/back-button.component';
import { RoomsRepository } from '../services/repo/rooms/rooms-repository';
import { QualityPipe } from '../pipes/quality.pipe';

@Component({
  selector: 'app-pokojowki',
  standalone: true,
  imports: [CommonModule, BackButtonComponent, TranslocoPipe, QualityPipe],
  templateUrl: './pokojowki.component.html',
  styleUrl: './pokojowki.component.css'
})
export class PokojowkiComponent {
  protected rooms = computed(() => this.roomRepo.state());

  constructor(private router: Router, public roomRepo: RoomsRepository) {}

  goToRoom(roomId: string): void {
    this.router.navigate(['/pokojowka-widok', roomId]);
  }
}

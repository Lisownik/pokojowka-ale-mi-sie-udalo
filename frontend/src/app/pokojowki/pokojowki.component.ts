import {Component, computed} from '@angular/core';
import {BackButtonComponent} from "../back-button/back-button.component";
import {Router} from "@angular/router";
import {AnimatedCounterComponent} from '../animated-counter/animated-counter.component';
import {TranslocoPipe} from '@ngneat/transloco';
import {RoomsRepository} from '../services/repo/rooms/rooms-repository';

@Component({
  selector: 'app-pokojowki',
  imports: [
    BackButtonComponent
    ,AnimatedCounterComponent, TranslocoPipe
  ],
  templateUrl: './pokojowki.component.html',
  styleUrl: './pokojowki.component.css'
})
export class PokojowkiComponent {

  protected rooms = computed(() => this.roomRepo.state());

  constructor(private router: Router, public roomRepo: RoomsRepository) {
  }

  goToRoom(roomId: string) {
    this.router.navigate([`/pokojowka-widok/${roomId}`]);
  }

  protected readonly parseInt = parseInt;
}

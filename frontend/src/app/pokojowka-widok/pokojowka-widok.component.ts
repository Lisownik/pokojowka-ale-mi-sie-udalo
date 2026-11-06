import {Component, computed, inject} from '@angular/core';
import {BackButtonComponent} from '../back-button/back-button.component';
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from '@angular/router';
import {AnimatedCounterComponent} from '../animated-counter/animated-counter.component';
import {TranslocoPipe} from '@ngneat/transloco';
import {RoomData} from '../types/types';
import {RoomsRepository} from '../services/repo/rooms/rooms-repository';
import {QualityPipe} from '../pipes/quality.pipe';

@Component({
  selector: 'app-pokojowka-widok',
  imports: [
    BackButtonComponent
    , AnimatedCounterComponent, TranslocoPipe, QualityPipe
  ],
  templateUrl: './pokojowka-widok.component.html',
  styleUrl: './pokojowka-widok.component.css'
})
export class PokojowkaWidokComponent {
  readonly userId: string | null;
  room = computed(() => this.roomsRepo.state().find((room: RoomData) => room.id === this.userId));


  private route = inject(ActivatedRoute);
  snapshot: ActivatedRouteSnapshot;
  constructor(public router: Router, public roomsRepo: RoomsRepository) {
    this.snapshot = this.route.snapshot;
    this.userId = this.snapshot.paramMap.get('id') ?? "0";
    this.init()
  }

  init() {
    console.log('Pokojowka Widok', this.userId);
    if(!this.room)
      this.router.navigate(['/pokojowki']);
  }


  protected readonly parseInt = parseInt;
  protected readonly Number = Number;
  protected readonly Math = Math;
}

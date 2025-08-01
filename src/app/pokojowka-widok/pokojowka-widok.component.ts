import {Component, inject} from '@angular/core';
import {BackButtonComponent} from '../back-button/back-button.component';
import {ActionButtonComponent} from '../action-button/action-button.component';
import {ActivatedRoute, ActivatedRouteSnapshot, Data, Router, RouterOutlet} from '@angular/router';
import {DataService, RoomInfo} from '../data.service';
import {AnimatedCounterComponent} from '../animated-counter/animated-counter.component';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-pokojowka-widok',
  imports: [
    BackButtonComponent
    , ActionButtonComponent, AnimatedCounterComponent
  ],
  templateUrl: './pokojowka-widok.component.html',
  styleUrl: './pokojowka-widok.component.css'
})
export class PokojowkaWidokComponent {
  readonly userId: string | null;
  room: RoomInfo | undefined;
  private route = inject(ActivatedRoute);
  snapshot: ActivatedRouteSnapshot;
  constructor(public dataService: DataService, public router: Router) {
    this.snapshot = this.route.snapshot;
    this.userId = this.snapshot.paramMap.get('id') ?? "0";
    this.init()
    this.dataService.rooms$.subscribe(rooms => this.init())
  }

  init() {
    console.log('Pokojowka Widok', this.userId);
    this.room = this.dataService.rooms.find(room => room.id === this.userId);
    if(!this.room)
      this.router.navigate(['/pokojowki']);
  }

  air_quality_context(AQI: number): string {
       if (AQI > 50000)
           return "Perfekcyjne";
       else if (AQI > 20000)
           return "Dobre"
       else if (AQI > 10000)
           return "Przeciętne"
       else if (AQI > 5000)
           return "Złe"
       else
           return "Nie zdrowe"
  }

  protected readonly parseInt = parseInt;
  protected readonly Number = Number;
  protected readonly Math = Math;
}

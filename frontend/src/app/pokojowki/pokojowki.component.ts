import {Component, Inject} from '@angular/core';
import {BackButtonComponent} from "../back-button/back-button.component";
import {ActionButtonComponent} from '../action-button/action-button.component';
import {Data, Router} from "@angular/router";
import {DataService, RoomInfo} from '../data.service';
import {AnimatedCounterComponent} from '../animated-counter/animated-counter.component';

@Component({
  selector: 'app-pokojowki',
  imports: [
    BackButtonComponent
    , ActionButtonComponent, AnimatedCounterComponent
  ],
  templateUrl: './pokojowki.component.html',
  styleUrl: './pokojowki.component.css'
})
export class PokojowkiComponent {
  // dataService: DataService = Inject(DataService)

  constructor(private router: Router, public dataService: DataService) {
  }

  goToRoom(roomId: string) {
    this.router.navigate([`/pokojowka-widok/${roomId}`]);
  }

  protected readonly parseInt = parseInt;
}

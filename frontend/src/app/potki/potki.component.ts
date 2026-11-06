import { Component } from '@angular/core';
import { BackButtonComponent } from '../back-button/back-button.component';
import {Router} from '@angular/router';
import {TranslocoPipe} from '@ngneat/transloco';
import {AnimatedCounterComponent} from '../animated-counter/animated-counter.component';


@Component({
  selector: 'app-potki',
  standalone: true,
  imports: [BackButtonComponent
    , TranslocoPipe, AnimatedCounterComponent],
  templateUrl: './potki.component.html',
  styleUrl: './potki.component.css'
})
export class PotkiComponent {

  protected potki: any[] = []

  constructor(private router: Router) {
  }

    goTopot(id: string) {
      this.router.navigate([`/potka-widok/${id}`]);
    }


  protected readonly parseInt = parseInt;
}


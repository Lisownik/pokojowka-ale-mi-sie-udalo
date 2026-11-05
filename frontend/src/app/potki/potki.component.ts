import { Component } from '@angular/core';
import { BackButtonComponent } from '../back-button/back-button.component';
import {ActionButtonComponent} from '../action-button/action-button.component';
import {Router} from '@angular/router';


@Component({
  selector: 'app-potki',
  standalone: true,
  imports: [BackButtonComponent
  , ActionButtonComponent],
  templateUrl: './potki.component.html',
  styleUrl: './potki.component.css'
})
export class PotkiComponent {

  constructor(private router: Router) {
  }

    goTopot() {
      this.router.navigate(['/potka-widok']);
    }


}


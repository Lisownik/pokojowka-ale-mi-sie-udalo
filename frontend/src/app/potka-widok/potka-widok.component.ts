import { Component } from '@angular/core';
import {BackButtonComponent} from '../back-button/back-button.component';
import {ActionButtonComponent} from '../action-button/action-button.component';

@Component({
  selector: 'app-potka-widok',
  imports: [BackButtonComponent
  , ActionButtonComponent],
  templateUrl: './potka-widok.component.html',
  styleUrl: './potka-widok.component.css'
})
export class PotkaWidokComponent {

}

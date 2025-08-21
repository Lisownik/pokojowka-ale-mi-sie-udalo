import { Component } from '@angular/core';
import {ActionButtonComponent} from '../action-button/action-button.component';
import {BackButtonComponent} from '../back-button/back-button.component';

@Component({
  selector: 'app-zarowki',
  imports: [
    ActionButtonComponent,
    BackButtonComponent
  ],
  templateUrl: './zarowki.component.html',
  styleUrl: './zarowki.component.css'
})
export class ZarowkiComponent {

}

import { Component } from '@angular/core';
import { ActionButtonComponent } from '../action-button/action-button.component';
import {RouterModule} from '@angular/router';
import {ClockComponent} from '../zegarek/zegarek.component';

@Component({
  selector: 'app-home',
  imports: [ActionButtonComponent
    , RouterModule, ClockComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {}

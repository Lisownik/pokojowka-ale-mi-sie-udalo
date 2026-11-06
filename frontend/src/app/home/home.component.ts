import { Component } from '@angular/core';
import {RouterModule} from '@angular/router';
import {ClockComponent} from '../zegarek/zegarek.component';

@Component({
  selector: 'app-home',
  imports: [RouterModule, ClockComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {}

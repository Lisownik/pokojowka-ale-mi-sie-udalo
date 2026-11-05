import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {SidebarComponent} from './sidebar/sidebar.component';
import { ClockComponent } from './zegarek/zegarek.component';
import {ThemeChangeComponent} from './theme-change/theme-change.component';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, ClockComponent, ThemeChangeComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'pokojowka';
}

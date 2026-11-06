import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {SidebarComponent} from './sidebar/sidebar.component';
import {ThemeChangeComponent} from './theme-change/theme-change.component';
import {LanguageChangeComponent} from './language-change/language-change';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SidebarComponent, ThemeChangeComponent, LanguageChangeComponent],
  styleUrl: './app.component.css',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'pokojowka';
}

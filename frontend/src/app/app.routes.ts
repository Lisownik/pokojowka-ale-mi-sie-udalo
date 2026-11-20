import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { PokojowkiComponent } from './pokojowki/pokojowki.component';
import { PotkiComponent } from './potki/potki.component';
import { PokojowkaWidokComponent } from './pokojowka-widok/pokojowka-widok.component';
import { PotkaWidokComponent } from './potka-widok/potka-widok.component';
import { ZarowkiComponent } from './zarowki/zarowki.component';
import { ZarowkaWidok } from './zarowka-widok/zarowka-widok'
import { Heater } from './heater/heater';
import { HeaterView } from './heater-view/heater-view';
import { Windows } from './windows/windows';
import { WindowView } from './window-view/window-view';

export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'pokojowki', component: PokojowkiComponent },
  { path: 'potki', component: PotkiComponent },
  { path: 'potka-widok/:id', component: PotkaWidokComponent },
  { path: 'pokojowka-widok', component: PokojowkaWidokComponent },
  { path: 'pokojowka-widok/:id', component: PokojowkaWidokComponent },
  { path: 'zarowki', component: ZarowkiComponent },
  { path: 'zarowka-widok/:id', component: ZarowkaWidok },
  { path: 'heaters', component: Heater },
  { path: 'heater-view/:id', component: HeaterView },
  { path: 'windows', component: Windows },
  { path: 'window-view/:id', component: WindowView }
];

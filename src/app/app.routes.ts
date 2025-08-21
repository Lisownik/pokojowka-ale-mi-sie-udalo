import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { PokojowkiComponent } from './pokojowki/pokojowki.component';
import { PotkiComponent } from './potki/potki.component';
import { PokojowkaWidokComponent } from './pokojowka-widok/pokojowka-widok.component';
import { PotkaWidokComponent } from './potka-widok/potka-widok.component';
import { ZarowkiComponent } from './zarowki/zarowki.component';
import { ZarowkaWidok } from './zarowka-widok/zarowka-widok';

export const appRoutes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'pokojowki', component: PokojowkiComponent },
  { path: 'potki', component: PotkiComponent },
  { path: 'pokojowka-widok', component: PokojowkaWidokComponent },
  { path: 'pokojowka-widok/:id', component: PokojowkaWidokComponent },
  { path: 'potka-widok', component: PotkaWidokComponent },
  { path: 'zarowki', component: ZarowkiComponent },
  { path: 'zarowka-widok/:id', component: ZarowkaWidok }
];

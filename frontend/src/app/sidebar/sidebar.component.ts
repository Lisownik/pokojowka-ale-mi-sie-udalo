import {Component, inject} from '@angular/core';
import { RouterModule } from '@angular/router';
import {TranslocoPipe} from '@ngneat/transloco';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, TranslocoPipe],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  openedPanel: number | null = null;

  constructor() {
  }

  togglePanel(panelNumber: number) {
    this.openedPanel = this.openedPanel === panelNumber ? null : panelNumber;
  }
}

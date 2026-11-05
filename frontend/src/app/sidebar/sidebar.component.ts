import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  openedPanel: number | null = null;

  togglePanel(panelNumber: number) {
    this.openedPanel = this.openedPanel === panelNumber ? null : panelNumber;
  }
}

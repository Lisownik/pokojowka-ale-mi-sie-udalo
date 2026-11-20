import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { BackButtonComponent } from '../back-button/back-button.component';
import { WindowsRepository } from '../services/repo/window/windows-repository';

@Component({
  selector: 'app-windows',
  standalone: true,
  imports: [CommonModule, BackButtonComponent, TranslocoPipe],
  templateUrl: './windows.html',
  styleUrl: './windows.css'
})
export class Windows {
  protected windows = computed(() => this.windowsRepo.state());

  constructor(private router: Router, public windowsRepo: WindowsRepository) {}

  toggleWindow(id: string, power: boolean, event: Event): void {
    event.stopPropagation();
    this.windowsRepo.toggleOpen(id, power);
  }

  goToWindow(id: string): void {
    this.router.navigate(['/window-view', id]);
  }
}

import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { BackButtonComponent } from '../back-button/back-button.component';
import { WindowsRepository } from '../services/repo/window/windows-repository';
import { WindowData } from '../types/types';

@Component({
  selector: 'app-window-view',
  standalone: true,
  imports: [CommonModule, BackButtonComponent, TranslocoPipe],
  templateUrl: './window-view.html',
  styleUrl: './window-view.css'
})
export class WindowView implements OnInit {
  private windowId = signal('');
  protected win = computed<WindowData | undefined>(() =>
    this.windowsRepo.state().find(w => w.id === this.windowId())
  );

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public windowsRepo: WindowsRepository
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) this.windowId.set(id);
      else this.router.navigate(['/windows']);
    });
  }

  toggleOpen(power: boolean): void {
    const w = this.win();
    if (w) this.windowsRepo.toggleOpen(w.id, power);
  }
}

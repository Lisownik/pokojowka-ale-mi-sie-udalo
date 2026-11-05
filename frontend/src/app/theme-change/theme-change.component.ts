import { Component, Renderer2 } from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-theme-change',
  templateUrl: './theme-change.component.html',
  styleUrls: ['./theme-change.component.css']
  ,imports: [CommonModule]
})
export class ThemeChangeComponent {
  isLightMode = false;

  constructor(private renderer: Renderer2) {}

  toggleTheme() {
    this.isLightMode = !this.isLightMode;
    if (this.isLightMode) {
      this.renderer.addClass(document.body, 'light-theme');
    } else {
      this.renderer.removeClass(document.body, 'light-theme');
    }
  }
}

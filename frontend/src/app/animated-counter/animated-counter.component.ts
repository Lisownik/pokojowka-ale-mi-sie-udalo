import { Component, Input, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-animated-counter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './animated-counter.component.html',
  styleUrl: `./animated-counter.component.css`,
})
export class AnimatedCounterComponent implements OnInit, OnDestroy {
  @Input() value: number = 0;
  @Input() duration: number = 600;
  @Input() suffix: string = '';
  @Input() fontSize: number = 24;
  @Input() theme: 'default' | 'digital' | 'retro' = 'default';
  @Input() minDigits: number = 1;

  displayDigits: { value: number; offset: number }[] = [];
  private animationTimeouts: any[] = [];

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.updateDisplay();
  }

  ngOnDestroy() {
    this.animationTimeouts.forEach(timeout => clearTimeout(timeout));
  }

  ngOnChanges() {
    this.updateDisplay();
  }

  private updateDisplay() {
    const valueStr = Math.abs(this.value).toString();
    const newDigits = valueStr.split('').map(d => parseInt(d));

    if (this.displayDigits.length !== newDigits.length) {
      this.handleDigitCountChange(newDigits);
    } else {
      this.animateToNewValues(newDigits);
    }
  }

  private handleDigitCountChange(newDigits: number[]) {
    this.animationTimeouts.forEach(timeout => clearTimeout(timeout));
    this.animationTimeouts = [];

    this.displayDigits = newDigits.map(digit => ({
      value: digit,
      offset: (-digit * this.digitHeight)
    }));
    this.cdr.detectChanges();
  }

  private initializeDigits(digits: number[]) {
    this.displayDigits = digits.map(digit => ({
      value: digit,
      offset: -digit * this.fontSize * 1.4
    }));
    this.cdr.detectChanges();
  }

  get digitHeight(): number {
    return Math.round(this.fontSize * 1.4);
  }

  private animateToNewValues(newDigits: number[]) {
    newDigits.forEach((newDigit, index) => {
      if (this.displayDigits[index].value !== newDigit) {
        this.animateDigit(index, newDigit);
      }
    });
  }

  private animateDigit(index: number, targetValue: number) {
    const currentValue = this.displayDigits[index].value;
    const steps = this.getAnimationSteps(currentValue, targetValue);

    steps.forEach((step, stepIndex) => {
      const timeout = setTimeout(() => {
        this.displayDigits[index].value = step;
        this.displayDigits[index].offset = -step * this.digitHeight;
        this.cdr.detectChanges();
      }, (stepIndex * this.duration) / steps.length);

      this.animationTimeouts.push(timeout);
    });
  }

  private getAnimationSteps(from: number, to: number): number[] {
    const steps: number[] = [];
    const diff = to - from;

    if (Math.abs(diff) <= 1) {
      return [to];
    }

    const numSteps = Math.min(Math.abs(diff), 8);
    for (let i = 1; i <= numSteps; i++) {
      const progress = i / numSteps;
      const easeProgress = this.easeInOutCubic(progress);
      const value = Math.round(from + diff * easeProgress);
      steps.push(value);
    }

    return steps;
  }

  private easeInOutCubic(t: number): number {
    return t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
  }

}

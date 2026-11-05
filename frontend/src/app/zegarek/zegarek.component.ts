import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-clock',
  imports: [],
  standalone: true,
  templateUrl: './zegarek.component.html',
  styleUrls: ['./zegarek.component.css']
})
export class ClockComponent implements OnInit, OnDestroy {
  currentDateTime: string = '';
  private intervalId: any;

  ngOnInit(): void {
    this.updateDateTime();
    this.intervalId = setInterval(() => {
      this.updateDateTime();
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  private updateDateTime(): void {
    const now = new Date();
    const formattedDate = now.toLocaleDateString('pl-PL');
    const formattedTime = now.toLocaleTimeString('pl-PL');
    this.currentDateTime = `${formattedDate} - ${formattedTime}`;
  }
}

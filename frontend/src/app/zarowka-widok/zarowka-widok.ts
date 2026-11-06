import {Component, computed, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {BackButtonComponent} from '../back-button/back-button.component';
import {TranslocoPipe} from '@ngneat/transloco';
import {BulbData, RoomData} from '../types/types';
import {BulbsRepository} from '../services/repo/bulbs/bulbs-repository';


@Component({
  selector: 'app-zarowka-widok',
  standalone: true,
  imports: [
    BackButtonComponent,
    CommonModule,
    TranslocoPipe
  ],
  templateUrl: './zarowka-widok.html',
  styleUrl: './zarowka-widok.css'
})
export class ZarowkaWidok implements OnInit {

  lightId: string = "";
  light = computed(() => this.bulbsRepo.state().find((bulb: BulbData) => bulb.id === this.lightId));


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public bulbsRepo: BulbsRepository
  ) {

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      console.log("idParam", idParam);
      if (idParam) {
        this.lightId = idParam;
      } else {
        console.warn("Navigating to /zarowki");
        this.router.navigate(['/zarowki']);
      }
    });
  }


  toggleLight(): void {
    //TODO()
  }

  onBrightnessChange(event: Event): void {
    //TODO()
  }

  onTemperatureChange(event: Event): void {
    //TODO()
  }
}

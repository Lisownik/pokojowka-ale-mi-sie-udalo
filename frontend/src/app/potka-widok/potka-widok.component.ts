import {Component, inject} from '@angular/core';
import {BackButtonComponent} from '../back-button/back-button.component';
import {TranslocoPipe} from '@ngneat/transloco';
import {ActivatedRoute, ActivatedRouteSnapshot, Router} from '@angular/router';

@Component({
  selector: 'app-potka-widok',
  imports: [BackButtonComponent
    , TranslocoPipe],
  templateUrl: './potka-widok.component.html',
  styleUrl: './potka-widok.component.css'
})
export class PotkaWidokComponent {
  readonly userId: string | null;
  plant: any | undefined;
  private route = inject(ActivatedRoute);
  snapshot: ActivatedRouteSnapshot;
  constructor(public router: Router) {
    this.snapshot = this.route.snapshot;
    this.userId = this.snapshot.paramMap.get('id') ?? "0";
    this.init()
  }

  init() {
    console.log('Pokojowka Widok', this.userId);
    if(!this.plant){
      this.router.navigate(['/potki']);
      console.warn("No potka :(")
    }
  }

  protected transformWetness(wet: number): string {
    if (wet > 80)
        return "potki.wetness.very_wet";
    else if (wet > 60)
        return "potki.wetness.wet"
    else if (wet > 40)
        return "potki.wetness.normal"
    else if (wet > 20)
        return "potki.wetness.dry"
    else
        return "potki.wetness.very_dry"
  }

  protected transformSunlight(sun: number): string {
    if (sun > 80)
        return "potki.sunlight.very_sunny";
    else if (sun > 60)
        return "potki.sunlight.sunny"
    else if (sun > 40)
        return "potki.sunlight.normal"
    else if (sun > 20)
        return "potki.sunlight.cloudy"
    else
        return "potki.sunlight.very_cloudy"
  }

  protected tranformTemperature(temp: number): string {
    if (temp > 30)
        return "potki.temperature.very_hot";
    else if (temp > 25)
        return "potki.temperature.hot"
    else if (temp > 15)
        return "potki.temperature.normal"
    else if (temp > 5)
        return "potki.temperature.cold"
    else
        return "potki.temperature.very_cold"
  }

  protected transformHumidity(hum: number): string {
    if (hum > 80)
        return "potki.air_humidity.very_humid";
    else if (hum > 60)
        return "potki.air_humidity.humid"
    else if (hum > 40)
        return "potki.air_humidity.normal"
    else if (hum > 20)
        return "potki.air_humidity.dry"
    else
        return "potki.air_humidity.very_dry"
  }
}

import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'humidity'})
export class HumidityPipe implements PipeTransform {
  transform(hum: number): string {
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

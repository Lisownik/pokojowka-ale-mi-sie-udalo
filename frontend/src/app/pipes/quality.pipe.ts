import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'qualityMeasure'})
export class QualityPipe implements PipeTransform {
  transform(AQI: number): string {
    if (AQI > 50000)
      return "Perfekcyjne";
    else if (AQI > 20000)
      return "Dobre"
    else if (AQI > 10000)
      return "Przeciętne"
    else if (AQI > 5000)
      return "Złe"
    else
      return "Nie zdrowe"
  }
}

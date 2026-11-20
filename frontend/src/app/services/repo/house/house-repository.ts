import { Injectable, signal, WritableSignal } from '@angular/core';
import { Repository } from '../../repository';
import { HouseData } from '../../../types/types';
import { Observable } from 'rxjs';
import { SseService } from '../../sse/sse-service';
import { environment } from '../../../../environments/environment';

const MOCK_HOUSE: HouseData[] = [
  {
    id: 'house-1',
    type: 'roof',
    online: true,
    lastSeen: Date.now(),
    name: 'Mój Dom',
    state: {
      outdoorTemp: 18.5,
      rainDetected: false,
      doorOpen: false
    }
  }
];

@Injectable({
  providedIn: 'root'
})
export class HouseRepository extends Repository<HouseData> {
  override state: WritableSignal<HouseData[]> = signal<HouseData[]>(
    environment.staticMode ? MOCK_HOUSE : []
  );

  private roomObservable: Observable<Array<HouseData>> | null = null;

  constructor(protected sseService: SseService) {
    super();

    if (!environment.staticMode) {
      // TODO
    }
  }

  override getData(): HouseData[] {
    return this.state();
  }

  toggleDoor(): void {
    this.state.update(houses =>
      houses.map((h, i) =>
        i === 0 ? { ...h, state: { ...h.state, doorOpen: !h.state.doorOpen } } : h
      )
    );
  }
}

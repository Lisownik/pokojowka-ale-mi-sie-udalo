import { Injectable, signal, WritableSignal } from '@angular/core';
import { Repository } from '../../repository';
import { BulbData } from '../../../types/types';
import {firstValueFrom, Observable} from 'rxjs';
import { SseService } from '../../sse/sse-service';
import { environment } from '../../../../environments/environment';

const MOCK_BULBS: BulbData[] = [
  {
    id: 'b1',
    type: 'yeelight',
    online: true,
    lastSeen: Date.now(),
    name: 'Salon',
    state: {
      power: true,
      brightness: 70,
      temperature: 3500
    }
  },
  {
    id: 'b2',
    type: 'yeelight',
    online: true,
    lastSeen: Date.now(),
    name: 'Sypialnia',
    state: {
      power: false,
      brightness: 40,
      temperature: 2700
    }
  },
  {
    id: 'b3',
    type: 'smart_led',
    online: true,
    lastSeen: Date.now(),
    name: 'Kuchnia',
    state: {
      power: true,
      brightness: 90,
      temperature: 4500
    }
  },
  {
    id: 'b4',
    type: 'smart_led',
    online: true,
    lastSeen: Date.now(),
    name: 'Biuro',
    state: {
      power: true,
      brightness: 55,
      temperature: 5000
    }
  }
];

@Injectable({
  providedIn: 'root'
})
export class BulbsRepository extends Repository<Array<BulbData>> {
  override state: WritableSignal<BulbData[]> = signal<BulbData[]>(
    environment.staticMode ? MOCK_BULBS : []
  );

  private bulbObservable: Observable<Array<BulbData>> | null = null;

  constructor(protected sseService: SseService) {
    super();

    if (environment.staticMode)
      return

    this.bulbObservable = sseService.createEventSource<Array<BulbData>>('bulbs');

    this.bulbObservable.subscribe(roomData => {
      this.state.set(roomData);
    });

  }

  override getData(): BulbData[] {
    return this.state();
  }

  togglePower(id: string, power: boolean): Promise<unknown> {
    this.state.update(bulbs =>
      bulbs.map(b =>
        b.id === id ? { ...b, state: { ...b.state, power: power } } : b
      )
    );

    return firstValueFrom(
      this.http.put(
        this.requestUrl(`/devices/bulb/${id}/state/${power}`),
        {}
      )
    )
  }

  setBrightness(id: string, brightness: number): Promise<unknown> {
    this.state.update(bulbs =>
      bulbs.map(b =>
        b.id === id ? { ...b, state: { ...b.state, brightness } } : b
      )
    );

    return firstValueFrom(
      this.http.put(
        this.requestUrl(`/devices/bulb/${id}/brightness/${brightness}`),
        {}
      )
    )
  }

  setTemperature(id: string, temperature: number): void {
    this.state.update(bulbs =>
      bulbs.map(b =>
        b.id === id ? { ...b, state: { ...b.state, temperature } } : b
      )
    );

  }
}

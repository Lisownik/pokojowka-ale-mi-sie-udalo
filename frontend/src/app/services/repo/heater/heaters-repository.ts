import {Injectable, OnDestroy, signal, WritableSignal} from '@angular/core';
import {Repository} from '../../repository';
import {HeaterData} from '../../../types/types';
import {firstValueFrom, Observable} from 'rxjs';
import {SseService} from '../../sse/sse-service';
import {environment} from '../../../../environments/environment';

const MOCK_HEATERS: HeaterData[] = [
  {
    id: 'h1',
    type: 'heater',
    online: true,
    lastSeen: Date.now(),
    name: 'Grzejnik Salon',
    state: {
      power: true,
      temperature: 21.5,
      temperatureGoal: 23.0
    }
  },
  {
    id: 'h2',
    type: 'heater',
    online: true,
    lastSeen: Date.now(),
    name: 'Grzejnik Sypialnia',
    state: {
      power: false,
      temperature: 19.5,
      temperatureGoal: 21.0
    }
  },
  {
    id: 'h3',
    type: 'heater',
    online: true,
    lastSeen: Date.now(),
    name: 'Grzejnik Kuchnia',
    state: {
      power: true,
      temperature: 22.0,
      temperatureGoal: 22.5
    }
  }
];

@Injectable({
  providedIn: 'root'
})
export class HeatersRepository extends Repository<Array<HeaterData>> {
  override state: WritableSignal<HeaterData[]> = signal<HeaterData[]>(
    environment.staticMode ? MOCK_HEATERS : []
  );

  private heaterObservable: Observable<Array<HeaterData>> | null = null;

  constructor(protected sseService: SseService) {
    super();

    if (environment.staticMode)
      return

    this.heaterObservable = sseService.createEventSource<Array<HeaterData>>("heaters");

    this.heaterObservable.subscribe(HeaterData => {
      this.state.set(HeaterData);
    });

  }

  override getData(): HeaterData[] {
    return this.state();
  }

  togglePower(id: string, power: boolean): Promise<unknown> {
    this.state.update(heaters =>
      heaters.map(h =>
        h.id === id ? { ...h, state: { ...h.state, power: power } } : h
      )
    );

    return firstValueFrom(
      this.http.put(
        this.requestUrl(`/devices/heater/${id}/state/${power}`),
        {}
      )
    )
  }

  setTemperatureGoal(id: string, temperatureGoal: number): Promise<unknown> {
    const clamped = Math.min(28, Math.max(16, Math.round(temperatureGoal * 2) / 2));
    this.state.update(heaters =>
      heaters.map(h =>
        h.id === id ? { ...h, state: { ...h.state, temperatureGoal: clamped } } : h
      )
    );

    return firstValueFrom(
      this.http.put(
        this.requestUrl(`/devices/heater/${id}/temperature_goal/${temperatureGoal}`),
        {}
      )
    )
  }
}

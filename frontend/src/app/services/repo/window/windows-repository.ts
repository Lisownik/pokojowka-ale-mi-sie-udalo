import { Injectable, signal, WritableSignal } from '@angular/core';
import { Repository } from '../../repository';
import { WindowData } from '../../../types/types';
import {firstValueFrom, Observable} from 'rxjs';
import { SseService } from '../../sse/sse-service';
import { environment } from '../../../../environments/environment';

const MOCK_WINDOWS: WindowData[] = [
  {
    id: 'w1',
    type: 'window',
    online: true,
    lastSeen: Date.now(),
    name: 'Okno Salon',
    state: {
      open: true,
      outdoorTemp: 21.0,
      rainDetected: false
    }
  },
  {
    id: 'w2',
    type: 'window',
    online: true,
    lastSeen: Date.now(),
    name: 'Okno Sypialnia',
    state: {
      open: false,
      outdoorTemp: 19.0,
      rainDetected: false
    }
  },
  {
    id: 'w3',
    type: 'window',
    online: true,
    lastSeen: Date.now(),
    name: 'Okno Kuchnia',
    state: {
      open: false,
      outdoorTemp: 17.5,
      rainDetected: true
    }
  }
];

@Injectable({
  providedIn: 'root'
})
export class WindowsRepository extends Repository<Array<WindowData>> {
  override state: WritableSignal<WindowData[]> = signal<WindowData[]>(
    environment.staticMode ? MOCK_WINDOWS : []
  );

  private windowObservable: Observable<Array<WindowData>> | null = null;

  constructor(protected sseService: SseService) {
    super();

    if (environment.staticMode)
      return

    this.windowObservable = sseService.createEventSource<Array<WindowData>>('windows');

    this.windowObservable.subscribe(windowData => {
      this.state.set(windowData);
    });

  }

  override getData(): WindowData[] {
    return this.state();
  }

  toggleOpen(id: string, power: boolean): Promise<unknown> {
    this.state.update(windows =>
      windows.map(w =>
        w.id === id ? { ...w, state: { ...w.state, open: !w.state.open } } : w
      )
    );

    return firstValueFrom(
      this.http.put(
        this.requestUrl(`/devices/window/${id}/state/${power}`),
        {}
      )
    )
  }
}

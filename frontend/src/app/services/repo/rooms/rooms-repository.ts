import { Injectable, signal, WritableSignal } from '@angular/core';
import { Repository } from '../../repository';
import { SseService } from '../../sse/sse-service';
import { Observable } from 'rxjs';
import { RoomData } from '../../../types/types';
import { environment } from '../../../../environments/environment';

const MOCK_ROOMS: RoomData[] = [
  {
    id: 'r1',
    type: 'room',
    online: true,
    lastSeen: Date.now(),
    name: 'Sypialnia',
    state: {
      temperature: 21.5,
      humidity: 45.2,
      pressure: 1012.5,
      quality: 54000
    }
  },
  {
    id: 'r2',
    type: 'room',
    online: true,
    lastSeen: Date.now(),
    name: 'Salon',
    state: {
      temperature: 23.2,
      humidity: 52.5,
      pressure: 1011.2,
      quality: 28500
    }
  },
  {
    id: 'r3',
    type: 'room',
    online: true,
    lastSeen: Date.now(),
    name: 'Kuchnia',
    state: {
      temperature: 25.4,
      humidity: 64.5,
      pressure: 1009.8,
      quality: 14200
    }
  },
  {
    id: 'r4',
    type: 'room',
    online: true,
    lastSeen: Date.now(),
    name: 'Łazienka',
    state: {
      temperature: 22.8,
      humidity: 72.5,
      pressure: 1010.5,
      quality: 7500
    }
  }
];

@Injectable({
  providedIn: 'root'
})
export class RoomsRepository extends Repository<Array<RoomData>> {
  override state: WritableSignal<RoomData[]> = signal<RoomData[]>(
    environment.staticMode ? MOCK_ROOMS : []
  );

  private roomObservable: Observable<Array<RoomData>> | null = null;

  constructor(protected sseService: SseService) {
    super();

    if (environment.staticMode)
      return

    this.roomObservable = sseService.createEventSource<Array<RoomData>>('rooms');

    this.roomObservable.subscribe(roomData => {
      this.state.set(roomData);
    });

  }

  override getData(): RoomData[] {
    return this.state();
  }
}

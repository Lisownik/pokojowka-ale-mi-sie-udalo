import {Injectable, signal, WritableSignal} from '@angular/core';
import {Repository} from '../../repository';
import {BulbData, RoomData} from '../../../types/types';
import {Observable} from 'rxjs';
import {SseService} from '../../sse/sse-service';

@Injectable({
  providedIn: 'root'
})
export class BulbsRepository extends Repository<BulbData> {
  override state: WritableSignal<BulbData[]> = signal<BulbData[]>([]);

private roomObservable: Observable<Array<BulbData>> | null = null;

constructor(protected sseService: SseService) {
  super();
  this.roomObservable = sseService.createEventSource<Array<BulbData>>("bulbs");

  this.roomObservable.subscribe(roomData => {
    this.state.set(roomData);
  })
}

override getData(): BulbData[] {
  return this.state();
}

}

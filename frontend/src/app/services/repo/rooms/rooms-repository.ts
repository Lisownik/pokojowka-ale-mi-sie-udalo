import {Injectable, signal, Signal, WritableSignal} from '@angular/core';
import {Repository} from '../../repository';
import {SseService} from '../../sse/sse-service';
import {Observable } from 'rxjs';
import {RoomData} from '../../../types/types';

@Injectable({
  providedIn: 'root'
})



export class RoomsRepository extends Repository<RoomData> {
    override state: WritableSignal<RoomData[]> = signal<RoomData[]>([]);

    private roomObservable: Observable<Array<RoomData>> | null = null;

    constructor(protected sseService: SseService) {
      super();
      this.roomObservable = sseService.createEventSource<Array<RoomData>>("rooms");

      this.roomObservable.subscribe(roomData => {
        this.state.set(roomData);
      })
    }

    override getData(): RoomData[] {
      return this.state();
    }

}

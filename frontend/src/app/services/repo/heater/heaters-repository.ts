import {Injectable, signal, WritableSignal} from '@angular/core';
import {Repository} from '../../repository';
import {HeaterData} from '../../../types/types';
import {Observable} from 'rxjs';
import {SseService} from '../../sse/sse-service';

@Injectable({
  providedIn: 'root'
})
export class HeatersRepository extends Repository<HeaterData> {
  override state: WritableSignal<HeaterData[]> = signal<HeaterData[]>([]);

  private roomObservable: Observable<Array<HeaterData>> | null = null;

  constructor(protected sseService: SseService) {
    super();
    this.roomObservable = sseService.createEventSource<Array<HeaterData>>("heaters");

    this.roomObservable.subscribe(HeaterData => {
      this.state.set(HeaterData);
    })
  }

  override getData(): HeaterData[] {
    return this.state();
  }

}

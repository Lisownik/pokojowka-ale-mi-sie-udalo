import {Injectable, signal, WritableSignal} from '@angular/core';
import {Repository} from '../../repository';
import {HouseData} from '../../../types/types';
import {Observable} from 'rxjs';
import {SseService} from '../../sse/sse-service';

@Injectable({
  providedIn: 'root'
})
export class HouseRepository  extends Repository<HouseData> {
  override state: WritableSignal<HouseData[]> = signal<HouseData[]>([]);

  private roomObservable: Observable<Array<HouseData>> | null = null;

  constructor(protected sseService: SseService) {
    super();
    //TODO()

    // this.roomObservable = sseService.createEventSource<Array<HouseData>>("rooms");
    //
    // this.roomObservable.subscribe(HouseData => {
    //   console.log(HouseData);
    //
    //   this.state.set(HouseData);
    // })
  }

  override getData(): HouseData[] {
    return this.state();
  }

}

import { Injectable, signal, WritableSignal } from '@angular/core';
import { Repository } from '../../repository';
import { HouseData } from '../../../types/types';
import {firstValueFrom, Observable} from 'rxjs';
import { SseService } from '../../sse/sse-service';
import { environment } from '../../../../environments/environment';

const MOCK_HOUSE: HouseData = {
  outdoorTemp: 18.5,
  rain: 0,
  open: false,
}

@Injectable({
  providedIn: 'root'
})
export class HouseRepository extends Repository<HouseData>{
  override state: WritableSignal<HouseData> = signal<HouseData>(
    environment.staticMode ? MOCK_HOUSE : ({} as HouseData)
  );

  private houseObservable: Observable<HouseData> | null = null;

  constructor(protected sseService: SseService) {
    super();

    if (environment.staticMode)
      return

    this.houseObservable = sseService.createEventSource<HouseData>('home');

    this.houseObservable.subscribe(HouseData => {
      console.log(HouseData);
      this.state.set(HouseData);
    });
  }

  override getData(): HouseData {
    return this.state();
  }

  toggleDoor(state: boolean): Promise<unknown> {
    const newHouseData = this.state();
    newHouseData.open = state;

    this.state.set(
      newHouseData
    );

    return firstValueFrom(
      this.http.put(
        this.requestUrl(`/devices/door/state/${state}`),
        {}
      )
    )
  }
}

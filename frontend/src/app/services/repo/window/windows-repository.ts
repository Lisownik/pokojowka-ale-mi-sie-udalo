import {Injectable, signal, WritableSignal} from '@angular/core';
import {Repository} from '../../repository';
import {WindowData} from '../../../types/types';
import {Observable} from 'rxjs';
import {SseService} from '../../sse/sse-service';

@Injectable({
  providedIn: 'root'
})
export class WindowsRepository extends Repository<WindowData> {
  override state: WritableSignal<WindowData[]> = signal<WindowData[]>([]);

  private roomObservable: Observable<Array<WindowData>> | null = null;

  constructor(protected sseService: SseService) {
    super();
    this.roomObservable = sseService.createEventSource<Array<WindowData>>("windows");

    this.roomObservable.subscribe(WindowData => {
      this.state.set(WindowData);
    })
  }

  override getData(): WindowData[] {
    return this.state();
  }

}

import { Injectable } from '@angular/core';
import {Observable, retry, timer} from 'rxjs';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SseService {

  constructor() {
  }

  createEventSource<T>(path: string): Observable<T> {
    const topicUrl = `${environment.apiUrl}/stream/${path.trim()}`;

    return new Observable<T>(observer => {
      const eventSource = new EventSource(topicUrl);

      eventSource.onmessage = event => {
        const messageData: unknown = JSON.parse(event.data);
        observer.next(messageData as T);
      }

      eventSource.onerror = event => {
        console.error(`Event source of ${path} struck an error: `, event);

        if (eventSource.readyState === EventSource.CLOSED) {
          observer.error(event);
        }
      }

      return () => eventSource.close();
    }).pipe(
      retry({
        delay: (_error, retryCount) =>
          timer(Math.min(1000 * 2 ** (retryCount - 1), 30_000)),
        resetOnSuccess: true,
      })
    );
  }

}

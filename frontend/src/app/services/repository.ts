import {inject, Signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '../../environments/environment';

export abstract class Repository<T> {
  protected http = inject(HttpClient);

  protected requestUrl(path: string): string{
    return `${environment.apiUrl}${path}`
  }

  abstract state: Signal<T>;
  abstract getData(): T;
}

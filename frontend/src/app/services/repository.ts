import {Signal} from '@angular/core';

export abstract class Repository<T> {
  abstract state: Signal<Array<T>>;

  abstract getData(): Array<T>;

}

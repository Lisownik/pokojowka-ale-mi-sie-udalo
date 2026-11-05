import {inject, Injectable} from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {signal} from '@angular/core';

export interface RoomInfo {
  id: string;
  name: string;
  data: RoomData;
}

export interface RoomData {
  temperature: number;
  humidity: number;
  quality: number;
  pressure: number;
  oxygen_monoxide: number;
  other_dangerous_gasses: number;
}


@Injectable({
  providedIn: 'root'
})
export class DataService {
  private roomsSubject = new Subject<RoomInfo[]>();
  public rooms$ = this.roomsSubject.asObservable();
  public rooms: Array<RoomInfo> = [
    {
      id: "asdge",
      name: "Pokój 1", data: {
        temperature: 19,
        humidity: 41,
        quality: 47,
        pressure: 1021,
        oxygen_monoxide: 0,
        other_dangerous_gasses: 0
      }},
    {
      id: "agoi12asbv325",
      name: "Pokój 2", data: {
        temperature: 27,
        humidity: 23,
        quality: 32,
        pressure: 1001,
        oxygen_monoxide: 0,
        other_dangerous_gasses: 0
      }},
    {
      id: "157276baS",
      name: "Pokój 3", data: {
        temperature: 23,
        humidity: 54,
        quality: 39,
        pressure: 1005,
        oxygen_monoxide: 0,
        other_dangerous_gasses: 0
      }},
    {
      id: "46842fsaghg85",
      name: "Pokój 4", data: {
        temperature: 27,
        humidity: 48,
        quality: 36,
        pressure: 1003,
        oxygen_monoxide: 0,
        other_dangerous_gasses: 0
      }}
  ];
  public roomAvg: RoomData = this.calcRoomAvg(this.rooms);
  private http = inject(HttpClient);

  constructor() {
    this.fetchRooms()
    setInterval(() => {
      console.table(this.rooms)
      this.fetchRooms()
    }, 2500)
  }

  fetchRooms() {
    this.http.get('http://localhost:3333/rooms', {
      responseType: "json",
    }).subscribe({
      next: (data) => {
        console.log('Config fetched successfully:', data);
        this.rooms = data as RoomInfo[];
        this.roomsSubject.next(this.rooms);
        this.calcRoomAvg(this.rooms);
        },
      error: err => {
      }
    });
  }

  calcRoomAvg(rooms: RoomInfo[]): RoomData {
    let avg: RoomData = {temperature: 0, quality: 0, humidity: 0, pressure: 0, oxygen_monoxide: 0, other_dangerous_gasses: 0};
    // Temperatura
    let tempArr = rooms.map((room: RoomInfo) => room.data.temperature);
    tempArr.forEach((temperature: number) => {
      avg.temperature += temperature;
    })
    avg.temperature /= tempArr.length;

    // Ciśnienie
    let pressureArr = rooms.map((room: RoomInfo) => room.data.pressure);
    pressureArr.forEach((pressure: number) => {
      avg.pressure += pressure;
    })
    avg.pressure /= pressureArr.length;
    console.log("AVG: ", avg)
    return avg;
  }
}


export type moduleType = "room" | "window" | "yeelight" | "smart_led" | "mobile" | "heater" | "roof";

export interface SseData<T = any> {
  id: string;
  type: moduleType;
  online: boolean;
  lastSeen: number;
  name: string;
  state: T;
}



export interface RoomData extends SseData{
  state: {
    temperature: number;
    humidity: number;
    pressure: number;
    quality: number;
  }
}

export interface BulbData extends SseData{
  state: {
    power: boolean;
    brightness: number;
    temperature: number;
  }
}

export interface HeaterData extends SseData{
  state: {
    power: boolean,
    temperatureGoal: number,
    temperature: number
  }
}

export interface WindowData extends SseData{
  state: {
    open: boolean;
  }
}

export interface HouseData extends SseData{
  state: {
    //TODO()
  }
}

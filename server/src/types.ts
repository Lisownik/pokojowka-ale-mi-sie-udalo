export type DeviceType = "room" | "window" | "yeelight" | "smart_ledzik"

export interface DeviceState{
  temperature?: number;
  humidity?: number;
  open?: boolean;
  position?: number;
  [key: string]: any;
}

export interface Device {
  id: string;
  name: string;
  type: DeviceType;
  online: boolean;
  lastSeen: number;
  state: DeviceState;
}

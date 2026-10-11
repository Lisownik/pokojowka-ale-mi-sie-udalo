export type DeviceType = "room" | "window" | "yeelight" | "smart_led" | "mobile"

const validDeviceTypes: DeviceType[] = ["room", "window", "yeelight", "smart_led", "mobile"];

export const isDeviceType = (value: string): boolean => validDeviceTypes.includes(value as DeviceType);


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

export type EventType = "cold" | "hot" | "programmed" | "go-out" | "go-in" | "bad-air"
export type ActionType = "close" | "open" | "heat-up" | "heat-down";
export type DeviceNames = Array<Record<string, string>>;

export const anyID = "<any>"

export interface ActionSlave {
  module_type: DeviceType;
  id: string;
  action: ActionType;
}

export interface EventToAction {
  module_type: DeviceType,
  id: string;
  event: EventType;
  slave: ActionSlave
}

export interface ShortDevice {
  id: string;
  type: string;
}

export type EventActionConnections = EventToAction[];
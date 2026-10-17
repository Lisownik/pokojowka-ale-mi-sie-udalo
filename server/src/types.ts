export type DeviceType = "room" | "window" | "yeelight" | "smart_led" | "mobile" | "heater"

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


export type WindowActions = "close" | "open"
export type HeaterActions = "heat-up" | "heat-down"
export type BulbActions = "on" | "off" | "brightness"
export type ActionType = WindowActions | HeaterActions | BulbActions
export type WindowState = "close" | "open"

export interface DeviceData{
  type: DeviceType;
  name: string;
}

export type DeviceNames = Record<string, DeviceData>;

export const anyID = "<any>"
export const everyID = "<every>"

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
  type: DeviceType;
}

export type EventActionConnections = EventToAction[];
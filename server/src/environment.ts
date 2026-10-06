import {DeviceType} from "./types";

export const Environment = {
  WSS_PORT: Number(process.env.WSS_PORT) | 1234,
  MQTT_URL: process.env.MQTT_URL as string | "mqtt://localhost:1883",
  REST_PORT: Number(process.env.REST_PORT) | 3333,
}

export namespace Simple {

  function random(min: number, max: number) {
    return Math.floor(Math.random() * (max - min)) + min;
  }

  export function id(): string {
    return crypto.randomUUID()
  }

  export function name(type: DeviceType = "room"): string {
    return `${type}_${random(0,256).toString(16)}_${random(0,256).toString(16)}`;
  }
}
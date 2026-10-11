import {DeviceType} from "./types";
import { z } from 'zod';


const environmentSchema = z.object({
  WSS_PORT: z.coerce.number().default(1234),
  MQTT_URL: z.string().default('mqtt://localhost:1883'),
  REST_PORT: z.coerce.number().default(1234),
  TEMPERATURE_MIN: z.coerce.number().default(18.0),
  TEMPERATURE_MAX: z.coerce.number().default(25.0),
  EVENTS_BASE_URL: z.string().default('events'),
  GPT_BASE_URL: z.string().default("http://localhost:11434/v1")
});

export const Environment = environmentSchema.parse(process.env);

export namespace Simple {

  function random(min: number, max: number) {
    return Math.floor(Math.random() * (max - min)) + min;
  }

  export function id(): string {
    return crypto.randomUUID()
  }

  export function name(type: DeviceType = "room"): string {
    return `${type}_${random(0,65536).toString(16)}_${random(0,65536).toString(16)}`;
  }
}
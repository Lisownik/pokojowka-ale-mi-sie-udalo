import {Device, DeviceData, DeviceState, DeviceType} from "./types";

const roomData: DeviceState = {
  temperature: 0,
  humidity: 0,
  pressure: 0,
  quality: 0
}

const bulbData: DeviceState = {
  power: "false",
  temperature: 1234,
  brightness: 99,
}

const windowData: DeviceState = {
  open: "false",
}

const heaterData: DeviceState = {
  power: "false",
  temperature: 11,
  temperatureGoal: 22
}

export function enforceData(device: Device, type: DeviceType): Device {
  switch (type) {
    case "room":
      device.state = {
        ...roomData,
        ...device.state,
      }
      break;
    case "smart_led":
      device.state = {
        ...bulbData,
        ...device.state,
      }
      break;
    case "window":
      device.state = {
        ...windowData,
        ...device.state,
      }
      break;
    case "heater":
      device.state = {
        ...heaterData,
        ...device.state,

      }
      break;
    default:
      break;
  }
  return device;
}
import { NameManager } from "./nameManager";
import {anyID, Device, DeviceState, DeviceType, everyID, ShortDevice} from "./types";

export class DeviceManager {
  private devices: Map<string, Device> = new Map();
  private defaultDevices: ShortDevice[] = [
    {
      id: anyID,
      type: "mobile"
    },
    {
      id: everyID,
      type: "window"
    },
    {
      id: everyID,
      type: "smart_led"
    },
    {
      id: everyID,
      type: "heater"
    },
    {
      id: "window-1",
      type: "window",
    }
  ]


  constructor(public nameManager: NameManager) {
  }


  shortenDevices(): ShortDevice[] {
    const newDevices: ShortDevice[] = [];
    this.devices.forEach(device => {
      newDevices.push(device);
    })

    return newDevices;
  }

  savedDevices(): ShortDevice[] {
    return this.nameManager.getAll().map(([key, value]) => ({
      id: key.toString(),
      type: value.type.toString(),
      }) as ShortDevice

    )
  }

  getShortDevices(): ShortDevice[] {
    return [
      ...this.defaultDevices,
      ...this.savedDevices(),
      ...this.shortenDevices()
    ]
  }

  deviceOnline(
    id: string,
    type: DeviceType,
    state: DeviceState = {}): Device {
    const existing = this.devices.get(id);

    if (!this.nameManager.has(id)){
      this.nameManager.set(id, `template-name-${type}`, type)
    }

    const newDevice: Device = {
      id,
      type,
      online: true,
      lastSeen: Date.now(),
      state: {
        ...existing?.state,
        ...state
      },
      name: this.nameManager.get(id),
    }

    this.devices.set(id, newDevice);

    return newDevice;
  }

  changeName(id: string, name: string): void {
    const existing = this.devices.get(id);

    if (!existing)
      return

    existing.name = name;
  }

  deviceOffline(id: string): Device | undefined {
    const device = this.devices.get(id);

    if(!device)
      return undefined;

    device.online = false;
    device.lastSeen = Date.now();

    return device;
  }

  updateState(id: string, state: DeviceState, type: DeviceType): Device {
    let device = this.devices.get(id);

    if (!device) {
      device = this.deviceOnline(id, type, state)
      return device;
    }


    device.state = {
      ...device.state,
      ...state
    }

    return device;
  }

  toArray(): Device[] {
    return Array.from(this.devices.values())
  }

  get(id: string): Device | undefined {
    return this.devices.get(id);
  }

  getById(id: string): Device | undefined {
    return this.devices.get(id);
  }

  getAllType(type: DeviceType): Device[] {
    return this.toArray().filter(device => device.type === type);
  }

  getAll(): Device[] {
    return this.toArray()
  }

  getOnline() {
    return this.toArray()
      .filter((device: Device) => device.online)
  }

  getOnlineType(type: DeviceType): Device[] {
    return this.getAllType(type)
      .filter((device: Device) => device.online)
  }
}
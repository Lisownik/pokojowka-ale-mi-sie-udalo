import {Device, DeviceType, EventType, NotificationType} from "./types";
import {Environment} from "./environment";
import {ActionManager} from "./actionManager";
import {MqttClient} from "mqtt";
import {publishNotification} from "./notifications";


export const Events: Record<DeviceType, EventType[]> = {
  "room": [
    "hot",
    "cold"
  ],
  "window": [],
  "yeelight": [],
  "smart_led": [],
  "mobile": [
    "go-out",
    "go-in",
    "bad-air"
  ],
  "heater": [],
  "roof": [
    "rain",
    "rain-stop",
    "sunny",
    "dark"
  ]
}

const MAX_EVENTS = {
  MAX_HEAT: 50.0,
  MAX_COLD: 10.0
}

export class EventManager {

  private client: MqttClient | null = null;

  constructor(public actionManager: ActionManager) {
  }

  public emit(id: string, event: EventType){
    console.log(`[${id}] ${event}`);

    if(this.client instanceof MqttClient)
      this.actionManager.tryEmit(id, event, "test").then();
  }



  private checkRoom(device: Device): void {
    if (!device.state.temperature)
      return
    const temp = device.state.temperature;

    if (temp > Environment.TEMPERATURE_MAX){
      this.emit(device.id, "hot");
    }

    if (temp > MAX_EVENTS.MAX_HEAT){
      // publishNotification(NotificationType.ERROR, "TEMPERATURA SKOLIM", `W pokoju ${device.name} wykryto zbyt dużą temperaturę dla człowieka`);
    }

    if (temp < Environment.TEMPERATURE_MIN){
      this.emit(device.id, "cold");
    }

    if (temp < MAX_EVENTS.MAX_COLD){
      // publishNotification(NotificationType.ERROR, "ZIMNO W CHUJ", `W pokoju ${device.name} wykryto zbyt niską temperaturę dla człowieka`)
    }

  }

  checkForEvents(device: Device, client: MqttClient) {
    if(!this.client)
      this.client = client;
    // console.group("CHECKING EVENTS FOR: ", device.id);
    switch (device.type) {
      case "room":
        this.checkRoom(device);
        break;
      case "mobile":
        console.warn("Checking mobile - this shouldn't happen");
        break;
      default:
        // console.log("No events found for " + device.type);
    }
    // console.groupEnd();
  }

}
import {Device, DeviceType, EventType} from "./types";
import {Environment} from "./environment";
import { ActionManager } from "./actionManager";
import {MqttClient} from "mqtt";


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
  "heater": []
}

export class EventManager {

  private client: MqttClient | null = null;

  constructor(public actionManager: ActionManager) {
  }

  public emit(id: string, event: EventType){
    console.log(`[${id}] ${event}`);

    if(this.client instanceof MqttClient)
      this.actionManager.emit(id, event, "test").then();
  }



  private checkRoom(device: Device): void {
    if (!device.state.temperature)
      return
    const temp = device.state.temperature;
    //Check for hot
    if (temp > Environment.TEMPERATURE_MAX){
      this.emit(device.id, "hot");
    }

    if (temp < Environment.TEMPERATURE_MIN){
      this.emit(device.id, "cold");
    }
  }

  checkForEvents(device: Device, client: MqttClient) {
    if(!this.client)
      this.client = client;
    console.group("CHECKING EVENTS FOR: ", device.id);
    switch (device.type) {
      case "room":
        this.checkRoom(device);
        break;
      case "mobile":
        console.warn("Checking mobile - this shouldn't happen");
        break;
      default:
        console.log("No events found for " + device.type);
    }
    console.groupEnd();
  }

}
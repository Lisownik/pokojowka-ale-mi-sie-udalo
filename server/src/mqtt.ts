import mqtt, {MqttClient} from 'mqtt';
import {DeviceManager} from "./deviceManager";
import {DeviceType} from "./types";
import {Environment} from "./environment";
import { NameManager } from './nameManager';
import { EventManager } from './eventManager';



export function startMqtt(deviceManager: DeviceManager, nameManager: NameManager, eventManager: EventManager) {
  const client = mqtt.connect(Environment.MQTT_URL);

  client.on("connect", () => {
    console.log("Connected to MQTT BROKER");

    client.subscribe("devices/+/+/status")
    client.subscribe("devices/+/+/state")
  })

  client.on('message', (topic, payload) => {
    try{
      const message = JSON.parse(payload.toString());

      const parts = topic.toString().split("/");

      if(parts.length !== 4) {
        return;
      }

      const [_, type, deviceId, messageType] = parts;


      if (messageType === "status") {
        handleStatus(
          deviceManager,
          deviceId,
          type as DeviceType,
          message
        )
      }

      if (messageType === "state"){
        handleState(
          deviceManager,
          deviceId,
          type as DeviceType,
          message,
          eventManager,
          client
        )
      }
    } catch (error) {
      console.error("Invalid MQTT message:", error);
    }
  })
  return client;
}

function handleStatus(
  deviceManager: DeviceManager,
  deviceId: string,
  type: DeviceType,
  message: {online: boolean},
) {
  if(message.online) {
    deviceManager.deviceOnline(deviceId, type);

    console.log(`${deviceId} is Online`);

    return;
  }

  const device = deviceManager.deviceOffline(deviceId);

  if(device){
    console.log(`${deviceId} Device Offline`);
  }
}

function handleState(
  deviceManager: DeviceManager,
  deviceId: string,
  type: DeviceType,
  state: Record<string, any>,
  eventManager: EventManager,
  client: MqttClient

) {
  const device = deviceManager.updateState(
    deviceId,
    state,
    type
  );

  eventManager.checkForEvents(device, client)

  if(!device){
    console.log(`
      Received state for unknown device ${deviceId}
      `);
    return;
  }


  console.log(`Updated ${deviceId}`, device.state)
}
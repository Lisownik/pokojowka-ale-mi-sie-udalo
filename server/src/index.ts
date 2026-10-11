import {DeviceManager} from "./deviceManager";
import {startMqtt} from "./mqtt";
import {createRouter} from "./router";
import {Environment} from "./environment";
import {NameManager} from "./nameManager";
import { EventManager } from "./eventManager";
import {ActionManager} from "./actionManager";


const nameManager = new NameManager();
nameManager.load().then(() => console.log("NameManager loaded name file"));

const actionManager = new ActionManager();

const eventManager = new EventManager(actionManager);



const deviceManager = new DeviceManager(nameManager);

const mqttClient = startMqtt(deviceManager, nameManager, eventManager);
actionManager.setClient(mqttClient);
console.log("MQTT Client set for action manager")


const expressApp = createRouter(deviceManager, nameManager, actionManager);


expressApp.listen(Environment.REST_PORT, () => {
  console.log(`Listening on port ${Environment.REST_PORT} yo`);
  setTimeout(async () => {
    await actionManager.emit("room-1", "cold", {})
  }, 5000)
})

console.log("Server started");
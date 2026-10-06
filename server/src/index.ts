import {DeviceManager} from "./deviceManager";
import {startMqtt} from "./mqtt";
import {createRouter} from "./router";
import {Environment} from "./environment";
import {NameManager} from "./nameManager";


const nameManager = new NameManager();
nameManager.load().then(r => console.log("NameManager loaded name file"));

const deviceManager = new DeviceManager(nameManager);

const mqttClient = startMqtt(deviceManager, nameManager);

const expressApp = createRouter(deviceManager, nameManager);


expressApp.listen(Environment.REST_PORT | 3333, () => {
  console.log(`Listening on port ${Environment.REST_PORT}`);
})

console.log("Server started");
import {DeviceManager} from "../deviceManager";
import {Router} from "express";
import {ActionManager} from "../actionManager";
import {enforceData} from "../data";

export function bulbsRoutes(deviceManager: DeviceManager, actionManager: ActionManager) {
  const router: Router = Router();

  router.get('/', (req, res) => {
    const bulbs = [
      ...deviceManager
        .getOnlineType("yeelight"),
      ...deviceManager
        .getOnlineType("smart_led")
    ].map(device => enforceData(device, "smart_led"))

    res.status(200).json(bulbs);
  })

  router.get("/raw", (req, res) => {
    const bulbs = [
      ...deviceManager
        .getAllType("yeelight"),
      ...deviceManager
        .getAllType("smart_led")
    ].map(device => enforceData(device, "smart_led"))

    res.status(200).json(bulbs);
  })

  router.put("/:id/state/:state", async (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400).send("Id not set properly");
      return
    }

    const state_string: string = req.params.state;
    const state: boolean = state_string == "true"
    console.log(`spoko nowy stan żarówki ${id} na ${state} brightness`);

    if(!deviceManager.has(id))
      return res.status(200).send("Device not found");

    deviceManager.get(id)!.state.power = state_string

    await actionManager.publishAction(id, state ? "on" : "off", {})

    res.status(200).send({state});
  })

  router.put("/:id/brightness/:bright", async (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400).send("Id not set properly");
      return
    }

    const brightness_string: string = req.params.bright;
    const brightness: number = parseInt(brightness_string, 10);
    if(!brightness || isNaN(brightness)) {
      res.status(400).send("brightness should be a number");
      return
    }

    if(!deviceManager.has(id))
      return res.status(200).send("Device not found");

    deviceManager.get(id)!.state.brightness = brightness;

    console.log(`spoko nowa jasność żarówki ${id} na ${brightness} brightness`);

    await actionManager.publishAction(id, "brightness", {brightness});

    res.status(200).send({brightness: brightness});
  })

  return router
}
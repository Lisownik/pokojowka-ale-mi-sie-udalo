import { Router } from "express";
import { DeviceManager } from "../deviceManager";
import {ActionManager} from "../actionManager";
import { enforceData } from "../data";

export function heatersRoutes(deviceManager: DeviceManager, actionManager: ActionManager) {
  const router: Router = Router();

  router.get('/', (req, res) => {
    const heaters = deviceManager
      .getOnlineType("heater")
      .map(device => enforceData(device, "heater"))

    res.status(200)
      .json(heaters);
  })

  router.get("/raw", (req, res) => {
    const heaters = deviceManager
      .getAllType("heater")
      .map(device => enforceData(device, "heater"))

    res.status(200)
      .json(heaters);
  })

  router.put("/:id/temperature_goal/:temp", async (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400)
        .send("Id not set properly");
      return
    }

    const temperature_string = req.params.temp
    const temperature = parseFloat(temperature_string);
    if(!temperature || isNaN(temperature)){
      res.status(400)
        .send("temperature should be a number")
      return
    }

    if(!deviceManager.has(id))
      return res.status(200)
        .send("Device not found")

    deviceManager.get(id)!.state.temperatureGoal = temperature;

    await actionManager.publishAction(id, "temperature", {temperatureGoal: temperature});

    res.status(200)
      .send({
        temperature_goal: temperature,
      })
  })

  router.put("/:id/state/:state", async (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400).send("Id not set properly");
      return
    }

    const state_string: string = req.params.state;
    const state: boolean = state_string == "true"
    console.log(`spoko nowy stan heatera ${id} na ${state}`);

    if(!deviceManager.has(id))
      return res.status(200).send("Heater device not found");

    deviceManager.get(id)!.state.power = state_string

    await actionManager.publishAction(id, state ? "on" : "off", {})

    res.status(200).send({state});
  })

  return router;
}
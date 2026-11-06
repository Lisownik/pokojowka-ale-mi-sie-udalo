import {DeviceManager} from "../deviceManager";
import {Router} from "express";
import {ActionType} from "../types";
import {ActionManager} from "../actionManager";
import { enforceData } from "../data";

export function windowsRoutes(deviceManager: DeviceManager, actionManager: ActionManager) {
  const router: Router = Router()

  router.get("/", (req, res) => {
    const okna = deviceManager
      .getOnlineType("window")
      .map(device => enforceData(device, "window"))

    res.status(200)
      .json(okna)
  })

  router.put("/:id/state/:state", async (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400).send("Id not set properly");
      return
    }

    const state: boolean = req.params.state == "true";

    if(!deviceManager.has(id)){
      await actionManager.publishAction(id, "open", {"open": state});
      return res.status(200).send("Device not found");
    }

    deviceManager.get(id)!.state.open = state;

    console.log(`spoko ale okno ${id} zmieniono na ${state}`)

    const action: ActionType = state ? "open" : "close"
    await actionManager.publishAction(id, action, {"open": state});


    res.status(200).send({state});
  })

  router.get("/raw", (req, res) => {
    const okna = deviceManager
      .getAllType("window")
      .map(device => enforceData(device, "window"))

    res.status(200).json(okna);
  })

  return router
}
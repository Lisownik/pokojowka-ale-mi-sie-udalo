import {DeviceManager} from "../deviceManager";
import {Router} from "express";
import {isWindowState, WindowState} from "../types";
import {ActionManager} from "../actionManager";
import {publish} from "./streaming.routes";

export function windowsRoutes(deviceManager: DeviceManager, actionManager: ActionManager) {
  const router: Router = Router()

  router.get("/", (req, res) => {
    const okna = deviceManager
      .getOnlineType("window")

    res.status(200)
      .json(okna)
  })

  router.put("/:id/state/:state", async (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400).send("Id not set properly");
      return
    }

    const state_string: string = req.params.state;
    const state: WindowState = state_string as WindowState;
    if(!isWindowState(state)){
      res.status(400).send("state not set properly");
      return
    }

    const isOpened: string = state === "open" ? "true" : "false";

    if(!deviceManager.has(id)){
      await actionManager.publishAction(id, "open", {"open": isOpened});
      return res.status(200).send("Device not found");
    }

    deviceManager.get(id)!.state.open = isOpened;

    console.log(`spoko ale okno ${id} zmieniono na ${state}`)

    await actionManager.publishAction(id, state, {"open": isOpened});


    res.status(200).send({state});
  })

  router.get("/raw", (req, res) => {
    const okna = deviceManager
      .getAllType("window")

    res.status(200).json(okna);
  })

  return router
}
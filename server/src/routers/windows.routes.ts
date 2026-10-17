import {DeviceManager} from "../deviceManager";
import {Router} from "express";
import {WindowState} from "../types";

export function windowsRoutes(deviceManager: DeviceManager) {
  const router: Router = Router()

  router.get("/", (req, res) => {
    const okna = deviceManager
      .getOnlineType("window")

    res.status(200)
      .json(okna)
  })

  router.put("/:id/state/:state", (req, res) => {
    const id = req.params.id;
    if(!id){
      res.status(400).send("Id not set properly");
      return
    }

    const state_string: string = req.params.state;
    const state: WindowState = state_string as WindowState;
    if(!state){
      res.status(400).send("state not set properly");
      return
    }

    console.log(`spoko ale okno ${id} zmieniono na ${state}`)

    res.status(200).send({state});
  })

  router.get("/raw", (req, res) => {
    const okna = deviceManager
      .getAllType("window")

    res.status(200).json(okna);
  })

  return router
}
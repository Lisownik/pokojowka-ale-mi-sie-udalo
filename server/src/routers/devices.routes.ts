import { DeviceManager } from "../deviceManager";
import {NameManager} from "../nameManager";
import {Router} from "express";

export function devicesRoutes(deviceManager: DeviceManager, nameManager: NameManager) {
  const router: Router = Router()

  router.get("/online", (req, res) => {
    const rooms = deviceManager
      .getOnline()

    res.status(200)
      .json(rooms)
  })

  router.get("/ids", (req, res) => {
    const devices = deviceManager
      .getAll()

    res.status(200)
      .json(devices)
  })

  router.put("/:id/:name", async (req, res) => {
    const id = req.params.id;
    const name = req.params.name;


    deviceManager.changeName(id, name);

    const type = deviceManager.get(id)?.type
    if(!type)
    {
      res.status(400)
        .send(`Bad identifier ${id} not found`)
      return
    }

    await nameManager.set(id, name, type)

    res.status(200)
      .json({
        msg: "success"
      })
  })

  router.get("/module-info", (req, res) => {
    res.status(200)
      .send(
        deviceManager.getShortDevices()
      )
  })

  return router;
}
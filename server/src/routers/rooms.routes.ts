import {DeviceManager} from "../deviceManager";
import {Router} from "express";
import {enforceData} from "../data";

export function roomsRoutes(deviceManager: DeviceManager) {
  const router: Router = Router();

  router.get("/", (req, res) => {
    const rooms = deviceManager
      .getOnlineType("room")
      .map(device => enforceData(device, "room"))

    res.status(200)
      .json(rooms)
  })

  router.get("/:id", (req, res) => {
    const id = req.params.id
    res.status(200)
      .json(
        deviceManager.getById(id)
      )
  })

  router.get("/raw", (req, res) => {
    const rooms = deviceManager
      .getAllType("room")
      .map(device => enforceData(device, "room"))

    res.status(200)
      .json(rooms)
  })

  return router;
}
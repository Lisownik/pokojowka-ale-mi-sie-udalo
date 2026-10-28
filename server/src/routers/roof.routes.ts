import {DeviceManager} from "../deviceManager";
import {Router} from "express";

export function roofRoutes(deviceManager: DeviceManager): Router {
  const router: Router = Router();

  router.get("/", (req, res) => {
    const roof = deviceManager
      .getOnlineType("roof")

    res.status(200)
      .json(roof[0] || {})
  })

  router.get("/:id", (req, res) => {
    const id = req.params.id

    res.status(200)
      .json(
        deviceManager.getById(id)
      )
  })

  return router
}
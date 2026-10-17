import {Router} from "express";
import {DeviceType, isDeviceType} from "../types";
import {Simple} from "../environment";

export function registerRoutes() {
  const router: Router = Router();

  router.post("/:type", async (req, res) => {
    const type = req.params.type;

    if(!isDeviceType(type))
    {
      res.status(400).send("bad type");
      return
    }

    res.status(200).send(Simple.name(type as DeviceType))
  })

  return router;
}
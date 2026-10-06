import express, { Express } from "express";
import cors from "cors";
import { DeviceManager } from "./deviceManager";
import {NameManager} from "./nameManager";

export function createRouter(deviceManager: DeviceManager, nameManager: NameManager): Express {
  const app = express()
    .use(cors())
    .use(express.json());

  app.get("/", (req, res) => {
    res.status(200).json({
      msg: "cześć świecie, witaj to ja verity"
    });
  });

  app.get("/devices/room", (req, res) => {
    const rooms = deviceManager
      .getOnlineType("room")

    res.status(200).json(rooms);
  });

  app.get("/devices/room/raw", (req, res) => {
    const rooms = deviceManager
      .getAllType("room")

    res.status(200).json(rooms);
  });

  app.get("/devices/room/:id", (req, res) => {
    const id = req.params.id;
    res.status(200).json(deviceManager.getById(id))
  })

  app.get("/devices/online", (req, res) => {
    const rooms = deviceManager
      .getOnline()

    res.status(200).json(rooms);
  });

  app.get("/devices/ids", (req, res) => {
    const devices = deviceManager.getAll();

    res.status(200).json(devices.map((device) => device.id));
  })

  app.put("/devices/room/:id/:name", async (req, res) => {
    const id = req.params.id;
    const name = req.params.name;

    deviceManager.changeName(id, name);

    await nameManager.set(id, name)

    res.status(200).json({
      msg: "success"
    })
  })

  app.use((req, res) => {
    res.status(404).json({
      error: "Not Found!"
    });
  });

  return app;
}

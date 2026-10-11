import express, { Express } from "express";
import cors from "cors";
import { DeviceManager } from "./deviceManager";
import {NameManager} from "./nameManager";
import {ActionManager} from "./actionManager";
import {anyID, DeviceType, EventActionConnections, isDeviceType} from "./types";
import {AiClient} from "./aiClient";
import { Simple } from "./environment";

export function  createRouter(deviceManager: DeviceManager, nameManager: NameManager, actionManager: ActionManager, aiClient: AiClient): Express {
  const app = express()
    .use(cors())
    .use(express.json());

  app.get("/", (req, res) => {
    res.status(200).send("cześć świecie, witaj to ja verity");
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

  app.get("/devices/window", (req, res) => {
    const okna = deviceManager
      .getOnlineType("window")

    res.status(200).json(okna);
  })

  app.get("/devices/window/raw", (req, res) => {
    const okna = deviceManager
      .getAllType("window")

    res.status(200).json(okna);
  })

  app.get("/devices/bulb", (req, res) => {
    const bulbs = [
      ...deviceManager
        .getOnlineType("yeelight"),
      ...deviceManager
        .getOnlineType("smart_led")
    ]

    res.status(200).json(bulbs);
  })

  app.get("/devices/bulb/raw", (req, res) => {
    const bulbs = [
      ...deviceManager
        .getAllType("yeelight"),
      ...deviceManager
        .getAllType("smart_led")
    ]

    res.status(200).json(bulbs);
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

  app.put("/devices/:id/:name", async (req, res) => {
    const id = req.params.id;
    const name = req.params.name;

    deviceManager.changeName(id, name);

    await nameManager.set(id, name)

    res.status(200).json({
      msg: "success"
    })
  })

  app.get("/names", (req, res) => {
    res.status(200).json(nameManager.getAll());
  })

  app.post("/event/program", async (req, res) => {
    await actionManager.emit(anyID, "programmed", {})
    res.status(200).json({
      msg: "success",
      defaultID: anyID
    })
  })

  app.post("/connections", async (req, res) => {
    try {
      const raw = req.body.connections;
      if (typeof raw !== "string") {
        return res.status(400).send("connections must be a JSON string");
      }

      let parsed: unknown;
      try {
        parsed = JSON.parse(raw);
      } catch {
        return res.status(400).send("connections is not valid JSON");
      }

      const result: EventActionConnections = parsed as EventActionConnections
      if (!result) {
        return res.status(400).json({ error: "idk" });
      }

      await actionManager.set(result);
      res.status(200).send("good");
    } catch (err) {
      console.error("Failed to set connections:", err);
      res.status(500).send("error");
    }

  })

  app.get("/module-info", (req, res) => {
    res.status(200).send(deviceManager.getShortDevices())
  })

  app.get("/action/all", (req, res) => {
    res.status(200).json(actionManager.ETAConnections);
  })

  app.get("/gpt/:msg", async (req, res) => {
    const msg = req.params.msg;
    const gptRes = await aiClient.messageWithDevices(msg);

    if(!gptRes) {
      res.status(400).send("bad gpt request")
      return
    }

    res.status(200).json(gptRes);
  })

  app.post("/register/:type", async (req, res) => {
    const type = req.params.type;

    if(!isDeviceType(type))
    {
      res.status(400).send("bad type");
      return
    }

    res.status(200).send(Simple.name(type as DeviceType))
  })

  app.use((req, res) => {
    res.status(404).json({
      error: "Nie znaleziono cwelu jebany"
    });
  });

  return app;
}

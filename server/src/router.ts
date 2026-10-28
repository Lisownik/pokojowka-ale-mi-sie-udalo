import express, { Express } from "express";
import cors from "cors";
import { DeviceManager } from "./deviceManager";
import {NameManager} from "./nameManager";
import {ActionManager} from "./actionManager";
import {AiClient} from "./aiClient";
import {requestLogger} from "./logger";
import {devicesRoutes} from "./routers/devices.routes";
import {roomsRoutes} from "./routers/rooms.routes";
import { windowsRoutes } from "./routers/windows.routes";
import { bulbsRoutes } from "./routers/bulbs.routes";
import {namesRoutes} from "./routers/names.routes";
import {eventsRoutes} from "./routers/events.routes";
import {registerRoutes} from "./routers/register.routes";
import {gptRoutes} from "./routers/gpt.routes";
import {rootRoutes} from "./routers/root.routes";
import {streamingRoutes} from "./routers/streaming.routes";
import {heatersRoutes} from "./routers/heaters.routes";
import {EventManager} from "./eventManager";
import {roofRoutes} from "./routers/roof.routes";

export function  createRouter(deviceManager: DeviceManager, nameManager: NameManager, actionManager: ActionManager, aiClient: AiClient, eventManager: EventManager): Express {
  const app = express()
    .use(cors())
    .use(express.json())
    .use(requestLogger)

  app.use("/", rootRoutes())
  app.use("/devices", devicesRoutes(deviceManager, nameManager))
  app.use("/devices/room", roomsRoutes(deviceManager))
  app.use("/devices/window", windowsRoutes(deviceManager, actionManager))
  app.use("/devices/bulb", bulbsRoutes(deviceManager, actionManager))
  app.use("/devices/heater", heatersRoutes(deviceManager, actionManager))
  app.use("/devices/roof", roofRoutes(deviceManager))
  app.use("/names", namesRoutes(nameManager))
  app.use("/", eventsRoutes(actionManager))
  app.use("/register", registerRoutes())
  app.use("/gpt", gptRoutes(aiClient, actionManager))
  app.use("/stream", streamingRoutes(deviceManager, eventManager))

  app.use((req, res) => {
    res.status(404).json({
      error: "Nie znaleziono cwelu jebany"
    });
  });

  return app;
}

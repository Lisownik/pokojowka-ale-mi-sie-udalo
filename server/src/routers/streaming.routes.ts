import { DeviceManager } from "../deviceManager";
import {DeviceData, DeviceType} from "../types";
import express, {Request, Response, Router} from "express";




class Topic<T> {
  private clients = new Set<Response>();
  constructor(protected path: String) {}

  private send(res: Response, snapshot: unknown) {
    res.write(`data: ${JSON.stringify(snapshot)}\n\n`);
  }

  subscribe(req: Request, res: Response) {
    console.log(`Subscribed to ${this.path} stream`)
    res.set({
      "Content-Type": "text/event-stream",
      "Cache-Control": "no-cache, no-transform",
      Connection: "keep-alive",
      "X-Accel-Buffering": "no",
    });
    res.flushHeaders();

    this.clients.add(res);

    req.on("close", () => {
      this.clients.delete(res);
      res.end()
      console.log(`Ended subscription to ${this.path} stream`)
    });
  }

  publish(snapshot: unknown) {
    this.clients.forEach((res) => this.send(res, snapshot));
  }
}

export function publish(deviceManager: DeviceManager, type: DeviceType){
  switch(type){
    case "room":
      roomsTopic.publish(
        deviceManager.getOnlineType(type)
      );
      break;
    case "smart_led":
      bulbsTopic.publish(
        deviceManager.getOnlineType(type)
      )
      break;
    case "window":
      windowsTopic.publish(
        deviceManager.getOnlineType(type)
      )
      break;
    case "heater":
      heatersTopic.publish(
        deviceManager.getOnlineType(type)
      )
      break;
    default:
      break;
  }
}

export const bulbsTopic = new Topic("/stream/bulbs")
export const roomsTopic = new Topic("/stream/rooms")
export const windowsTopic = new Topic("/stream/windows")
export const heatersTopic = new Topic("/stream/heaters")

export function streamingRoutes(deviceManager: DeviceManager) {
  const router: Router = express.Router();

  router.get("/rooms", (req, res) => {
    roomsTopic.subscribe(req, res)
    publish(deviceManager, "room")
  })

  router.get("/bulbs", (req, res) => {
    bulbsTopic.subscribe(req, res)
    publish(deviceManager, "smart_led")
  })

  router.get("/windows", (req, res) => {
    windowsTopic.subscribe(req, res)
    publish(deviceManager, "window")
  })

  router.get("/heaters", (req, res) => {
    heatersTopic.subscribe(req, res)
    publish(deviceManager, "heater")
  })

  return router;
}
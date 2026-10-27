import { DeviceManager } from "../deviceManager";
import {anyID, DeviceData, DeviceType} from "../types";
import express, {Request, Response, Router} from "express";
import {EventManager} from "../eventManager";
import {clearInterval} from "node:timers";




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
      'Transfer-Encoding': 'chunked',
      Connection: "keep-alive",
      "X-Accel-Buffering": "no",
    });
    res.flushHeaders();

    req.socket.setKeepAlive(true, 1_000);
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

export function streamingRoutes(deviceManager: DeviceManager, eventManager: EventManager) {
  const router: Router = express.Router();

  const connectedClients = new Set<Response>();
  router.get("/activity", (req, res) => {
    console.log(`Subscribed to activity stream`)
    res.set({
      "Content-Type": "text/event-stream",
      "Cache-Control": "no-cache, no-transform",
      'Transfer-Encoding': 'chunked',
      Connection: "keep-alive",
      "X-Accel-Buffering": "no",
    });
    res.flushHeaders();

    if(connectedClients.size === 0){
      eventManager.emit(anyID, "go-in")
    }

    connectedClients.add(res);

    req.socket.setKeepAlive(true, 1_000);

    req.on("close", () => {
      connectedClients.delete(res);
      console.log(`Ended subscription to activity stream`)
      res.end()


      if(connectedClients.size === 0){
        eventManager.emit(anyID, "go-out")
      }
    });
  })

  router.get("/activity/clients", (req, res) => {
    console.log(connectedClients.size)
    res.status(200).json(
      connectedClients.size
    )
  })

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
import { ActionManager } from "../actionManager";
import {Router} from "express";
import {anyID, EventActionConnections} from "../types";

export function eventsRoutes(actionManager: ActionManager) {
  const router: Router = Router();

  router.post("/event/program", async (req, res) => {
    await actionManager.tryEmit(anyID, "programmed", {})
    res.status(200).json({
      msg: "success",
      defaultID: anyID
    })
  })

  router.get("/action/all", async (req, res) => {
    res.status(200)
      .json(
        actionManager.ETAConnections
      );
  })

  router.post("/connections", async (req, res) => {
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

  return router;
}
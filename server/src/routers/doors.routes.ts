import { Router } from "express";
import {ActionType, everyID} from "../types";
import {ActionManager} from "../actionManager";
import {EventManager} from "../eventManager";

export function doorsRoutes(actionManager: ActionManager): Router{
  const router: Router = Router();

  router.put("/state/:state", async (req, res) => {
    const state: boolean = req.params.state == "true";

    console.log(`spoko ale drzwi frontowe zmieniono na ${state}`)

    const action: ActionType = state ? "open" : "close"
    await actionManager.publishAction(
      ActionManager.everyIDForModule("door"),
      action,
      {"open": state}
    );

    res.status(200).send({state});
  })

  return router
}
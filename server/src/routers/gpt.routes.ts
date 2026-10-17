import { AiClient } from "../aiClient";
import {Router} from "express";

export function gptRoutes(aiClient: AiClient) {
  const router: Router = Router();

  router.get("/:msg", async (req, res) => {
    const msg = req.params.msg;
    const gptRes = await aiClient.messageWithDevices(msg);

    if(!gptRes) {
      res.status(400).send("bad gpt request")
      return
    }

    res.status(200).json(gptRes);
  })

  return router;
}
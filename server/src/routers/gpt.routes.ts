import { AiClient } from "../aiClient";
import {Router} from "express";
import {everyID} from "../types";
import {ActionManager} from "../actionManager";

function isAIRequest(findable_message: string, ai_response: string): boolean {
  return ai_response.toLowerCase().includes(findable_message.toLowerCase());
}

export function gptRoutes(aiClient: AiClient, actionManager: ActionManager) {
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

  router.get("/commands/:msg", async (req, res) => {
    const msg = req.params.msg;
    const gptRes = await aiClient.messageWithDevices(msg);

    if(!gptRes) {
      res.status(400).send("bad gpt request")
      return
    }

    let resultText = gptRes

    if(isAIRequest(`events/smart_led-${everyID}/off`, gptRes)){
      await actionManager.publishAction(ActionManager.everyIDForModule('smart_led'), "off", {})
      resultText = "Pomyślnie wyłączono wszystkie żarówki!"
    }

    if(isAIRequest(`events/smart_led-${everyID}/on`, gptRes)){
      await actionManager.publishAction(ActionManager.everyIDForModule('smart_led'), "on", {})
      resultText = "Pomyślnie włączono wszystkie żarówki!"
    }


    if(isAIRequest(`events/window-${everyID}/close`, gptRes)){
      await actionManager.publishAction(ActionManager.everyIDForModule('window'), "open", {})
      resultText = "Pomyślnie wyłączono wszystkie żarówki!"
    }

    if(isAIRequest(`events/window-${everyID}/open`, gptRes)){
      await actionManager.publishAction(ActionManager.everyIDForModule('smart_led'), "close", {})
      resultText = "Pomyślnie włączono wszystkie żarówki!"
    }

    res.status(200).json(resultText);
  })

  return router;
}
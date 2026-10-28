import {OpenAI} from "openai";
import {Environment} from "./environment";
import { DeviceManager } from "./deviceManager";
import {everyID} from "./types";

export class AiClient {
  client = new OpenAI({
    // baseURL: Environment.GPT_BASE_URL,
    apiKey: Environment.GPT_API_KEY
  })

  model = "gpt-5.5"

  constructor(public deviceManager: DeviceManager) {}

  private async useChatCompletion(systemTopic: string, userTopic: string): Promise<string | null>{
    console.log("useChatCompletion", systemTopic);
    const res = await this.client.chat.completions.create({
      model: this.model,
      messages: [
        {
          role: "system", content: systemTopic,
        },
        {
          role: "user", content: userTopic,
        }
      ]
    })

    return res.choices[0].message.content
  }


  public async messageWithDevices(message: string): Promise<string | null>{
    return this.useChatCompletion(
      `<BARDZO WAŻNE>[JEŻELI W PYTANIU BĘDZIE PROŚBA O: WYŁĄCZENIE ŚWIATEŁ DODAJ DO WYPOWIEDZI: "events/smart_led-${everyID}/off"; WŁĄCZENIE ŚWIATEŁ DODAJ DO WYPOWIEDZI: "events/smart_led-${everyID}/on"];`+
      `[Rozmawiaj w języku polskim] (To są informacje o urządzeniach w tym systemie, jeżeli ktoś o nie będzie pytał to użyj tych danych: ${JSON.stringify(this.deviceManager.toArray())})`,
      message
    )
  }
}


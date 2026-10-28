import {MqttClient} from "mqtt";
import {Environment} from "./environment";
import {
  ActionSlave,
  ActionType,
  anyID,
  DeviceType,
  EventActionConnections,
  EventToAction,
  EventType,
  everyID
} from "./types";
import {readFile, writeFile} from "node:fs/promises";


export class ActionManager {

  ETAConnections: EventActionConnections = []

  static_event_url = Environment.EVENTS_BASE_URL;

  private client: MqttClient | null = null;

  private filepath: string = "./data/ETA.json";

  async load(): Promise<void> {
    try{
      const data = await readFile(this.filepath, "utf8");
      const parsed: unknown = JSON.parse(data);
      if(!Array.isArray(parsed)){
        throw new Error("ETA.json must contain an array")
      }

      const newETA = parsed as EventActionConnections;
      newETA.forEach((ETA: EventToAction) => {
        if(ETA.id === "" || ETA.id === "<any>")
          ETA.id = anyID
        if(ETA.slave.id === "" || ETA.slave.id === "<any>")
          ETA.slave.id = anyID
      })

      this.ETAConnections = newETA;

    } catch (error: unknown) {
      if(error instanceof Error && "code" in error && error.code === "ENOENT"){
        this.ETAConnections = []
        //save
        return
      }

      throw error;
    }
  }

  private async save(): Promise<void> {
    await writeFile(
      this.filepath,
      JSON.stringify(this.ETAConnections, null, 4),
      "utf8"
    )
  }

  async set(connections: EventActionConnections): Promise<void> {
    connections.forEach((ETA: EventToAction) => {
      if(ETA.id === "" || ETA.id === "<any>")
        ETA.id = anyID
      if(ETA.slave.id === "" || ETA.slave.id === "<any>")
        ETA.slave.id = anyID
    })

    this.ETAConnections = connections;
    await this.save();

    console.log("New connections saved.")
  }

  constructor() {
    this.load().then(() => {
      console.log("Loaded ETA connections...")
    })
  }

  static everyIDForModule(moduleType: DeviceType): string{
    return `${moduleType}-${everyID}`
  }

  private getAction(id: string, event: EventType): ActionSlave | null {
    const ETA = this.ETAConnections.find(
      (e) =>
        ((e.id === id) || (e.id === anyID) || (e.id === everyID))
        &&
        e.event === event
    );
    if (!ETA) {
      console.log(`Action for id: ${id} and event: ${event} not found :(`)
      return null;
    }
    console.log(`Action found: ${ETA.slave.id}, event: ${ETA.slave.action}; for event: ${event} and id: ${id}`);

    if(ETA.slave.id === everyID){
      ETA.slave.id = ActionManager.everyIDForModule(ETA.slave.module_type)
    }

    return ETA.slave;
  }


  public setClient(client: MqttClient): void {
    this.client = client;
  }

  private eventUrl(id: string, action: EventType | ActionType): string {
    return `${this.static_event_url}/${id}/${action}`;
  }

  public async emit<T>(id: string, event: EventType, payload: T) {
    console.group("Emitting event");
    console.log("Received event: ", id, event, payload);
    const action = this.getAction(id, event);
    if (action)
      await this.publishEvent<T>(this.eventUrl(action.id, action.action), payload);
    console.groupEnd();
  }

  public async publishAction<T>(id: string, action: ActionType, payload: T) {
    console.group("Emitting action");
    console.log("Received action: ", id, action);
    await this.publishEvent<T>(this.eventUrl(id, action), payload);
    console.groupEnd();
  }

   private async publishEvent<T>(
    topic: string,
    payload: T
  ) {
    if (!this.client){
      console.log("Client not set");
      return
    }
    console.log(`Publishing event ${topic}`);
    await this.client.publishAsync(topic, JSON.stringify(payload), { qos: 1});
  }
}
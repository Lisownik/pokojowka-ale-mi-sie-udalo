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
import {parseAction} from "./notifications";


/**
 * Manager integracji E2A
 *
 * Zarządza integracjami E2A, przechowując je w pliku według konfiguracji
 * Poprzez metodę tryEmit/publishEvent, może wysyłać event poprzez serwer mqtt
 *
 * @example
 * const actionManager = new ActionManager();
 * actionManager.tryEmit(id, event, "test")
 *
 */
export class ActionManager {

  /**
   * Aktualny stan integracji E2A wczytany z pliku lub wprowadzony przez metodę set
   * @defaultValue []
   */
  ETAConnections: EventActionConnections = []

  /**
   * Pierwsza cześć url wywołanego eventu
   *   np. /events/<id>/<eventType>
   *   dla tego pola równego "events"
   * @defaultValue Environment.EVENTS_BASE_URL
   */
  static_event_url = Environment.EVENTS_BASE_URL;


  /**
   * Lazy Loaded MqttClient
   * `null` dopóki {@link ActionManager.setClient} będzie wywołane.
   * @internal
   */
  private client: MqttClient | null = null;

  /**
   * Ścieżka do pliku gdzie są przechowywane dane E2A
   * @defaultValue ./data/ETA.json
   * @internal
   */
  private filepath: string = "./data/ETA.json";

  /**
   * Wczytuje dane z pliku i interpretuje je do pola ETAConnections
   *
   * Wczytuje dane z pliku {@link ActionManager.filepath},
   * jeżeli poprawnie przejdzie parsing danych,
   * zostaną one zapisane w tablicy {@link ActionManager.ETAConnections}
   *
   * @returns {Promise<EventActionConnections>} Zinterpretowane dane z wczytanego pliku
   *
   * @throws {Error} Jeżeli to nie jest zwykły error
   *
   * @example
   * const actionManager = new ActionManager();
   * await actionManager.load();
   *
   * @remarks
   * Jest to ciężka metoda, wczytuje plik i wykonuje na tych danych kilka zmian
   *
   * @async
   */
  async load(): Promise<EventActionConnections> {
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
      return this.ETAConnections;
    } catch (error: unknown) {
      if(error instanceof Error && "code" in error && error.code === "ENOENT"){
        this.ETAConnections = []
        return this.ETAConnections
      }

      throw error;
    }
  }

  /**
   * Zapisuje stan danych E2A do pliku
   *
   * Tablice {@link ActionManager.ETAConnections} zapisuje do pliku {@link ActionManager.filepath}
   *
   * @returns {Promise<void>}
   *
   * @example
   * const actionManager = new ActionManager();
   * await actionManager.set([]);
   * await actionManager.save();
   *
   * @async
   */
  private async save(): Promise<void> {
    await writeFile(
      this.filepath,
      JSON.stringify(this.ETAConnections, null, 4),
      "utf8"
    )
  }

  /**
   * Zapisuje stan danych E2A do pliku
   *
   * Tablice {@link ActionManager.ETAConnections} zapisuje do pliku {@link ActionManager.filepath}
   *
   * @returns {Promise<void>}
   *
   * @example
   * const actionManager = new ActionManager();
   * await actionManager.set([]);
   * await actionManager.save();
   *
   * @async
   */
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

  /**
   * @throws {Error} Kiedy metoda load() zwróci error
   */
  constructor() {
    this.load().then(() => {
      console.log("Loaded ETA connections...")
    })
  }

  /**
   * Zwr
   *
   * @param {DeviceType} moduleType - What it represents, format, constraints (e.g. UUID v4).
   *
   * @returns {string} Description of the resolved value (shape, when null/empty is returned).
   *
   * @example
   * const id = ActionManager.everyIDForModule("room");
   *
   * @static
   */
  static everyIDForModule(moduleType: DeviceType): string{
    return `${moduleType}-${everyID}`
  }

  /**
   * Dla podanego eventu znajduje przypisaną akcję
   *
   * @param {string} id - id urządzenia, które ma powiązane z sobą E2A
   * @param {EventType} event - typ eventu, dla którego ma być znaleziona akcja
   *
   * @returns {ActionSlave | null} Zwraca akcje dla podanego eventu
   *
   * @example
   * const actionManager = new ActionManager();
   * const action = actionManager.getAction("room-1", "hot");
   */
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

  /**
   * Ustawia aktualny obiekt MqttClient, którego będzie używał ActionManager
   *
   * @param {MqttClient} client - mqttClient z którego będzie korzystał ActionManager
   *
   * @example
   * const actionManager = new ActionManager();
   * actionManager.setClient(mqttClient);
   *
   *
   * @remarks
   * Powinno używać się go raz dla całego lifetime'u obiektu
   * @see {ActionManager.client}
   * @see {startMqtt}
   *
   */
  public setClient(client: MqttClient): void {
    this.client = client;
  }

  /**
   * Tworzy url dla podanego id oraz eventu/akcji
   *
   * @param {string} id - Urządzenia dla którego ma być stworzony event/akcja
   * @param {EventType | ActionType} action - Typ eventu/akcji dla jakiego ma być stworzony url
   *
   * @returns {string} Ścieżka eventu uzupełniona danymi
   *
   * @example
   * const actionManager = new ActionManager();
   * const eventUrl = actionManager.eventUrl("win-10", "open");
   * // /events/win-10/open
   *
   * @see {ActionManager.static_event_url}
   */
  private eventUrl(id: string, action: EventType | ActionType): string {
    return `${this.static_event_url}/${id}/${action}`;
  }

  /**
   * Dla podanego eventu, próbuje wywołać akcję poprzez metodę publishEvent
   *
   * @typeParam T - Jaki payload będzie przesłany przez serwer MQTT np. JSON, string, number...
   *
   * @param {string} id - id urządzenia dla którego ma być znaleziona przypisana akcja
   * @param {EventType} event - typ eventu dla którego ma być znaleziona przypisana akcja
   * @param {T} payload - dodatkowe dane przesyłane wraz z akcją
   *
   * @returns {Promise<void>}
   *
   * @example
   * const actionManager = new ActionManager();
   * this.actionManager.tryEmit(id, event, "test").then();
   *
   *
   * @see {ActionManager.getAction}
   * @see {ActionManager.publishEvent}
   * @see {parseAction}
   */
  public async tryEmit<T>(id: string, event: EventType, payload: T): Promise<void> {
    console.group("Emitting event");
    console.log("Received event: ", id, event, payload);
    const action = this.getAction(id, event);

    parseAction(id, event, action);

    if (action)
      await this.publishEvent<T>(this.eventUrl(action.id, action.action), payload);
    console.groupEnd();
  }

  /**
   * Nakładka na metodę publishEvent, aby bezpiecznie wywoływać akcje przez serwer mqtt
   *
   * @typeParam T - Jaki payload będzie przesłany przez serwer MQTT np. JSON, string, number...
   *
   * @param {string} id - id urządzenia dla którego ma być wywołana przypisana akcja
   * @param {ActionType} action - typ akcji jaki ma być wywołany na urządzeniu
   * @param {T} payload - dodatkowe dane przesyłane wraz z akcją
   *
   * @example
   * const actionManager = new ActionManager();
   * await actionManger.publishAction("win-10", "open", {})
   *
   * @see {ActionManager.publishEvent}
   * @see {ActionManager.tryEmit}
   *
   */
  public async publishAction<T>(id: string, action: ActionType, payload: T): Promise<void> {
    console.group("Emitting action");
    console.log("Received action: ", id, action);
    await this.publishEvent<T>(this.eventUrl(id, action), payload);
    console.groupEnd();
  }

  /**
   * Wysyła akcję przez serwer mqtt
   *
   * @typeParam T - Jaki payload będzie przesłany przez serwer MQTT np. JSON, string, number...
   *
   * @param {string} topic - Pełny url wywołanej akcji np. "/events/led-1/on"
   * @param {T} payload - dodatkowe dane przesyłane wraz z akcją
   *
   * @example
   * await this.publishEvent<T>(this.eventUrl("led-1", "brightness"), {brightness: 80});
   *
   * @see {ActionManager.client}
   * @see {ActionManager.publishAction}
   */
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
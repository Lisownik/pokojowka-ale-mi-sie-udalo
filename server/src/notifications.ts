import {ActionSlave, EventType, Notification, NotificationType} from "./types";
import {notificationTopic} from "./routers/streaming.routes";

export const notifications: Notification[] = [];

function* counter() {
  let i = 0;
  while (true) {
    yield i++;
  }
}

const gen = counter();
export const getId = () => gen.next().value ?? notifications.length;

export function publishNotification(notifType: NotificationType, title: String, text: String, id: number = getId()) {
  let notification = {
    id,
    type: notifType,
    title: title,
    description: text
  } as Notification;

  notificationTopic.publish(
    notification
  );

  notifications.push(
    notification
  );
}

export function parseAction(id: string, actionType: EventType, action: ActionSlave | null): void {
  // MOŻE
  // if(actionType === "go-in"){
  //   setTimeout(() => {
  //     publishNotification(NotificationType.INFO, "Witaj Użytkowniku", ``);
  //   }, 3000)
  //   return
  // }

  if (!action) {
    publishNotification(NotificationType.SILENT, "Brak Automatycznego Zdarzenia", `Zauważono event: ${actionType} dla urządzenia o id: ${id}`);
    return
  }

  publishNotification(NotificationType.SILENT, "Automatyczne Zdarzenie", `Dla urządzenia ${id} oraz eventu: ${actionType} wykonano działanie: ${action.action} dla urządzenia ${action.module_type} i id ${action.id}`);
}
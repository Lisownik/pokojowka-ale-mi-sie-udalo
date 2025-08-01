import { WebSocketServer, WebSocket } from 'ws';
import {
    ActionType,
    FiltersStructure,
    IncomingResponsePayload,
    Parameters,
    ResponsePayload,
    ServerData,
    UserData, userInfo
} from "./types";
import crypto from 'crypto';
import { YeelightService } from 'yeelight-service';
import {
    IYeelight,
    IYeelightDevice,
    IYeelightMethodResponse, YeelightEffect,
    YeelightMethodStatusEnum
} from 'yeelight-service/lib/yeelight.interface';

export function standardizeData(action: ActionType, params: ResponsePayload): string {
    return JSON.stringify(
        {
            action,
            params
        } as ServerData
    );
}

export async function sendError(
    ws: WebSocket,
    code: number,
    message: string,
    details?: any
): Promise<void> {
    await sendResponse(ws, 'error', { code, message, details })
}

export async function sendResponse(ws: WebSocket, variable: string, value: any): Promise<void> {
    try {
        ws.send(standardizeData('response', { variable, value } as ResponsePayload))
        console.log(`Sending response completed successfully: `, { variable, value }, standardizeData('response', { variable, value } as ResponsePayload));
    }
    catch (error) {
        await sendError(ws, 300, `Sending response error`, { variable, value, error });
        console.error(`Sending response error: `, { variable, value, error });
        throw error;
    }
}

export async function sendRequest(ws: WebSocket, method: string, params: Parameters): Promise<void> {
    try {
        ws.send(JSON.stringify({
                action: 'request',
                method,
                params
            }
        ))
    }
    catch (error) {
        await sendError(ws, 300, `Sending request error`, { error });
        console.error(`Sending request error: `, { error });
        throw error;
    }
}

export function parseResponse(ws: WebSocket, userId: string, payload: IncomingResponsePayload) {
    console.log("New response incoming")
    console.table(payload);
    Users.set(userId, {
        name: Users.get(userId)!.name,
        params: payload.value,
        type: payload.type,
    })
}


export const wss: WebSocketServer = new WebSocketServer({
    port: parseInt(process.env.PORT ?? '8080'),
    perMessageDeflate: {
        zlibDeflateOptions: {
            chunkSize: 1024,
            level: 3,
            memLevel: 7,
        },
        zlibInflateOptions: { chunkSize: 10240 },
        clientNoContextTakeover: true,
        serverNoContextTakeover: true,
        serverMaxWindowBits: 10,
        concurrencyLimit: 10,
        threshold: 1024,
    },
});

export function simpleId(): string {
    return crypto.randomBytes(16).toString('hex');
}

export function simpleName(): string {
    let pass = "room_" + crypto.randomBytes(2).toString('hex');
    if(!Array.from(Users.values()).find((user: UserData)=> user.name === pass))
        return pass;
    else
        return simpleName();
}

const yeelightService: IYeelight = new YeelightService();
yeelightService.devices.subscribe((devices) => {
    devices.forEach((device) => {
        const deviceName = device.name.value;
        const deviceConnected = device.connected.value;

        if (!deviceConnected) {
            return;
        }
        const bulbId = simpleId();

        Users.set(bulbId, {
            name: deviceName,
            type: 'lightbulb',
            params: {
                power: device.power.value,
                brightness: device.brightness.value
            }
        })
        IdToYeelight.set(bulbId, device)

        console.log(bulbId, {
            name: deviceName,
            type: 'lightbulb',
            params: {
                power: device.power.value,
                brightness: device.brightness.value
            }
        })

        let bulbData = Users.get(bulbId)!;

        console.log(deviceName)

        device.brightness.asObservable().subscribe((brightness) => {
            console.log("Brightness: ", brightness)
            bulbData.params['brightness'] = brightness
        })
        device.power.asObservable().subscribe((powerState) => {
            console.log("Power: ", powerState)
            bulbData.params['power'] = powerState
        })
        device.name.asObservable().subscribe((newName) => {
            console.log("Name: ", newName)
            bulbData.name = newName
        })

        device.setPower('on')
    });
});

export const Users = new Map<string, UserData>();
export const IdToWS = new Map<string, WebSocket>();
export const IdToYeelight = new Map<string, IYeelightDevice>();

export class RoomService {
    static info(): userInfo[] {
        let arr = new Array<{id: string, name: string, data: Parameters}>()
        Array.from(Users.entries()).forEach(([key, value]) => {
            if (value.type === 'room') {
                arr.push(
                    {
                        id: key,
                        name: value.name,
                        data: value.params,
                    })
            }
        })
        return arr;
    }
}

export const filters: FiltersStructure = {
    "room": {
        "get": (): { keys: any[], values: any[] } => {
            let keys = Array.from(Users.entries())
            let objects: { keys: any[], values: any[] } = { keys: [], values: [] }
            keys.forEach(([key, value]) => {
                if (value.type === 'room') {
                    objects.keys.push(key)
                    objects.values.push(Users.get(key))
                }
            })
            return objects
        },
        "info": (): {id: string, name: string, data: Parameters}[] => {
            let arr = new Array<{id: string, name: string, data: Parameters}>()
            let keys = Array.from(Users.entries())
            keys.forEach(([key, value]) => {
                if (value.type === 'room') {
                    arr.push(
                        {
                            id: key,
                            name: value.name,
                            data: value.params,
                        }
                    )
                }
            })
            return arr;
        },
        "name-change": (id: string, name: string): number => {
            let ws: WebSocket | undefined = IdToWS.get(id);
            if(!ws || !name) return 300;
            let data = Users.get(id);
            data!.name = name;
            sendRequest(ws, 'name.change', {'name': name})
            return 200;
        },
        "name-start": (id: string, params: Parameters): void => {
            let data = Users.get(id);
            console.log('fucking nigger?')
            if(!data || !params['name']) return;
            data.name = params['name'];
        }
    },
    "bulb": {
        "get": () => {
            let arr = new Array<{id: string, name: string, data: Parameters}>()
            let keys = Array.from(Users.entries())
            keys.forEach(([key, value]) => {
                if (value.type === 'lightbulb') {
                    arr.push(
                        {
                            id: key,
                            name: value.name,
                            data: value.params,
                        }
                    )
                }
            })
            console.log(arr)
            return arr;
        },
        "brightness-change": (id: string, brightness: number, duration?: number): boolean => {
            let bulb = IdToYeelight.get(id);
            if(!bulb)
                return false
            console.log("try to bright: ", brightness)
            bulb.setName("niga?")
            bulb.setBrightness(Number(brightness))
            return true
        }
    }
}
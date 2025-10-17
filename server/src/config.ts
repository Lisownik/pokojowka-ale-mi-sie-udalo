import { WebSocketServer, WebSocket } from 'ws';
import {
    ActionType,
    FiltersStructure,
    IncomingResponsePayload,
    Parameters, PowerState,
    ResponsePayload,
    ServerData,
    ModuleData, userInfo
} from "./types";
import crypto from 'crypto';
import { YeelightService } from 'yeelight-service';
import {
    IYeelight,
    IYeelightDevice
} from 'yeelight-service/lib/yeelight.interface';
import OpenAI from 'openai';
import {getPotki, modifyPotka} from './file-managment'
import {clearTimeout} from "node:timers";

export const GPTClient = new OpenAI({apiKey: process.env.API_KEY});


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
    Modules.set(userId, {
        name: Modules.get(userId)!.name,
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
    return crypto.randomBytes(8).toString('hex');
}

export function simpleName(type: string = "room"): string {
    let pass = `${type}_` + crypto.randomBytes(2).toString('hex');
    if(!Array.from(Modules.values()).find((user: ModuleData)=> user.name === pass))
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

        Modules.set(bulbId, {
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

        let bulbData = Modules.get(bulbId)!;

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
        device.setName("żarówka")
    });
});


export const Modules = new Map<string, ModuleData>();
export const IdToWS = new Map<string, WebSocket>();
export const IdToYeelight = new Map<string, IYeelightDevice>();

export class PotManagment {
    static info(): userInfo[] {
        let arr = new Array<{id: string, name: string, data: Parameters}>()
        Array.from(Modules.entries()).forEach(([key, value]) => {
            if (value.type === 'pot') {
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
    }
    static read(id: string): userInfo | undefined {
        const find = Array.from(Modules.entries()).find(([key, value]) => value.type === 'pot' && key === id)
        if(!find)
            return undefined;
        return {
            id: find[0],
            name: find[1].name,
            data: find[1].params,
        } as userInfo
    }
    static register() {
        let id = simpleId();
        modifyPotka(id, simpleName("pot"))
        return id
    }
    static async changeName(id: string, name: string) {
        console.group(`Changing name of ${id}`)
        console.log(`New Name: ${name}`)
        await modifyPotka(id, name)
        const potka = Modules.get(id);
        console.log("Potka", potka?.name);
        if(!potka)
        {
            console.log("Potka data not found :/")
        } else
            potka.name = name;
        console.groupEnd()
    }
    static async updateData(id: string, data: Parameters) {
        console.group("Update data")
        const timeout = setTimeout(() => {
            PotManagment.deletePot(id)
        }, 10 * 1000)
        const potka = Modules.get(id);
        console.log("Data: ", data)
        if(!potka)
        {
            const potki = await getPotki()
            console.log(`First data request of id: ${id}, and name: ${potki[id] || "none"}`)
            Modules.set(id, {name: potki[id] || simpleName("pot"), params: data, type: "pot", timeoutIt: timeout})
        } else {
            console.log(`Data modify request of id: ${id}, and name: ${potka.name}`)
            clearTimeout(potka.timeoutIt)
            Modules.set(id, {
                name: potka!.name,
                params: data,
                type: "pot",
                timeoutIt: timeout
            })
        }
        console.groupEnd()

    }
    static deletePot(id: string) {
        Modules.delete(id)
    }
}

export const filters: FiltersStructure = {
    "room": {
        "get": (): { keys: any[], values: any[] } => {
            let keys = Array.from(Modules.entries())
            let objects: { keys: any[], values: any[] } = { keys: [], values: [] }
            keys.forEach(([key, value]) => {
                if (value.type === 'room') {
                    objects.keys.push(key)
                    objects.values.push(Modules.get(key))
                }
            })
            return objects
        },
        "info": (): {id: string, name: string, data: Parameters}[] => {
            let arr = new Array<{id: string, name: string, data: Parameters}>()
            let keys = Array.from(Modules.entries())
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
            let data = Modules.get(id);
            data!.name = name;
            sendRequest(ws, 'name.change', {'name': name})
            return 200;
        },
        "name-start": (id: string, params: Parameters): void => {
            let data = Modules.get(id);
            if(!data || !params['name']) return;
            data.name = params['name'];
        }
    },
    "bulb": {
        "get": () => {
            let arr = new Array<{id: string, name: string, data: Parameters}>()
            let keys = Array.from(Modules.entries())
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
            return arr;
        },
        "brightness-change": (id: string, brightness: number, duration?: number): boolean => {
            let bulb = IdToYeelight.get(id);
            if(!bulb)
                return false
            bulb.setBrightness(Number(brightness))
            return true
        },
        "power-change": (id: string, power: PowerState): boolean => {
            let bulb = IdToYeelight.get(id);
            if(!bulb)
                return false
            bulb.setPower(power)
            return true
        },
        "name-change": (id: string, newName: string): boolean => {
            let bulb = IdToYeelight.get(id);
            if(!bulb)
                return false
            bulb.setName(newName)
            return true
        }
    }
}
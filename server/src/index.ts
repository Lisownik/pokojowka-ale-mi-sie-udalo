import 'dotenv/config';
import {
    wss,
    sendResponse,
    sendError,
    Modules,
    simpleId,
    parseResponse,
    filters,
    simpleName,
    IdToWS
} from "./config";
import { WebSocket } from "ws";
import {IncomingResponsePayload, RequestPayload, ServerData} from "./types";
import { app } from "./endpoints";

wss.on('listening', () => {
    console.log(`Listening on port ${process.env.PORT || 8080}`);
})

wss.on('connection', async (ws: WebSocket) => {
    wss.clients.forEach((client: WebSocket) => {
        sendResponse(client, 'list', Array.from(Modules.keys()))
    })

    const UserId = simpleId();
    Modules.set(UserId, { params: {} , name: simpleName()})
    IdToWS.set(UserId, ws);

    await sendResponse(ws, 'message', `Successfully connected! ${UserId}, ${Modules.get(UserId)!.name}`);

    ws.on('error', async (err) => {
        await sendError(ws, 0x1, 'WebSocket Spotted an Error', err);
        console.error('Websocket Spotted an Error: ', err);
    })

    ws.on('message', async (data: Buffer) => {
        try {
            const message: ServerData = JSON.parse(data.toString());
            console.table(message)
            if (message.action === 'request') {
                const payload = message.params as RequestPayload;
                console.log(payload.method)

                const [category, key] = payload.method.split('.'); // -> ["room", "get"]
                const func = filters[category][key];
                if(!func)
                    return;

                await sendResponse(ws, payload.responseVar ?? "temp", func(UserId, payload.params));
            }
            if (message.action === 'response')
                parseResponse(ws, UserId, message.params as IncomingResponsePayload)
        } catch (error) {
            await sendError(ws, 0x2, 'Message Formatting Error:', error);
            console.error(error);
        }
    })

    ws.on('close', () => {
        console.log('Websocket closed');
        Modules.delete(UserId);
    })
})

app.listen(process.env.endpointPort ?? 3333, () => {
    console.log(`Listening on (express) port ${process.env.endpointPort ?? 3333}`);
})
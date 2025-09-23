import { WebSocket } from 'ws'
export type dataAgentType = 'room' | 'pot' | 'lightbulb';
export type ActionType = 'request' | 'response';
export type Parameters = { [key: string]: any };

export type userInfo = {id: string, name: string, data: Parameters};

export interface ServerData {
    action: ActionType;
    params: RequestPayload | IncomingResponsePayload | ResponsePayload;
}

export interface ResponsePayload {
    variable: string;
    value: any;
}

export interface IncomingResponsePayload {
    method: string;
    type?: dataAgentType;
    value: Parameters;
}

export interface RequestPayload {
    method: string;
    params: Parameters;
    responseVar?: string;
}

export interface UserData {
    name: string;
    type?: dataAgentType;
    params: Parameters;
    // websocket: WebSocket;
}

export interface QueryCategory {
    [key: string]: Function;
}

export interface FiltersStructure {
    [category: string]: QueryCategory;
}

export type PowerState = "on" | "off";
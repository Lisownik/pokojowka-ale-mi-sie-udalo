import express, { Request, Response } from 'express';
import {filters, Users} from "./config";

export const app = express();

app.use(express.json());

app.get('/', (_req, res) => {
    res.send('Hello, connected to rooms endpoint!');
})

app.get('/room-list', (_req: Request, res: Response) => {
    res.json(Array.from(Users.keys()));
})


app.get('/room-values', (_req: Request, res: Response) => {
    console.table(filters["room"]["get"]())
    res.header("Access-Control-Allow-Origin", "*");
    res.json(filters["room"]["get"]())
})

app.get('/rooms', (_req: Request, res: Response) => {
    console.log("A")
    console.table(filters["room"]["get"]())
    res.header("Access-Control-Allow-Origin", "*");
    res.json(filters["room"]["info"]())
})

app.put('/room/:id/name', (req: Request, res: Response) => {
    try {
        const { id } = req.params;
        const { newName } = req.query;

        if(!assert_requirement(res, [id, newName], 'ID and newName are required'))
            return

        if (!filters["room"]["name-change"](id, newName)) {
            error_response(res, 404, 'Room not found or update failed')
            return
        }

        send_response(res, 200, {
            message: 'Room name updated successfully',
            data: { id, newName }
        })
    } catch (error) {
        console.error('Error updating room name:', error);
        error_response(res, 500, 'Internal server error')
    }
});

app.get('/bulbs', (req: Request, res: Response) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.json(filters["bulb"]["get"]())
})

app.put('/bulb/:id/name', (req: Request, res: Response) => {
    try {
        const { id } = req.params;
        const { newName } = req.body;

        if(!assert_requirement(res, [id, newName], 'ID and newName are required'))
            return

        if (!filters["bulb"]["name-change"](id, newName)) {
            error_response(res, 404, 'Bulb not found or update failed')
            return
        }

        send_response(res, 200, {
            message: 'Bulb name updated successfully',
            data: { id, newName }
        })
    } catch (error) {
        console.error('Error updating bulb name:', error);
        error_response(res, 500, 'Internal server error')
    }
})

app.put('/bulb/:id/brightness', (req: Request, res: Response)=> {
    try {
        const { id } = req.params;
        const { brightness, duration } = req.query;

        if(!assert_requirement(res, [id, brightness], 'ID and brightness are required'))
            return

        if (!filters["bulb"]["brightness-change"](id, brightness, duration)) {
            error_response(res, 404, 'Bulb not found or update failed')
            return
        }

        send_response(res, 200, {
            message: 'Bulb brightness updated successfully',
            data: { id, brightness, duration}
        })
    } catch (error) {
        console.error('Error updating bulb brightness:', error);
        error_response(res, 500, 'Internal server error')
    }
})

function assert_requirement(res: Response, params: any[], errorMessage: string = "Parameters requiremenets were not met"): boolean {
    params.forEach((param) => {
        if(!param) {
            res.status(400).json({
                params,
                errorMessage,
            })
            return false
        }
    })
    return true
}

function error_response(res: Response, code: number, errorMessage: string) {
    res.status(code).json({errorMessage})
}

function send_response(res: Response, code: number, json: Object) {
    res.status(code).json(json)
}
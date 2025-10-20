import express, {Request, Response} from 'express';
import {filters, Modules, PotManagment} from "./config";
import {ModuleData, PowerState} from "./types";
import cors from 'cors';
import {GPTClient} from "./config";
import {Parameters} from "./types";

export const app = express();


app.use(cors());

app.use(express.json());

app.get('/', (_req, res) => {
    res.send('Hello, connected to rooms endpoint!');
})

app.get('/room-list', (_req: Request, res: Response) => {
    res.json(Array.from(Modules.keys()));
})


app.get('/room-values', (_req: Request, res: Response) => {
    console.table(filters["room"]["get"]())
    res.json(filters["room"]["get"]())
})

app.get('/rooms', (_req: Request, res: Response) => {
    console.log("A")
    console.table(filters["room"]["get"]())
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

app.get('/room/avg', (req: Request, res: Response) => {
    let keys = Array.from(Modules.entries())
    let objects: { keys: string[], values: ModuleData[] } = { keys: [], values: [] }
    keys.forEach(([key, value]) => {
        if (value.type === 'room') {
            objects.keys.push(key)
            objects.values.push(value)
        }
    })
    const len = objects.values.length;
    if(len === 0) {
        send_response(res, 200, {
            message: 'Room avg is none',
            data: {
                "temperature": 0,
                "humidity": 0,
                "pressure": 0,
                "quality": 0,
            }
        })
        return
    }
    let data = {
        temperature: 0,
        humidity: 0,
        pressure: 0,
        quality: 0,
    }
    objects.values.forEach((value) => {
        data.temperature += value.params.temperature;
        data.humidity += value.params.humidity;
        data.pressure += value.params.pressure;
        data.quality += value.params.quality;
    })
    data.temperature /= len;
    data.humidity /= len;
    data.pressure /= len;
    data.quality /= len;
    send_response(res, 200, {
        message: "Room avg was calculated successfully!",
        data
    })
})


app.get('/bulbs', (req: Request, res: Response) => {
    res.json(filters["bulb"]["get"]())
})

app.put('/bulb/:id/name', (req: Request, res: Response) => {
    try {
        const { id } = req.params;
        const { newName } = req.query;

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
        error_response(res, 500, `Internal server error: ${error}`)
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
app.put('/bulb/:id/power', (req: Request, res: Response)=> {
    try {
        const { id } = req.params;
        const { power_state } = req.query;

        if(!assert_requirement(res, [id, power_state as PowerState], 'ID and power state are required'))
            return

        if (!filters["bulb"]["power-change"](id, power_state)) {
            error_response(res, 404, 'Bulb not found or update failed')
            return
        }

        send_response(res, 200, {
            message: 'Bulb power state updated successfully',
            data: { id, power_state}
        })
    } catch (error) {
        console.error('Error updating bulb power state:', error);
        error_response(res, 500, 'Internal server error')
    }
})

app.get('/gpt', async (req, res) => {
    const { message } = req.query;
    if(!message) return res.status(400).send({
        error: 'Invalid query',
    })

    try{
        const APIResponse = await GPTClient.responses.create({
            model: "gpt-5",
            input: message.toString()
        })

        console.log(`Input: ${message}, Output: ${APIResponse.output_text}`)

        return res.status(200).json({
            output: APIResponse.output_text,
            status: "successfully fetched an answer"
        })
    } catch (err) {
        res.status(500).send({
            error: 'Internal Server Error',
            errMessage: err,
        })
    }
})

app.get('/tts', async (req, res, next) => {
    const {message} = req.query;
    if (!message) return res.status(400).send({
        error: 'Invalid query',
    })
    try {
        console.log(`Input: ${message}`)
        console.log(`{Jeśli ten tekst/pytania będzie wspominało o warunkach w jakimś pokoju to są dane z pokojów jako JSON: ${JSON.stringify(filters["room"]["info"]())}} /* Krótko odpowiedz na: */` + message.toString())
        const text = (await GPTClient.responses.create({
            model: "gpt-4o-mini",
            input: `{Jeśli ten tekst/pytania będzie wspominało o warunkach w jakimś pokoju to są dane z pokojów jako JSON: ${JSON.stringify(filters["room"]["info"]())}} /* Krótko odpowiedz na: */` + message.toString()
        })).output_text;

        console.log(`Output: ${text}`);

        const mp3 = await GPTClient.audio.speech.create({
            model: "gpt-4o-mini-tts",
            voice: "shimmer",
            input: text,
        });

        const buffer = Buffer.from(await mp3.arrayBuffer());
        console.log(`Sending`)
        res
            .setHeader("Content-Type", "audio/mpeg")
            .send(buffer);
    } catch (err) {
        console.log(err)
        res.status(500).send({
            error: 'Internal Server Error',
            errMessage: err,
        })
    }
})

app.get('/pot', (_req, res) => {
    send_response(res, 200, PotManagment.info())
})

app.get('/pot/register', (_req, res) => {
  send_response(res, 200, PotManagment.register())
})

app.get('/pot/:id', (req, res) => {
    const { id } = req.params;

    if(!assert_requirement(res, [id], 'ID is required'))
        return

    try{
        send_response(res, 200, PotManagment.read(id) || {})
    } catch (e) {
        send_response(res, 500, {e})
    }
})

app.post('/pot/:id', async (req, res) => {
    try{
        const { id } = req.params;
        const data = req.body;

        if(!assert_requirement(res, [id, data], 'ID and data are required'))
            return
        await PotManagment.updateData(id, data as Parameters)
        send_response(res, 201, {msg: "Successfully updated data!"})
    } catch (e) {
        send_response(res, 500, {e})
        return
    }
})

app.delete('/pot/:id', (req, res) => {
    const { id } = req.params;

    if(!assert_requirement(res, [id], 'ID is required'))
        return

    try{
        PotManagment.deletePot(id)
        send_response(res, 201, {msg: "Successfully disconnected!"})
    } catch (e) {
        send_response(res, 500, {e})
        throw e
    }
})

app.put('/pot/:id/:name', async (req, res) => {
    const { id, name } = req.params;

    if(!assert_requirement(res, [id, name], 'ID and name are required'))
        return

    try{
        await PotManagment.changeName(id, name)
        send_response(res, 201, {msg: `Successfully changed name of pot: ${id}!`})
    } catch (e) {
        send_response(res, 500, {e})
        throw e
    }

})

app.use((req, res, next) => {
    res.status(404).json({
        error: 'Not Found!'
    })
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
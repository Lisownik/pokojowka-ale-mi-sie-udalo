import { YeelightService } from 'yeelight-service';
import {
    IYeelight
} from 'yeelight-service/lib/yeelight.interface';

const yeelightService: IYeelight = new YeelightService();
yeelightService.devices.subscribe((devices) => {
    devices.forEach((device) => {
        const deviceName = device.name.value;
        const deviceConnected = device.connected.value;

        if (!deviceConnected) {
            return;
        }
                console.log(deviceName)
        console.log(device)
        device.setName("Może teraz?")
        const min = 1, max = 100;

        device.setPower('on')

        setInterval(() => {
            device.setBrightness(rand(min, max))
        }, 1000)
        // do something with device `myDevice` knowing, that every other device is disconnected from socket
    });
});

function rand(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1) ) + min;
}
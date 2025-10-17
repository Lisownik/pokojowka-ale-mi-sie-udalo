import {
  Discover,
  IDevice,
  Yeelight,
} from 'yeelight-awesome';

const discover = new Discover(
// @ts-ignore
  { port: 1982, asPromise: true, debug: true },
);
discover.once('deviceAdded', (device: IDevice) => {
  console.log('found device: ', device);
  const yeelight = new Yeelight({
    lightIp: device.host,
    lightPort: device.port,
  });

  yeelight.on('connected', () => {
      yeelight.setName("NIGGA")
      yeelight.setBright(100)
      setTimeout(() => {
      yeelight.setBright(10)
      setTimeout(() => {
      yeelight.setBright(1)
      setTimeout(()=>{
      yeelight.setBright(100)
      }, 1000)
      }, 1000)
      }, 1000)
  });
  yeelight.connect();
});

discover.start();
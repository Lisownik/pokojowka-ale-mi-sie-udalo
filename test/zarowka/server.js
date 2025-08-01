const { Client } = require('yeelight-node')
const client = new Client()

client.bind(yeelight => {
    yeelight.set_power('on')
    yeelight.set_name("prosze-dizalaj").then(
        data => console.log(data)
    )
    yeelight.get_prop('bright').then(
        data => console.log(data)
    )
        yeelight.get_prop('name').then(
        data => console.log(data)
    )
})
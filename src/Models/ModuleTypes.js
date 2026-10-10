export const static_module_types = {
  room: {
    isIdNeeded: true,
    events: ["hot", "cold", "bad_air"],
    actions: []
  },
  window: {
    isIdNeeded: true,
    events: [],
    actions: ["open", "close"]
  },
  mobile: {
    isIdNeeded: false,
    events: ["go-in", "go-out", "programmed"],
    actions: ["notify-byob"]
  },
  heater: {
    isIdNeeded: true,
    events: ["start", "stop"],
    actions: ["start", "stop"]
  }

}

export const anyID = "<any>"


export const modules = [
  { id: "1234", type: "pokojowka" },
  { id: "GBASDE1", type: "pokojowka" },
  { id: "ABCD", type: "okno" },
  { id: "DCBA", type: "okno" },
  { id: "123asd", type: "okno" },
  { id: anyID, type: "mobile" },
]
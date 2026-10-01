export const static_module_types = {
  pokojowka: {
    isIdNeeded: true,
    events: ["zimno", "ciepło", "bad_air"],
    actions: []
  },
  okno: {
    isIdNeeded: true,
    events: [],
    actions: ["open", "close"]
  },
  mobile: {
    isIdNeeded: false,
    events: ["come-in", "come-out", "program-1"],
    actions: ["notify-byob"]
  },
  heater: {
    isIdNeeded: true,
    events: ["start", "stop"],
    actions: ["start", "stop"]
  }

}


export const modules = [
  { id: "1234", type: "pokojowka" },
  { id: "GBASDE1", type: "pokojowka" },
  { id: "ABCD", type: "okno" },
  { id: "DCBA", type: "okno" },
  { id: "123asd", type: "okno" },
  { id: "<any>", type: "mobile" },
]
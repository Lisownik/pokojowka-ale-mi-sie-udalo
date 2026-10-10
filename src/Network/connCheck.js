import {anyID, static_module_types} from "../Models/ModuleTypes";
import {sendConnections} from "./api";

function getNode(nodes, id){
  console.log(`Check for ${id} in `, nodes)
  return Array.from(nodes).find((node) =>
    node.id === id
  ).data
}

function validateId(type, id){
  console.log(`Check for ${id} in `, type)
  const neededId = static_module_types[type].isIdNeeded
  if(!neededId)
    return true
  return id !== ""
}

function validNode(node){

  const hasType = node.module_type !== "";
  const hasEoA =  node.eventOrAction !== ""
  const validId = validateId(node.module_type, node.id)

  console.log("validating node: ", hasType, hasEoA, validId, node)

  return hasType && hasEoA && validId
}

function validEdgeElements(source, target){
  console.log(`Check source :`, source, `Check target :`, target);
  return validNode(source) && validNode(target)
}

function generateConnection(source, target){
  return {
    module_type: source.module_type,
    id: source.id ?? anyID,
    event: source.eventOrAction,
    slave: {
      module_type: target.module_type,
      id: target.id ?? anyID,
      action: target.eventOrAction
    }
  }
}

export function getViableConnections(nodes, edges){
  if((!nodes || nodes.length <= 0 ) || (!edges || edges.length <= 0)){
    console.log("no nodes no edges")
    return;
  }

  console.log(nodes, edges)

  let connections = []
  edges.forEach((edge) => {
    console.group("Edging")
    console.log("edge: ", edge, " source: ", edge.source, " target: ", edge.target);
    const [node1, node2] = [
      getNode(nodes, edge.source),
      getNode(nodes, edge.target)
    ]
    console.log("EDGE: ", node1, node2)

    if(!validEdgeElements(node1, node2)){
      console.log("not valid nodes")
      return
    }
    console.log("valid nodes")

    const connection = generateConnection(node1, node2)
    console.log("Testing connection", connection)

    connections.push(connection)



    console.groupEnd()

  })

  sendConnections(connections).then(() => {
    console.log("Connection sent")
  })
}
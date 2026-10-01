import {static_module_types} from "../Models/ModuleTypes";

function getNode(nodes, id){
  console.log(`Check for ${id} in `, nodes)
  return Array.from(nodes).find((node) =>
    node.id === id
  ).data
}

function validateId(type, id){
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
  return validNode(source) && validNode(target)
}

function generateConnection(source, target){
  return {
    module_type: source.module_type,
    id: source.module_type,
    event: source.eventOrAction,
    slave: {
      module_type: target.module_type,
      id: target.id,
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
  edges.forEach((edge) => {
    console.group("Edging")
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

    console.groupEnd()

  })
}
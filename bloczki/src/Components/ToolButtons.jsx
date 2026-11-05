import { useReactFlow } from "@xyflow/react";
import { useModules } from "../Network/modules";
import {getViableConnections} from "../Network/connCheck";


function getId(){
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 10000);
  return `node_${timestamp}_${random}`;
}


function NodeButtons() {
  const { addNodes, getNodes, getEdges } = useReactFlow();
  const { refetch } = useModules();

  const addNode = (type) => {
    const offset = getNodes().length * 50;
    addNodes({
      id: getId(),
      type: type,
      position: { x: offset, y: 100 + offset },
      data: {
        label: `${type} Node`,
        module_type: "",
        id: "",
        eventOrAction: ""
      }
    });
  };



  return (
    <div className="NodeButtons">
      <button className="btn btn-success" onClick={() => addNode("input")}>+ Event</button>
      <button className="btn btn-success" onClick={() => addNode("output")}>+ Action</button>
      <button className="btn btn-danger" onClick={refetch}>Update</button>
      <button className="btn btn-info" onClick={() => getViableConnections(getNodes(), getEdges())}>Ha ha</button>
    </div>
  );
}

export default NodeButtons;
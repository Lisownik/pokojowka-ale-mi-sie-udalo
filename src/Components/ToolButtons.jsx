import { useReactFlow } from "@xyflow/react";
import { useModules } from "../Network/modules";
import {getViableConnections} from "../Network/connCheck";

function NodeButtons() {
  const { addNodes, getNodes, getEdges } = useReactFlow();
  const { refetch } = useModules();

  const addNode = (type) => {
    addNodes({
      id: crypto.randomUUID(),
      type,
      position: { x: 0, y: 100 },
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
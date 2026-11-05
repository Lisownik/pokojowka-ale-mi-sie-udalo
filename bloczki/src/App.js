import {useCallback, useEffect, useState} from 'react';
import {
  Background,
  ReactFlow,
  addEdge,
  Position,
  useNodesState,
  useEdgesState, ReactFlowProvider,
} from '@xyflow/react';
import './index.css';
import './App.css'
import CustomNode from './Nodes/CustomNode';
import InputNode from "./Nodes/InputNode";
import NodeButtons from "./Components/ToolButtons";
import OutputNode from "./Nodes/OutputNode";
import {getConnections} from "./Network/api";
import {useModules} from "./Network/modules";

const nodeTypes = {
  custom: CustomNode,
  input: InputNode,
  output: OutputNode,
};

function random(min, max) {
  return Math.floor(Math.random() * (max - min)) + min;
}

function toId(id, type,  event){
  return `${id}-${type}-${event}-${random(1,10)}`;
}

const CustomNodeFlow = () => {
  const [nodes, setNodes, onNodesChange] = useNodesState([
    {
      id: '1',
      type: 'input',
      data: { label: 'Node 1' },
      position: { x: 0, y: 250 },
    },
    {
      id: '2',
      type: 'output',
      data: {},
      position: { x: 500, y: 50 },
    },
    {
      id: '3',
      type: 'input',
      data: { label: 'Node 2' },
      position: { x: 0, y: -100 },
    },
  ]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useModules();

  useEffect(() => {
    const controller = new AbortController();

    async function loadFromServer() {
      try{
        const data = await getConnections(controller.signal)

        const arrData = Array.from(data);
        const newNodes = []
        const newEdges = []
        console.log("Data from server", arrData);
        arrData.forEach((node, index) => {
          const id1 = toId(node.id, node.module_type, node.event)
          const id2 = toId(node.slave.id, node.slave.module_type, node.slave.action)

          newNodes.push({
            id: id1,
            position: { x: 0, y: 250 + index*300, },
            type: "input",
            data: {
              label: 'Node',
              id: node.id,
              module_type: node.module_type,
              eventOrAction: node.event,
            }
          })

          newNodes.push({
            id: id2,
            position: { x: 500, y: 250 + index*300, },
            type: "output",
            data: {
              label: 'Node',
              id: node.slave.id,
              module_type: node.slave.module_type,
              eventOrAction: node.slave.action,
            }
          })



          newEdges.push({
            id: id1 + id2,
            source: id1,
            target: id2
          })
        })

        console.log(newNodes)
        console.log(newEdges)

        setNodes(newNodes)
        setEdges(newEdges)
      } catch (err) {
        if (err.name !== 'AbortError') setError(err.message);
      } finally {
        setLoading(false);
      }

    }

    loadFromServer();
    return () => controller.abort()
  }, [ setNodes, setEdges])

  const onConnect = useCallback(
    (params) => setEdges((eds) => addEdge(params, eds)),
    [setEdges],
  );


  if (loading) return <div>Loading…</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <ReactFlowProvider>
    <ReactFlow
      nodes={nodes}
      edges={edges}
      onNodesChange={onNodesChange}
      onEdgesChange={onEdgesChange}
      onConnect={onConnect}
      nodeTypes={nodeTypes}
      fitView
      colorMode="system"
    >
      <Background />
    </ReactFlow>
    <NodeButtons/>
    </ReactFlowProvider>
  );
};

export default CustomNodeFlow;

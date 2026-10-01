import { useCallback } from 'react';
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

const nodeTypes = {
  custom: CustomNode,
  input: InputNode,
  output: OutputNode,
};

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

  const onConnect = useCallback(
    (params) => setEdges((eds) => addEdge(params, eds)),
    [setEdges],
  );

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

import { Handle, useNodeConnections } from '@xyflow/react';

const LimitHandle = ({ type, position, connectioncount }) => {
  const connections = useNodeConnections({
    handleType: type,
  });

  return <Handle isConnectable={connections.length < connectioncount} position={position} type={type}/>;
};

export default LimitHandle;

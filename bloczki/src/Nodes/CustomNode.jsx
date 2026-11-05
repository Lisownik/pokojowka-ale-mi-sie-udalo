import { memo } from 'react';
import { Position } from '@xyflow/react';

import LimitHandle from '../Handles/LimitHandle';

const CustomNode = () => {
  return (
    <div>
      <LimitHandle type="target" position={Position.Left} connectioncount={1} />
      <div>{'← Only one edge allowed'}</div>
    </div>
  );
};

export default memo(CustomNode);

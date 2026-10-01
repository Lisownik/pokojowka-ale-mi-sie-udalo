import {useEffect, useState} from "react";
import {useModulesStore} from "../Models/modulesStorage";
import {static_module_types} from "../Models/ModuleTypes";
import LimitHandle from "../Handles/LimitHandle";
import {Position} from "@xyflow/react";

function OutputNode({data}) {
  const [module_type, setModule_type] = useState("")
  const [id, setId] = useState("")
  const [eventOrAction, setEventOrAction] = useState("")

  const modules = useModulesStore((s) => s.modules)
  const typeOptions = Object.keys(static_module_types).filter(
    (t) => static_module_types[t].actions.length > 0
  );

  const idOptions = modules.filter((m) => m.type === module_type)

  const EOAOptions = static_module_types[module_type]?.actions ?? [];

  useEffect(() => {
    data['module_type'] = module_type
    data['id'] = id
    data['eventOrAction'] = eventOrAction
  }, [data, eventOrAction, id, module_type]);

  const onTypeChange = (e) => {
    setModule_type(e.target.value);
    setId("");
    setEventOrAction("");
  };

  useEffect(() => {
    if (id && !modules.some((m) => m.id === id && m.type === module_type)) {
      setId("");
    }
  }, [modules, id, module_type]);

  return (
    <div className="OutputNode">
      <LimitHandle type="target" position={Position.Left} connectioncount={1} />
      <p className="h2">ACTION</p>
      <div className="selectWrapper">
        <label>Wybierz moduł oraz jego akcję</label>
        <select className="select" value={module_type} onChange={onTypeChange}>
          <option value="" disabled>Type…</option>
          {typeOptions.map((t) => (
            <option key={t} value={t}>{t}</option>
          ))}
        </select>
        <select
          className="select"
          value={id}
          disabled={(!module_type) || (!static_module_types[module_type]?.isIdNeeded)}
          onChange={(e) => setId(e.target.value)}
        >
          <option value="" disabled>ID…</option>
          {idOptions.map((m) => (
            <option key={m.id} value={m.id}>{m.id}</option>
          ))}
        </select>
        <select
          className="select"
          value={eventOrAction}
          disabled={!module_type}
          onChange={(e) => setEventOrAction(e.target.value)}
        >
          <option value="" disabled>Action</option>
          {
            EOAOptions.map((m) => (
              <option key={m} value={m}>{m}</option>
            ))
          }
        </select>
      </div>
    </div>
  )


}

export default OutputNode;
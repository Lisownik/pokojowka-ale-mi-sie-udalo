


import {useEffect, useState} from "react";
import {useModulesStore} from "../Models/modulesStorage";
import {static_module_types} from "../Models/ModuleTypes";
import LimitHandle from "../Handles/LimitHandle";
import {Position} from "@xyflow/react";

function InputNode({ data }) {
  const [module_type, setModule_type] = useState(data.module_type ?? "")
  const [id, setId] = useState(data.id ?? "")
  const [eventOrAction, setEventOrAction] = useState(data.eventOrAction ?? "")
  console.log("my input id", id);

  const modules = useModulesStore((s) => s.modules)

  const typeOptions = Object.keys(static_module_types).filter(
    (t) => static_module_types[t].events.length > 0
  );

  const idOptions = modules.filter((m) => m.type === module_type)

  const EOAOptions = static_module_types[module_type]?.events ?? [];


  const onTypeChange = (e) => {
    data['module_type'] = e.target.value
    setModule_type(e.target.value);
    data['id'] = null;
    setId("");
    data['eventOrAction'] = null;
    setEventOrAction("");
  };

  const onEventChange = (e) => {
    data['eventOrAction'] = e.target.value;
    setEventOrAction(e.target.value);
  }

  const onIdChange = (e) => {
    data['id'] = e.target.value;
    setId(e.target.value);
  }

  useEffect(() => {
    console.log("testing useeffect with modules input: ", modules, module_type, id)
    if (id && !modules.some((m) => m.id === id && m.type === module_type)) {
      // setId("");
    }
  }, [modules, id, module_type]);

  return (
    <>
      <LimitHandle type="source" position={Position.Right} connectioncount={1} />
      <p className="h2">EVENT</p>

      <div className="selectWrapper">
      <label>Wybierz moduł oraz jego event</label>
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
            onChange={(e) => onIdChange(e)}
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
        onChange={(e) => onEventChange(e)}
        >
          <option value="" disabled>Event</option>
        {
          EOAOptions.map((m) => (
            <option key={m} value={m}>{m}</option>
          ))
        }
      </select>
    </div>
    </>
  )


}

export default InputNode;
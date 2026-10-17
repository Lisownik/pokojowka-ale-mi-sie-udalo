import {useFetch} from "./api";
import { useEffect } from "react";
import {useModulesStore} from "../Models/modulesStorage";

export function useModules() {
  const { data, loading, error, refetch } = useFetch("/devices/module-info");
  const setModules = useModulesStore((s) => s.setModules);
  const setStatus = useModulesStore((s) => s.setStatus);

  useEffect(() => {
    if (error) {
      console.warn("Network err: ", error);
    }
    if (!loading && data) {
      console.log("jupi: ", data);
      console.log("Changing modules")
      setModules(data)
      setStatus("connected")
    }
  }, [data, loading, error, setModules, setStatus]);

  return { data, loading, error, refetch };
}
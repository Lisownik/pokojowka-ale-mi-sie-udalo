import { create } from "zustand";
import {modules} from "./ModuleTypes";

export const useModulesStore = create((set) => ({
  modules: modules,
  status: "connecting",

  setModules: (modules) => set({ modules }),
  upsertModule: (m) =>
    set((s) => ({
      modules: s.modules.some((x) => x.id === m.id)
        ? s.modules.map((x) => (x.id === m.id ? m : x))
        : [...s.modules, m],
    })),
  removeModule: (id) =>
    set((s) => ({ modules: s.modules.filter((x) => x.id !== id) })),
  setStatus: (status) => set({ status }),
}));
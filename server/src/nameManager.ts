import { readFile, writeFile } from "node:fs/promises";
import {DeviceData, DeviceNames, DeviceType} from "./types";


export class NameManager {
  private names: DeviceNames = {};
  private filepath: string = "./data/devices.json";
  constructor(
  ) {}

  async load(): Promise<void> {
    try {
      const data = await readFile(this.filepath, "utf8");
      const parsed: unknown = JSON.parse(data);

      this.names = parsed as DeviceNames;
    } catch (error: unknown) {
      if (
        error instanceof Error &&
        "code" in error &&
        error.code === "ENOENT"
      ) {
        this.names = {};
        await this.save();
        return;
      }

      throw error;
    }
  }

  private async save(): Promise<void> {
    await writeFile(
      this.filepath,
      JSON.stringify(this.names, null, 4) + "\n",
      "utf8"
    );
  }

  get(id: string): string {
    const entry = this.names[id];

    return entry?.name ?? "no-name";
  }

  has(id: string): boolean {
    return id in this.names;
  }

  async set(id: string, name: string, type: DeviceType): Promise<void> {
    const entry = this.names[id];

    if (entry) {
      entry.name = name;
      entry.type = type;
    } else {
      this.names[id] = {
          name,
          type
        }
    }

    await this.save();
  }

  getAll(): [string, DeviceData][] {
    return Object.entries(this.names)
  }
}

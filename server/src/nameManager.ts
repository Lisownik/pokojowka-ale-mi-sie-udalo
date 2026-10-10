import { readFile, writeFile } from "node:fs/promises";
import {DeviceNames} from "./types";


export class NameManager {
  private names: DeviceNames = [];
  private filepath: string = "./data/devices.json";
  constructor(
  ) {}

  async load(): Promise<void> {
    try {
      const data = await readFile(this.filepath, "utf8");
      const parsed: unknown = JSON.parse(data);

      if (!Array.isArray(parsed)) {
        throw new Error("devices.json must contain an array");
      }

      this.names = parsed as DeviceNames;
    } catch (error: unknown) {
      if (
        error instanceof Error &&
        "code" in error &&
        error.code === "ENOENT"
      ) {
        this.names = [];
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
    const entry = this.names.find((entry) =>
      Object.hasOwn(entry, id)
    );

    return entry?.[id] ?? "no-name";
  }

  has(id: string): boolean {
    return this.names.some((entry) =>
      Object.hasOwn(entry, id)
    );
  }

  async set(id: string, name: string): Promise<void> {
    const entry = this.names.find((entry) =>
      Object.hasOwn(entry, id)
    );

    if (entry) {
      entry[id] = name;
    } else {
      this.names.push({
        [id]: name,
      });
    }

    await this.save();
  }

  getAll(): DeviceNames {
    return [...this.names];
  }
}

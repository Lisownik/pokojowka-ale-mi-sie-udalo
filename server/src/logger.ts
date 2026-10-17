import { Request, Response, NextFunction } from "express";

export function requestLogger(req: Request, res: Response, next: NextFunction) {
  const start = process.hrtime.bigint();

  res.on("finish", () => {
    const ms = Number(process.hrtime.bigint() - start) / 1e6;
    console.log(
      `[${req.method}] ${req.originalUrl} ${res.statusCode} ${ms.toFixed(1)}ms - ${req.ip} at ${new Date().toISOString()}`
    );
  });

  next();
}
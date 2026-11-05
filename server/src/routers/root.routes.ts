import {Router} from "express";
import { notifications } from "../notifications";

export function rootRoutes() {
  const router: Router = Router();

  router.get("/", (req, res) => {
    res.status(200)
      .send("cześć świecie, witaj to ja verity");
  })

  router.get("/notifications", (req, res) => {
    res.status(200)
      .json(notifications)
  })

  return router;
}
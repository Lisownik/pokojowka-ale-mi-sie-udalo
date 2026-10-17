import {Router} from "express";

export function rootRoutes() {
  const router: Router = Router();

  router.get("/", (req, res) => {
    res.status(200)
      .send("cześć świecie, witaj to ja verity");
  })

  return router;
}
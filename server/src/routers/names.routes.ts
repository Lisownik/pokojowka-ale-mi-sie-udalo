import {NameManager} from "../nameManager";
import {Router} from "express";

export function namesRoutes(nameManager: NameManager){
  const router: Router = Router();

  router.get("/", (req, res) => {
    res.status(200)
      .json(
        nameManager.getAll()
      );
  })

  return router;
}
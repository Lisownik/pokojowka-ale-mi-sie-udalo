import { Router } from "express";
import {Environment} from "../environment";

export function developerRoutes(){
  const router: Router = Router();

  router.get("/exit", (req, res) => {
    if(!Environment.DEVELOPER_MODE){
      res.status(401).send("Not Authorized");

      return;
    }
    res.status(200).send({
      status: "exiting...",
    })

    process.exit(0);
  })

  return router;
}
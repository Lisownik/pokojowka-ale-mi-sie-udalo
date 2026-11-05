import { useEffect, useState } from "react";
import axios from "axios";

const BASE_URL = "http://localhost:1234"

export function useFetch(url) {
  const [data, setData] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  url = BASE_URL + url;

  useEffect(() => {
    setLoading(true);
    axios
      .get(url)
      .then((response) => {
        setData(response.data);
      })
      .catch((err) => {
        setError(err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [url]);

  const refetch = () => {
    setLoading(true);
    axios
      .get(url)
      .then((response) => {
        setData(response.data);
      })
      .catch((err) => {
        setError(err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return { data, loading, error, refetch };
}


export async function sendConnections(payload) {
  let url = BASE_URL + "/connections";
  const response = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      connections: JSON.stringify(payload),
    }),
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  return response;
}

export async function getConnections(signal) {
  let url = BASE_URL + "/action/all";
  const res = await fetch(url, {signal})
  if(!res.ok) throw new Error(`HTTP Request failed: ${res.status}`);
  const data = await res.json();

  return data;
}
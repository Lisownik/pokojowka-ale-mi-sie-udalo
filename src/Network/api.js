import { useEffect, useState } from "react";
import axios from "axios";

const BASE_URL = "http://localhost:3333"

function useFetch(url) {
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

export default useFetch;
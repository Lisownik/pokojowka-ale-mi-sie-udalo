const { Observable, timer } = rxjs;
const { retry } = rxjs.operators;

function createEventSource(path, host="localhost") {
  const topicUrl = `http://${host}:1234/stream/${path.trim()}`;

  return new Observable(observer => {
    const eventSource = new EventSource(topicUrl);

    eventSource.onmessage = event => {
      const messageData = JSON.parse(event.data);
      observer.next(messageData);
    }

    eventSource.onerror = event => {
      console.error(`Event source of ${path} struck an error: `, event);

      if (eventSource.readyState === EventSource.CLOSED) {
        observer.error(event);
      }
    }

    window.onbeforeunload = () => {
      eventSource.close()
    }

    return () => eventSource.close();
  }).pipe(
    retry({
      delay: (_error, retryCount) =>
        timer(Math.min(1000 * 2 ** (retryCount - 1), 30_000)),
      resetOnSuccess: true,
    })
  );
}
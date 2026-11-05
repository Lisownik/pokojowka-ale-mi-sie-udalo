import React from 'react';
import './index.css';
import App from './App';
import { createRoot } from 'react-dom/client';
import "bootstrap/dist/css/bootstrap.min.css";

import './index.css';

const container = document.querySelector('#app');
const root = createRoot(container);

root.render(<App />);
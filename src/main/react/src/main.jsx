import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './assets/styles/index.scss';
import App from './App.jsx'
import Router from "./Router.jsx";
import "./assets/beer/beer.min.css"
import "./assets/beer/beer.min.js"
import "./assets/beer/material-dynamic-colors.min.js"

import {setDefaultTheme} from "./components/ThemeButton.jsx";
setDefaultTheme(document.body)

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Router />
  </StrictMode>,
)
